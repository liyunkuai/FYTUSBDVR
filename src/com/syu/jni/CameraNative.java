/************************************************************
  Copyright (C), 2012-2017, FYT Tech. Co., Ltd.
  FileName: touchnative.java
  Author: wusheng     Version: v1.0     Date:2015-11-5
  Description:    
  Version:       v1.0   
  Function List:   
    
   
 
***********************************************************/

package com.syu.jni;

import com.syu.codec.Utils;

public class CameraNative
{
	static 
	{
		if(Utils.DEBUG) {
			System.loadLibrary("fytcamera");
		}
	}
	public	static    native    int     native_open_camera(int id);//��ʱֻ֧��id = 0���Ժ���ܻ�������usb camera
	public	static    native    int     native_init_camera(int width, int height, int fps, int pixelformat); //����camera�Ŀ�͸��ԣ�֡���Լ�ͼ��ĸ�ʽ 1=MJPEG 0=YUV ��ʱĬ�϶�����Ϊ1���Ȳ���8328 dvr 
	public	static    native    int     native_start_preview( ); //��cameraԤ��
	public	static    native    byte[]    native_read_frame( ); //��ȡԤ����frame һ�η���һ֡����
	public	static    native    void    native_stop_preview( );//ֹͣԤ��
	public	static    native    void    native_close_camera( );//�ر�camera
	public	static    native    int    native_send_cmdto8328(byte[] send_data,byte[] recv_data);//�ر�camera
}
