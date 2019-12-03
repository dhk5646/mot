package com.aks.mot.com.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.aks.mot.com.annotation.Comment;


@Comment("공통 예외처리 핸들러")
@ControllerAdvice

public class AksExceptionHandler {

	
	@ExceptionHandler(Throwable.class)
	@ResponseStatus (HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleThrowable(Throwable ex) {
		HashMap<String, String> rtnMap = new HashMap<String, String>();
		rtnMap.put("rtnCd", "-1");
		rtnMap.put("rtnMsg", ex.getMessage());
		
		ModelAndView mav = new ModelAndView(); 
		mav.addObject("rtnMap", rtnMap); 
		mav.setViewName("ajaxView"); 
		
		return mav;
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus (HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleException(Exception ex) {

	    //log.error("■■■■■■■■■■■■■■■■■■■■■ [StackTrace] Begin ■■■■■■■■■■■■■■■■■■■■■");
	    //log.error(getStackTrace(ex));
		//log.error("■■■■■■■■■■■■■■■■■■■■■ [StackTrace] E n d ■■■■■■■■■■■■■■■■■■■■■");
		
		String rtnMsg = ex.getMessage();
		/*
		 * if("".equals(SessionUtil.getUserId())) { rtnMsg = "[Session Expired] " +
		 * rtnMsg; } else { rtnMsg = "[Server Error] " + rtnMsg; }
		 */
		
		HashMap<String, String> rtnMap = new HashMap<String, String>();
		rtnMap.put("rtnCd", "-1");
		rtnMap.put("rtnMsg", rtnMsg);
		
		ModelAndView mav = new ModelAndView(); 
		mav.addObject("rtnMap", rtnMap); 
		mav.setViewName("ajaxView"); 
		
		return mav;
	}
	
	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus (HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleRuntimeException(RuntimeException ex) {

	    //log.error("■■■■■■■■■■■■■■■■■■■■■ [StackTrace] Begin ■■■■■■■■■■■■■■■■■■■■■");
	    //log.error(getStackTrace(ex));
		//log.error("■■■■■■■■■■■■■■■■■■■■■ [StackTrace] E n d ■■■■■■■■■■■■■■■■■■■■■");
		
		String rtnMsg = ex.getMessage();
		/*
		 * if("".equals(SessionUtil.getUserId())) { rtnMsg = "[Session Expired] " +
		 * rtnMsg; } else { rtnMsg = "[Server Error] " + rtnMsg; }
		 */
		
		HashMap<String, String> rtnMap = new HashMap<String, String>();
		rtnMap.put("rtnCd", "-1");
		rtnMap.put("rtnMsg", rtnMsg);
		
		ModelAndView mav = new ModelAndView(); 
		mav.addObject("rtnMap", rtnMap); 
		mav.setViewName("ajaxView"); 
		
		return mav;
	}
	
	/*
	 * @ExceptionHandler(BizException.class)
	 * 
	 * @ResponseStatus (HttpStatus.INTERNAL_SERVER_ERROR) public ModelAndView
	 * handleBizException(BizException ex) {
	 * 
	 * log.error("■■■■■■■■■■■■■■■■■■■■■ [StackTrace] Begin ■■■■■■■■■■■■■■■■■■■■■");
	 * log.error(getStackTrace(ex));
	 * log.error("■■■■■■■■■■■■■■■■■■■■■ [StackTrace] E n d ■■■■■■■■■■■■■■■■■■■■■");
	 * 
	 * String rtnMsg = ex.getMessage(); if("".equals(SessionUtil.getUserId())) {
	 * rtnMsg = "[Session Expired] " + rtnMsg; } else { rtnMsg = "[Server Error] " +
	 * rtnMsg; }
	 * 
	 * HashMap<String, String> rtnMap = new HashMap<String, String>();
	 * rtnMap.put("rtnCd", "-1"); rtnMap.put("rtnMsg", rtnMsg);
	 * 
	 * ModelAndView mav = new ModelAndView(); mav.addObject("rtnMap", rtnMap);
	 * mav.setViewName("ajaxView");
	 * 
	 * return mav; }
	 */

	public static String getStackTrace(final Throwable e) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		e.printStackTrace(pw);
		return sw.getBuffer().toString();
	}
	
}
