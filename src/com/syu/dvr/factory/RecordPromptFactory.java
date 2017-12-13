package com.syu.dvr.factory;

import java.util.Timer;
import java.util.TimerTask;

import com.syu.dvr.widget.StateSignLayout;

public class RecordPromptFactory {
	
	
	private  MyTimerTask task;
	private  Timer timer;
	private boolean show;
	private static RecordPromptFactory factory;
	private StateSignLayout layout;
	public static RecordPromptFactory getInstance(){
		if (factory==null) {
			synchronized (RecordPromptFactory.class) {
				if (factory==null) {
					factory=new RecordPromptFactory();
				}
			}
		}
		return factory;
	}
	public void RunTask(StateSignLayout layout) {
		this.layout=layout;
		if (task!=null) {
			task.cancel();
			task=null;
		}
		timer=new Timer();
		task=new MyTimerTask();
		timer.schedule(task, 1, 1000);
	}
	public void stopTimer() {
		if (task!=null) {
			task.cancel();
			task=null;
		}
		show=false;
	}
	
	class MyTimerTask extends TimerTask{
		@Override
		public void run() {
			if (layout!=null) {
				show=!show;
				layout.showPrompt(show);
			}
		}
	}
	
}
