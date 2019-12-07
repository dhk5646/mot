package com.aks.mot.cmn.util;

import java.util.ResourceBundle;

public class PropertyUtil {

	private static ResourceBundle common;
	
	static {
		if ("prd".equals(System.getProperty("spring.profiles.active"))){
			common = ResourceBundle.getBundle("application-prd");
		} else if ("dev".equals(System.getProperty("spring.profiles.active"))){
			common = ResourceBundle.getBundle("application-dev");
		} else {
			common = ResourceBundle.getBundle("application");
		}
	}
	
	public static String getString(String key) {
		String rtnVal = "";
		try {
			rtnVal = common.getString(key);
		} catch(Exception ex) {
			
		}
		
		return rtnVal;
	}
	
	public static int getInt(String key) {
		String rtnVal = "";
		try {
			rtnVal = common.getString(key);
		} catch(Exception ex) {
			
		}
		return Integer.parseInt(rtnVal);
	}
	
	public static String getString(String key, String defaultVal) {
		String rtnVal = "";
		try {
			rtnVal = common.getString(key);
		} catch(Exception ex) {
			
		}
		
		if(rtnVal == null || "".equals(rtnVal)) {
			return defaultVal;
		}
		
		return rtnVal;		
	}
	
}
