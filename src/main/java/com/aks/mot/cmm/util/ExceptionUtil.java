package com.aks.mot.cmm.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

public class ExceptionUtil {

	public static String getMessage(Exception e) {
		if(ExceptionUtils.getThrowableCount(e) > 1) {
			return e.getCause().getMessage().replace("\n", "");
		} else {
			String message = e.getMessage();
			if(StringUtils.isNotEmpty(message)) {
				return message.replace("\n", "");
			}
			return message;
		}
	}

	public static Exception getRootCause(Throwable throwable) {
		if(ExceptionUtils.getThrowableCount(throwable) > 1) {
			return (Exception) ExceptionUtils.getRootCause(throwable);
		} else {
			return (Exception) throwable;
		}
	}

	public static String getStackTrace(final Throwable e) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		e.printStackTrace(pw);
		return sw.getBuffer().toString();
	}

	public static String getSimpleStackTrace(Throwable ex) {
		String str = ExceptionUtil.getStackTrace(ex);
		String[] arr = str.split("\n");
		if(arr != null && arr.length > 2) {
			return arr[1].trim()+","+arr[2].trim();
		}
		return str;
	}
}
