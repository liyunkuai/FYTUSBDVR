package com.syu.dvr.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.util.Log;

public class LogCatUtils {
	private static boolean IS_DEBUG=false;
	static{
		File file=new File("/sdcard", "dvrdebug");
		if (file.exists()) {
			IS_DEBUG=true;
		}
	}
	public static void showString(String msg){
		if (IS_DEBUG) {
				StackTraceElement mSte = new Exception().getStackTrace()[1];
				Log.i("log",mSte.getFileName()+"|line: " + mSte.getLineNumber() + " ---> " + msg);
				writeSdFile(mSte.getFileName()+"|line: " + mSte.getLineNumber() + " ---> " + msg);
				LogPreview.show(mSte.getFileName()+"|line: " + mSte.getLineNumber() + " ---> " + msg);
		}
	}
	private static void writeSdFile(String write_str) {
    	FileWriter writer = null;
		try {
			StackTraceElement mSte = new Exception().getStackTrace()[1];
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
			writer = new FileWriter("/sdcard"+ File.separator + "DVRLOG记录.txt", true);
			writer.write(sdf.format(new Date())+": "+mSte.getFileName()+"|line: " + mSte.getLineNumber() +write_str);
			writer.write("\r\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (writer!=null) {
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
