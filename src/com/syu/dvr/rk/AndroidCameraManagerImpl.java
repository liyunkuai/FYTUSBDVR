/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.syu.dvr.rk;


import java.io.IOException;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.AutoFocusMoveCallback;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.Camera.OnZoomChangeListener;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * A class to implement {@link CameraManager} of the Android camera framework.
 */
@SuppressLint("NewApi")
class AndroidCameraManagerImpl implements CameraManager {
    private static final String TAG = "CAM_" +
            AndroidCameraManagerImpl.class.getSimpleName();

    private Parameters[] mParameters;
    private boolean[] mParametersIsDirty;
    private IOException mReconnectIOException;

    /* Messages used in CameraHandler. */
    // Camera initialization/finalization
    private static final int OPEN_CAMERA = 1;
    private static final int RELEASE =     2;
    private static final int RECONNECT =   3;
    private static final int UNLOCK =      4;
    private static final int LOCK =        5;
    // Preview
    private static final int SET_PREVIEW_TEXTURE_ASYNC =        101;
    private static final int START_PREVIEW_ASYNC =              102;
    private static final int STOP_PREVIEW =                     103;
    private static final int SET_PREVIEW_CALLBACK_WITH_BUFFER = 104;
    private static final int ADD_CALLBACK_BUFFER =              105;
    private static final int SET_PREVIEW_DISPLAY_ASYNC =        106;
    private static final int SET_PREVIEW_CALLBACK =             107;
    // Parameters
    private static final int SET_PARAMETERS =     201;
    private static final int GET_PARAMETERS =     202;
    private static final int REFRESH_PARAMETERS = 203;
    // Focus, Zoom
    private static final int AUTO_FOCUS =                   301;
    private static final int CANCEL_AUTO_FOCUS =            302;
    private static final int SET_AUTO_FOCUS_MOVE_CALLBACK = 303;
    private static final int SET_ZOOM_CHANGE_LISTENER =     304;
    // Face detection
    private static final int SET_FACE_DETECTION_LISTENER = 461;
    private static final int START_FACE_DETECTION =        462;
    private static final int STOP_FACE_DETECTION =         463;
    private static final int SET_ERROR_CALLBACK =          464;
    // Presentation
    private static final int ENABLE_SHUTTER_SOUND =    501;
    private static final int SET_DISPLAY_ORIENTATION = 502;

    private CameraHandler mCameraHandler;
    private android.hardware.Camera[] mCamera;
    private int mNumberOfCameras;

    // Used to retain a copy of Parameters for setting parameters.
    private Parameters[] mParamsToSet = new Parameters[2];

    AndroidCameraManagerImpl() {
        HandlerThread ht = new HandlerThread("Camera Handler Thread");
        ht.start();
        mCameraHandler = new CameraHandler(ht.getLooper());
        mNumberOfCameras = android.hardware.Camera.getNumberOfCameras();
//        mParameters = new Parameters[mNumberOfCameras];
//        mParametersIsDirty = new boolean[mNumberOfCameras];
//        mCamera = new android.hardware.Camera[mNumberOfCameras];
        mParameters = new Parameters[CameraSettings.MAX_SUPPORT_CAMERAS];
        mParametersIsDirty = new boolean[CameraSettings.MAX_SUPPORT_CAMERAS];
        mCamera = new android.hardware.Camera[CameraSettings.MAX_SUPPORT_CAMERAS];
    }

    private class CameraHandler extends Handler {
        CameraHandler(Looper looper) {
            super(looper);
        }

        private void startFaceDetection(int id) {
            if(id > -1 && id < mCamera.length && mCamera[id] != null)
                mCamera[id].startFaceDetection();
        }

        private void stopFaceDetection(int id) {
            if(id > -1 && id < mCamera.length && mCamera[id] != null)
                mCamera[id].stopFaceDetection();
        }

        private void setFaceDetectionListener(FaceDetectionListener listener, int id) {
            if(id > -1 && id < mCamera.length && mCamera[id] != null)
                mCamera[id].setFaceDetectionListener(listener);
        }

        private void setPreviewTexture(Object surfaceTexture, int id) {
            try {
                if(id > -1 && id < mCamera.length && mCamera[id] != null)
                    mCamera[id].setPreviewTexture((SurfaceTexture) surfaceTexture);
            } catch (IOException e) {
                Log.e(TAG, "Could not set preview texture", e);
            }
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        private void enableShutterSound(boolean enable, int id) {
            if(id > -1 && id < mCamera.length && mCamera[id] != null)
                mCamera[id].enableShutterSound(enable);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        private void setAutoFocusMoveCallback(
                android.hardware.Camera camera, Object cb) {
            camera.setAutoFocusMoveCallback((AutoFocusMoveCallback) cb);
        }

        public void requestTakePicture(
                final ShutterCallback shutter,
                final PictureCallback raw,
                final PictureCallback postView,
                final PictureCallback jpeg,
                final int id) {
            post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(id > -1 && id < mCamera.length && mCamera[id] != null)
                            mCamera[id].takePicture(shutter, raw, postView, jpeg);
                    } catch (RuntimeException e) {
                        // TODO: output camera state and focus state for debugging.
                        Log.e(TAG, "take picture failed.");
                        throw e;
                    }
                }
            });
        }

        /**
         * Waits for all the {@code Message} and {@code Runnable} currently in the queue
         * are processed.
         *
         * @return {@code false} if the wait was interrupted, {@code true} otherwise.
         */
        public boolean waitDone() {
            final Object waitDoneLock = new Object();
            final Runnable unlockRunnable = new Runnable() {
                @Override
                public void run() {
                    synchronized (waitDoneLock) {
                        waitDoneLock.notifyAll();
                    }
                }
            };

            synchronized (waitDoneLock) {
                mCameraHandler.post(unlockRunnable);
                try {
                    waitDoneLock.wait();
                } catch (InterruptedException ex) {
                    Log.v(TAG, "waitDone interrupted");
                    return false;
                }
            }
            return true;
        }

        /**
         * This method does not deal with the API level check.  Everyone should
         * check first for supported operations before sending message to this handler.
         */
        @Override
        public void handleMessage(final Message msg) {
            Log.d(TAG, "handleMessage = " + msg.what);
            try {
                switch (msg.what) {
                    case OPEN_CAMERA:
                        Log.d(TAG, "Camera " + msg.arg1 +" open enter");
                        try {
                            mCamera[msg.arg1] = android.hardware.Camera.open(msg.arg1);
                        } catch (RuntimeException e) {
                            Log.w(TAG, "open camera " + msg.arg1 +" failed");
                        }
                        if (mCamera[msg.arg1] != null) {
                            mParametersIsDirty[msg.arg1] = true;

                            // Get a instance of Camera.Parameters for later use.
                            if (mParamsToSet[msg.arg1] == null) {
                                mParamsToSet[msg.arg1] = mCamera[msg.arg1].getParameters();
                            }
                        } else {
                            if (msg.obj != null) {
                                ((CameraOpenErrorCallback) msg.obj).onDeviceOpenFailure(msg.arg1);
                            }
                        }
                        Log.d(TAG, "Camera " + msg.arg1 +" open exist");
                        return;

                    case RELEASE:
                        Log.d(TAG, "Camera " + msg.arg1 +" release enter");
                        Log.i(TAG, "release camera " + msg.arg1);
                        mCamera[msg.arg1].release();
                        mCamera[msg.arg1] = null;
                        Log.d(TAG, "Camera " + msg.arg1 +" release exist");
                        return;

                    case RECONNECT:
                        Log.d(TAG, "Camera " + msg.arg1 +" reconnect enter");
                        mReconnectIOException = null;
                        try {
                            mCamera[msg.arg1].reconnect();
                        } catch (IOException ex) {
                            mReconnectIOException = ex;
                        }
                        Log.d(TAG, "Camera " + msg.arg1 +" reconnect exist");
                        return;

                    case UNLOCK:
                        Log.d(TAG, "Camera " + msg.arg1 +" unlock enter");
                        mCamera[msg.arg1].unlock();
                        Log.d(TAG, "Camera " + msg.arg1 +" unlock exist");
                        return;

                    case LOCK:
                        Log.d(TAG, "Camera " + msg.arg1 +" lock enter");
                        mCamera[msg.arg1].lock();
                        Log.d(TAG, "Camera " + msg.arg1 +" lock exist");
                        return;

                    case SET_PREVIEW_TEXTURE_ASYNC:
                        Log.d(TAG, "Camera " + msg.arg1 +" setPreviewTexture enter");
                        setPreviewTexture(msg.obj, msg.arg1);
                        Log.d(TAG, "Camera " + msg.arg1 +" setPreviewTexture exist");
                        return;

                    case SET_PREVIEW_DISPLAY_ASYNC:
                        try {
                            Log.d(TAG, "Camera " + msg.arg1 +" setPreviewDisplay enter");
                            mCamera[msg.arg1].setPreviewDisplay((SurfaceHolder) msg.obj);
                            Log.d(TAG, "Camera " + msg.arg1 +" setPreviewDisplay exist");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        return;

                    case START_PREVIEW_ASYNC:
                        Log.d(TAG, "Camera " + msg.arg1 +" startPreview enter");
                        mCamera[msg.arg1].startPreview();
                        if (msg.obj != null) {
                            ((CameraStartPreviewCallback) msg.obj).onPreviewStarted(msg.arg1);
                        }
                        Log.d(TAG, "Camera " + msg.arg1 +" startPreview exist");
                        return;

                    case STOP_PREVIEW:
                        Log.d(TAG, "Camera " + msg.arg1 +" stopPreview enter");
                        mCamera[msg.arg1].stopPreview();
                        Log.d(TAG, "Camera " + msg.arg1 +" stopPreview exist");
                        return;

                    case SET_PREVIEW_CALLBACK_WITH_BUFFER:
                        Log.d(TAG, "Camera " + msg.arg1 +" setPreviewCallbackWithBuffer enter");
                        mCamera[msg.arg1].setPreviewCallbackWithBuffer(
                            (PreviewCallback) msg.obj);
                        Log.d(TAG, "Camera " + msg.arg1 +" setPreviewCallbackWithBuffer exist");
                        return;

                    case ADD_CALLBACK_BUFFER:
                        mCamera[msg.arg1].addCallbackBuffer((byte[]) msg.obj);
                        return;

                    case AUTO_FOCUS:
                        Log.d(TAG, "Camera " + msg.arg1 +" autoFocus enter");
                        mCamera[msg.arg1].autoFocus((AutoFocusCallback) msg.obj);
                        Log.d(TAG, "Camera " + msg.arg1 +" autoFocus exist");
                        return;

                    case CANCEL_AUTO_FOCUS:
                        Log.d(TAG, "Camera " + msg.arg1 +" cancelAutoFocus enter");
                        mCamera[msg.arg1].cancelAutoFocus();
                        Log.d(TAG, "Camera " + msg.arg1 +" cancelAutoFocus exist");
                        return;

                    case SET_AUTO_FOCUS_MOVE_CALLBACK:
                        Log.d(TAG, "Camera " + msg.arg1 +" setAutoFocusMoveCallback enter");
                        setAutoFocusMoveCallback(mCamera[msg.arg1], msg.obj);
                        Log.d(TAG, "Camera " + msg.arg1 +" setAutoFocusMoveCallback exist");
                        return;

                    case SET_DISPLAY_ORIENTATION:
                        Log.d(TAG, "Camera " + msg.arg1 +" setDisplayOrientation enter");
                        mCamera[msg.arg2].setDisplayOrientation(msg.arg1);
                        Log.d(TAG, "Camera " + msg.arg1 +" setDisplayOrientation exist");
                        return;

                    case SET_ZOOM_CHANGE_LISTENER:
                        Log.d(TAG, "Camera " + msg.arg1 +" setZoomChangeListener enter");
                        mCamera[msg.arg1].setZoomChangeListener(
                            (OnZoomChangeListener) msg.obj);
                        Log.d(TAG, "Camera " + msg.arg1 +" setZoomChangeListener exist");
                        return;

                    case SET_FACE_DETECTION_LISTENER:
                        setFaceDetectionListener((FaceDetectionListener) msg.obj, msg.arg1);
                        return;

                    case START_FACE_DETECTION:
                        startFaceDetection(msg.arg1);
                        return;

                    case STOP_FACE_DETECTION:
                        stopFaceDetection(msg.arg1);
                        return;

                    case SET_ERROR_CALLBACK:
                        mCamera[msg.arg1].setErrorCallback((ErrorCallback) msg.obj);
                        return;

                    case SET_PARAMETERS:
                        try {
                            Log.d(TAG, "Camera " + msg.arg1 + " setParameters enter");
                            mParametersIsDirty[msg.arg1] = true;
                            mParamsToSet[msg.arg1].unflatten((String) msg.obj);
                            mCamera[msg.arg1].setParameters(mParamsToSet[msg.arg1]);
                            Log.d(TAG, "Camera " + msg.arg1 + " setParameters exist");
                        } catch (RuntimeException e) {
                            Log.w(TAG, "camera " + msg.arg1 + " setParameters occur RuntimeException");
                        }
                        return;

                    case GET_PARAMETERS:
                        if (mParametersIsDirty[msg.arg1]) {
                            try {
                                Log.d(TAG, "Camera " + msg.arg1 +" getParameters enter");
                                mParameters[msg.arg1] = mCamera[msg.arg1].getParameters();
                                mParametersIsDirty[msg.arg1] = false;
                                Log.d(TAG, "Camera " + msg.arg1 +" getParameters exist");
                            } catch (RuntimeException e) {
                                Log.w(TAG, "camera " + msg.arg1 + " getParameters occur RuntimeException");
                            }
                        }
                        return;

                    case SET_PREVIEW_CALLBACK:
                        Log.d(TAG, "Camera " + msg.arg1 +" setPreviewCallback enter");
                        mCamera[msg.arg1].setPreviewCallback((PreviewCallback) msg.obj);
                        Log.d(TAG, "Camera " + msg.arg1 +" setPreviewCallback exist");
                        return;

                    case ENABLE_SHUTTER_SOUND:
                        enableShutterSound((msg.arg1 == 1) ? true : false, msg.arg2);
                        return;

                    case REFRESH_PARAMETERS:
                        mParametersIsDirty[msg.arg1] = true;
                        return;

                    default:
                        throw new RuntimeException("Invalid CameraProxy message=" + msg.what);
                }
            } catch (RuntimeException e) {
                for (int i = 0; i < mCamera.length; i++) {
                    if (msg.what != RELEASE && mCamera[i] != null) {
                        try {
                            mCamera[i].release();
                        } catch (Exception ex) {
                            Log.e(TAG, "Fail to release the camera.");
                        }
                        mCamera[i] = null;
                    } else if (mCamera[i] == null) {
                      Log.w(TAG, "Cannot handle message, mCamera is null.");
                      continue;
                    }
                }
                throw e;
            }
        }
    }

    @Override
    public CameraManager.CameraProxy cameraOpen(
        Handler handler, int cameraId, CameraOpenErrorCallback callback) {
        mNumberOfCameras = android.hardware.Camera.getNumberOfCameras();
        mCameraHandler.obtainMessage(OPEN_CAMERA, cameraId, 0,
                CameraOpenErrorCallbackForward.getNewInstance(
                        handler, callback)).sendToTarget();
        mCameraHandler.waitDone();
        if (mCamera[cameraId] != null) {
            return new AndroidCameraProxyImpl(cameraId);
        } else {
            return null;
        }
    }

    /**
     * A class which implements {@link CameraManager.CameraProxy} and
     * camera handler thread.
     * TODO: Save the handler for the callback here to avoid passing the same
     * handler multiple times.
     */
    public class AndroidCameraProxyImpl implements CameraManager.CameraProxy {
        public int mId = -1;
        private AndroidCameraProxyImpl(int id) {
            mId = id;
        }

        @Override
        public android.hardware.Camera getCamera() {
            return mCamera[mId];
        }

        @Override
        public void release() {
            // release() must be synchronous so we know exactly when the camera
            // is released and can continue on.
            //mCameraHandler.sendEmptyMessage(RELEASE);
            mCameraHandler.obtainMessage(RELEASE, mId, 0).sendToTarget();
            mCameraHandler.waitDone();
        }

        @Override
        public boolean reconnect(Handler handler, CameraOpenErrorCallback cb) {
            //mCameraHandler.sendEmptyMessage(RECONNECT);
            mCameraHandler.obtainMessage(RECONNECT, mId, 0).sendToTarget();
            mCameraHandler.waitDone();
            CameraOpenErrorCallback cbforward =
                    CameraOpenErrorCallbackForward.getNewInstance(handler, cb);
            if (mReconnectIOException != null) {
                if (cbforward != null) {
                    cbforward.onReconnectionFailure(AndroidCameraManagerImpl.this);
                }
                return false;
            }
            return true;
        }

        @Override
        public void unlock() {
            //mCameraHandler.sendEmptyMessage(UNLOCK);
            mCameraHandler.obtainMessage(UNLOCK, mId, 0).sendToTarget();
            mCameraHandler.waitDone();
        }

        @Override
        public void lock() {
            mCameraHandler.obtainMessage(LOCK, mId, 0).sendToTarget();
            //mCameraHandler.sendEmptyMessage(LOCK);
        }

        @Override
        public void setPreviewTexture(SurfaceTexture surfaceTexture) {
            mCameraHandler.obtainMessage(SET_PREVIEW_TEXTURE_ASYNC, mId, 0, surfaceTexture).sendToTarget();
        }

        @Override
        public void setPreviewDisplay(SurfaceHolder surfaceHolder) {
            mCameraHandler.obtainMessage(SET_PREVIEW_DISPLAY_ASYNC, mId, 0, surfaceHolder).sendToTarget();
        }

        public void setPreviewDisplay() {
            mCameraHandler.obtainMessage(SET_PREVIEW_DISPLAY_ASYNC, mId, 0, CameraHolder.instance().getHolder(mId)).sendToTarget();
        }
        @Override
        public void startPreview(Handler handler, CameraStartPreviewCallback cb) {
            mCameraHandler.obtainMessage(START_PREVIEW_ASYNC, mId, 0,
                    CameraStartPreviewCallbackForward.getNewInstance(handler, cb)).sendToTarget();
            //mCameraHandler.obtainMessage(START_PREVIEW_ASYNC, mId, 0).sendToTarget();
            //mCameraHandler.sendEmptyMessage(START_PREVIEW_ASYNC);
        }

        @Override
        public void stopPreview() {
            mCameraHandler.obtainMessage(STOP_PREVIEW, mId, 0).sendToTarget();
            //mCameraHandler.sendEmptyMessage(STOP_PREVIEW);
            mCameraHandler.waitDone();
        }

        @Override
        public void setPreviewDataCallback(
                Handler handler, CameraPreviewDataCallback cb) {
            mCameraHandler.obtainMessage(
                    SET_PREVIEW_CALLBACK, mId, 0,
                    PreviewCallbackForward.getNewInstance(handler, this, cb)).sendToTarget();
        }

        @Override
        public void setPreviewDataCallbackWithBuffer(
                Handler handler, CameraPreviewDataCallback cb) {
            mCameraHandler.obtainMessage(
                    SET_PREVIEW_CALLBACK_WITH_BUFFER, mId, 0,
                    PreviewCallbackForward.getNewInstance(handler, this, cb)).sendToTarget();
        }

        @Override
        public void addCallbackBuffer(byte[] callbackBuffer) {
            mCameraHandler.obtainMessage(ADD_CALLBACK_BUFFER, mId, 0, callbackBuffer).sendToTarget();
        }

        @Override
        public void autoFocus(Handler handler, CameraAFCallback cb) {
            mCameraHandler.obtainMessage(
                    AUTO_FOCUS, mId, 0,
                    AFCallbackForward.getNewInstance(handler, this, cb)).sendToTarget();
        }

        @Override
        public void cancelAutoFocus() {
            mCameraHandler.removeMessages(AUTO_FOCUS);
            mCameraHandler.obtainMessage(CANCEL_AUTO_FOCUS, mId, 0).sendToTarget();
            //mCameraHandler.sendEmptyMessage(CANCEL_AUTO_FOCUS);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void setAutoFocusMoveCallback(
                Handler handler, CameraAFMoveCallback cb) {
            mCameraHandler.obtainMessage(
                    SET_AUTO_FOCUS_MOVE_CALLBACK, mId, 0,
                    AFMoveCallbackForward.getNewInstance(handler, this, cb)).sendToTarget();
        }

        @Override
        public void takePicture(
                Handler handler,
                CameraShutterCallback shutter,
                CameraPictureCallback raw,
                CameraPictureCallback post,
                CameraPictureCallback jpeg) {
            mCameraHandler.requestTakePicture(
                    ShutterCallbackForward.getNewInstance(handler, this, shutter),
                    PictureCallbackForward.getNewInstance(handler, this, raw),
                    PictureCallbackForward.getNewInstance(handler, this, post),
                    PictureCallbackForward.getNewInstance(handler, this, jpeg),
                    mId);
        }

        @Override
        public void setDisplayOrientation(int degrees) {
            mCameraHandler.obtainMessage(SET_DISPLAY_ORIENTATION, degrees, mId)
                    .sendToTarget();
        }

        @Override
        public void setZoomChangeListener(OnZoomChangeListener listener) {
            mCameraHandler.obtainMessage(SET_ZOOM_CHANGE_LISTENER, mId, 0, listener).sendToTarget();
        }

        public void setFaceDetectionCallback(
                Handler handler, CameraFaceDetectionCallback cb) {
            mCameraHandler.obtainMessage(
                    SET_FACE_DETECTION_LISTENER, mId, 0,
                    FaceDetectionCallbackForward.getNewInstance(handler, this, cb)).sendToTarget();
        }

        @Override
        public void startFaceDetection() {
            mCameraHandler.obtainMessage(START_FACE_DETECTION, mId, 0).sendToTarget();
            //mCameraHandler.sendEmptyMessage(START_FACE_DETECTION);
        }

        @Override
        public void stopFaceDetection() {
            mCameraHandler.obtainMessage(STOP_FACE_DETECTION, mId, 0).sendToTarget();
            mCameraHandler.sendEmptyMessage(STOP_FACE_DETECTION);
        }

        @Override
        public void setErrorCallback(ErrorCallback cb) {
            mCameraHandler.obtainMessage(SET_ERROR_CALLBACK, mId, 0, cb).sendToTarget();
        }

        @Override
        public void setParameters(Parameters params) {
            if (params == null) {
                Log.v(TAG, "null parameters in setParameters()");
                return;
            }
            mCameraHandler.obtainMessage(SET_PARAMETERS, mId, 0, params.flatten())
                    .sendToTarget();
        }

        @Override
        public Parameters getParameters() {
            mCameraHandler.obtainMessage(GET_PARAMETERS, mId, 0).sendToTarget();
            //mCameraHandler.sendEmptyMessage(GET_PARAMETERS);
            mCameraHandler.waitDone();
            return mParameters[mId];
        }

        @Override
        public void refreshParameters() {
            mCameraHandler.obtainMessage(REFRESH_PARAMETERS, mId, 0).sendToTarget();
            //mCameraHandler.sendEmptyMessage(REFRESH_PARAMETERS);
        }

        @Override
        public void enableShutterSound(boolean enable) {
            mCameraHandler.obtainMessage(
                    ENABLE_SHUTTER_SOUND, (enable ? 1 : 0), mId).sendToTarget();
        }
    }

    /**
     * A helper class to forward AutoFocusCallback to another thread.
     */
    private static class AFCallbackForward implements AutoFocusCallback {
        private final Handler mHandler;
        private final CameraProxy mCamera;
        private final CameraAFCallback mCallback;

        /**
         * Returns a new instance of {@link AFCallbackForward}.
         *
         * @param handler The handler in which the callback will be invoked in.
         * @param camera  The {@link CameraProxy} which the callback is from.
         * @param cb      The callback to be invoked.
         * @return        The instance of the {@link AFCallbackForward},
         *                or null if any parameter is null.
         */
        public static AFCallbackForward getNewInstance(
                Handler handler, CameraProxy camera, CameraAFCallback cb) {
            if (handler == null || camera == null || cb == null) return null;
            return new AFCallbackForward(handler, camera, cb);
        }

        private AFCallbackForward(
                Handler h, CameraProxy camera, CameraAFCallback cb) {
            mHandler = h;
            mCamera = camera;
            mCallback = cb;
        }

        @Override
        public void onAutoFocus(final boolean b, Camera camera) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onAutoFocus(b, mCamera);
                }
            });
        }
    }

    /** A helper class to forward AutoFocusMoveCallback to another thread. */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static class AFMoveCallbackForward implements AutoFocusMoveCallback {
        private final Handler mHandler;
        private final CameraAFMoveCallback mCallback;
        private final CameraProxy mCamera;

        /**
         * Returns a new instance of {@link AFMoveCallbackForward}.
         *
         * @param handler The handler in which the callback will be invoked in.
         * @param camera  The {@link CameraProxy} which the callback is from.
         * @param cb      The callback to be invoked.
         * @return        The instance of the {@link AFMoveCallbackForward},
         *                or null if any parameter is null.
         */
        public static AFMoveCallbackForward getNewInstance(
                Handler handler, CameraProxy camera, CameraAFMoveCallback cb) {
            if (handler == null || camera == null || cb == null) return null;
            return new AFMoveCallbackForward(handler, camera, cb);
        }

        private AFMoveCallbackForward(
                Handler h, CameraProxy camera, CameraAFMoveCallback cb) {
            mHandler = h;
            mCamera = camera;
            mCallback = cb;
        }

        @Override
        public void onAutoFocusMoving(
                final boolean moving, android.hardware.Camera camera) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onAutoFocusMoving(moving, mCamera);
                }
            });
        }
    }

    /**
     * A helper class to forward ShutterCallback to to another thread.
     */
    private static class ShutterCallbackForward implements ShutterCallback {
        private final Handler mHandler;
        private final CameraShutterCallback mCallback;
        private final CameraProxy mCamera;

        /**
         * Returns a new instance of {@link ShutterCallbackForward}.
         *
         * @param handler The handler in which the callback will be invoked in.
         * @param camera  The {@link CameraProxy} which the callback is from.
         * @param cb      The callback to be invoked.
         * @return        The instance of the {@link ShutterCallbackForward},
         *                or null if any parameter is null.
         */
        public static ShutterCallbackForward getNewInstance(
                Handler handler, CameraProxy camera, CameraShutterCallback cb) {
            if (handler == null || camera == null || cb == null) return null;
            return new ShutterCallbackForward(handler, camera, cb);
        }

        private ShutterCallbackForward(
                Handler h, CameraProxy camera, CameraShutterCallback cb) {
            mHandler = h;
            mCamera = camera;
            mCallback = cb;
        }

        @Override
        public void onShutter() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onShutter(mCamera);
                }
            });
        }
    }

    /**
     * A helper class to forward PictureCallback to another thread.
     */
    private static class PictureCallbackForward implements PictureCallback {
        private final Handler mHandler;
        private final CameraPictureCallback mCallback;
        private final CameraProxy mCamera;

        /**
         * Returns a new instance of {@link PictureCallbackForward}.
         *
         * @param handler The handler in which the callback will be invoked in.
         * @param camera  The {@link CameraProxy} which the callback is from.
         * @param cb      The callback to be invoked.
         * @return        The instance of the {@link PictureCallbackForward},
         *                or null if any parameters is null.
         */
        public static PictureCallbackForward getNewInstance(
                Handler handler, CameraProxy camera, CameraPictureCallback cb) {
            if (handler == null || camera == null || cb == null) return null;
            return new PictureCallbackForward(handler, camera, cb);
        }

        private PictureCallbackForward(
                Handler h, CameraProxy camera, CameraPictureCallback cb) {
            mHandler = h;
            mCamera = camera;
            mCallback = cb;
        }

        @Override
        public void onPictureTaken(
                final byte[] data, android.hardware.Camera camera) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onPictureTaken(data, mCamera);
                }
            });
        }
    }

    /**
     * A helper class to forward PreviewCallback to another thread.
     */
    private static class PreviewCallbackForward implements PreviewCallback {
        private final Handler mHandler;
        private final CameraPreviewDataCallback mCallback;
        private final CameraProxy mCamera;

        /**
         * Returns a new instance of {@link PreviewCallbackForward}.
         *
         * @param handler The handler in which the callback will be invoked in.
         * @param camera  The {@link CameraProxy} which the callback is from.
         * @param cb      The callback to be invoked.
         * @return        The instance of the {@link PreviewCallbackForward},
         *                or null if any parameters is null.
         */
        public static PreviewCallbackForward getNewInstance(
                Handler handler, CameraProxy camera, CameraPreviewDataCallback cb) {
            if (handler == null || camera == null || cb == null) return null;
            return new PreviewCallbackForward(handler, camera, cb);
        }

        private PreviewCallbackForward(
                Handler h, CameraProxy camera, CameraPreviewDataCallback cb) {
            mHandler = h;
            mCamera = camera;
            mCallback = cb;
        }

        @Override
        public void onPreviewFrame(
                final byte[] data, android.hardware.Camera camera) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onPreviewFrame(data, mCamera);
                }
            });
        }
    }
    
    
    
    private static class FaceDetectionCallbackForward implements FaceDetectionListener {
        private final Handler mHandler;
        private final CameraFaceDetectionCallback mCallback;
        private final CameraProxy mCamera;

        /**
         * Returns a new instance of {@link FaceDetectionCallbackForward}.
         *
         * @param handler The handler in which the callback will be invoked in.
         * @param camera  The {@link CameraProxy} which the callback is from.
         * @param cb      The callback to be invoked.
         * @return        The instance of the {@link FaceDetectionCallbackForward},
         *                or null if any parameter is null.
         */
        public static FaceDetectionCallbackForward getNewInstance(
                Handler handler, CameraProxy camera, CameraFaceDetectionCallback cb) {
            if (handler == null || camera == null || cb == null) return null;
            return new FaceDetectionCallbackForward(handler, camera, cb);
        }

        private FaceDetectionCallbackForward(
                Handler h, CameraProxy camera, CameraFaceDetectionCallback cb) {
            mHandler = h;
            mCamera = camera;
            mCallback = cb;
        }

        @Override
        public void onFaceDetection(
                final Camera.Face[] faces, Camera camera) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onFaceDetection(faces, mCamera);
                }
            });
        }
    }

    /**
     * A callback helps to invoke the original callback on another
     * {@link android.os.Handler}.
     */
    private static class CameraOpenErrorCallbackForward implements CameraOpenErrorCallback {
        private final Handler mHandler;
        private final CameraOpenErrorCallback mCallback;

        /**
         * Returns a new instance of {@link FaceDetectionCallbackForward}.
         *
         * @param handler The handler in which the callback will be invoked in.
         * @param cb The callback to be invoked.
         * @return The instance of the {@link FaceDetectionCallbackForward}, or
         *         null if any parameter is null.
         */
        public static CameraOpenErrorCallbackForward getNewInstance(
                Handler handler, CameraOpenErrorCallback cb) {
            if (handler == null || cb == null) {
                return null;
            }
            return new CameraOpenErrorCallbackForward(handler, cb);
        }

        private CameraOpenErrorCallbackForward(
                Handler h, CameraOpenErrorCallback cb) {
            mHandler = h;
            mCallback = cb;
        }

        @Override
        public void onCameraDisabled(final int cameraId) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onCameraDisabled(cameraId);
                }
            });
        }

        @Override
        public void onDeviceOpenFailure(final int cameraId) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onDeviceOpenFailure(cameraId);
                }
            });
        }

        @Override
        public void onReconnectionFailure(final CameraManager mgr) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onReconnectionFailure(mgr);
                }
            });
        }
    }

    private static class CameraStartPreviewCallbackForward implements CameraStartPreviewCallback {
        private final Handler mHandler;
        private final CameraStartPreviewCallback mCallback;

        /**
         * Returns a new instance of {@link FaceDetectionCallbackForward}.
         *
         * @param handler The handler in which the callback will be invoked in.
         * @param cb The callback to be invoked.
         * @return The instance of the {@link FaceDetectionCallbackForward}, or
         *         null if any parameter is null.
         */
        public static CameraStartPreviewCallbackForward getNewInstance(
                Handler handler, CameraStartPreviewCallback cb) {
            if (handler == null || cb == null) {
                return null;
            }
            return new CameraStartPreviewCallbackForward(handler, cb);
        }

        private CameraStartPreviewCallbackForward(
                Handler h, CameraStartPreviewCallback cb) {
            mHandler = h;
            mCallback = cb;
        }

        @Override
        public void onPreviewStarted(final int cameraId) {
            // TODO Auto-generated method stub
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onPreviewStarted(cameraId);
                }
            });
        }
    }
}
