package com.aks.mot.cmm.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class JspController {
	
	
	@RequestMapping(value = "/**/*.do")
	public ModelAndView doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
        String strUri = request.getRequestURI();
		ModelAndView mav = new ModelAndView();
		mav.setViewName(strUri.replace(".do", ""));
		
		return mav;
	}
	
}
