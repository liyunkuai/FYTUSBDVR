package com.syu.dvr.control;

public abstract class MultimediaControl {
	public MultimediaControl(CameraManager manager){};
	public abstract void shutter();
	public abstract void startRecord();
	public abstract void lock();
	public abstract void dvrSetting();
	public abstract void playblack();
	public abstract void dvrHelp();
	
}
