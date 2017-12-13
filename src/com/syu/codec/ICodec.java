package com.syu.codec;

import com.syu.codec.Codec.TakePictureCallback;

public interface ICodec {
	//public void setType(int type);
	//��ʼ��
//	void setup(int width, int height, int fps, int format);
	public void start();
	public void pause();
	public void stop();
	/**
	 * ���գ������ʽpng
	 * @param path ��Ƭ����·��
	 * @param quality ѹ������  0-100
	 */
	public void takePicture(String path, int quality, TakePictureCallback callback);
}
