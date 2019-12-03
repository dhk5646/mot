package com.aks.mot.com.controller;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aks.mot.com.exception.MvcException;
import com.aks.mot.com.util.BeanUtil;


@SuppressWarnings("unchecked")
@RestController
public class MvcController {
	
	private static Logger logger = LoggerFactory.getLogger(MvcController.class);
	
	@RequestMapping(value = "/{path}/{service}/{method}.mvc")
	public Map<String, Object> doGet(HttpServletRequest reqeust, HttpServletResponse response, @PathVariable String service, @PathVariable String method) throws Exception {
		
		long startSec = System.currentTimeMillis();
		
		logger.info("MvcController.doGet Start");
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("reqeust", reqeust);
		map.put("response", response);
		
		Object bean = BeanUtil.getBean(service.concat("Service"));
		Method action = getMethod(bean, method);
		
		
		Map<String, Object> result = (Map<String, Object>) action.invoke(bean, map);
		
		long totSec = (System.currentTimeMillis() - startSec)/1000;
		
		logger.info("MvcController.doGet End [" + totSec + "]"  );
		
		return result;
	}

    public static Method getMethod(Object bean, String methodName) {
    	Method[] methods = bean.getClass().getMethods();
    	
    	for (int i = 0 ; i < methods.length; i ++) {
    		if(methods[i].getName().equals(methodName)) {
    			return methods[i];
    		}
    	}
    	throw new MvcException("can't find " + methodName + ".");
    }
}