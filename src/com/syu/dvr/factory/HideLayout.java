package com.syu.dvr.factory;

import java.util.Timer;
import java.util.TimerTask;

import com.syu.dvr.uihandle.MainActivityUiHandle;
import com.syu.dvr.utils.HandleConstant;

public class HideLayout {
	
	private int mCount=0;
	private  MyTimerTask task;
	private  Timer timer;
	
	public void RunTask() {
		if (task!=null) {
			task.cancel();
			task=null;
		}
		mCount=0;
		timer=new Timer(MainActivityUiHandle.class.getName());
		task=new MyTimerTask();
		timer.schedule(task, 1, 1000);
	}
	public void stopTimer() {
		if (task!=null) {
			task.cancel();
			task=null;
		}
		mCount=0;
	}
	public void hideLayout(int id){
		
	}
	class MyTimerTask extends TimerTask{
		@Override
		public void run() {
			mCount++;
			if (mCount==10) {
				hideLayout(HandleConstant.HIDE_MENU);
				stopTimer();
			}
		}
	}
}
