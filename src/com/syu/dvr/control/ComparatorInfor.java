﻿package com.syu.dvr.control;

import java.util.Comparator;

import com.syu.dvr.module.DeleFileInfor;


public class ComparatorInfor implements Comparator {
	@Override
	public int compare(Object arg0, Object arg1) {
		DeleFileInfor infor1=(DeleFileInfor) arg0;
		DeleFileInfor infor2=(DeleFileInfor) arg1;
		int flag=infor1.getY_m_d().compareTo(infor2.getY_m_d());
		if(flag==0){
			return infor1.getH_m_s().compareTo(infor2.getH_m_s());
		}else{
		  return flag;
		}    
	}
}
