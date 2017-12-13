package com.syu.dvr.server;

import com.syu.dvr.TheApp;
import com.syu.dvr.control.FytDvrTypeControl;
import com.syu.dvr.factory.UpSystemTimeForCamera;
import com.syu.dvr.upgrade.UpgradeManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
public class BootReceiver extends BroadcastReceiver{
	
	 private  String ACTION_DATA = "android.intent.action.DATE_CHANGED";
	 public static  String ACTION_UPGRADE="com.syu.dvr.networkupgrade";
	 private String SHOW_NOTIFICATION="android.com.syu.dvr.action.SHOW_NOTIFICATION";
	@Override
	public void onReceive(Context context, Intent intent) {
		
		if (intent==null) {
			return;
		}
		String action=intent.getAction();
		if (isUpSystemTime(action)&&FytDvrTypeControl.getInstance().getmCManager()!=null) {
			UpSystemTimeForCamera.getInstance().RunTask();
		}else if (!action.isEmpty()&&action.contains(ACTION_UPGRADE)) {
			TheApp.mApp.mHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					UpgradeManager.getInstance().netWordCheck();
				}
			}, 500);
		}
	}
	private boolean isUpSystemTime(String action){
		if (action.isEmpty()) {
			return false;
		}
		if (action.equals(ACTION_DATA)) {
			return true;
		}
		if (action.equals("android.intent.action.TIME_SET")) {
			return true;
		}
		if (action.equals(Intent.ACTION_TIME_CHANGED)) {
			return true;
		}
		if (action.equals(Intent.ACTION_CONFIGURATION_CHANGED)) {
			return true;
		}
		return false;
	}
	
}
