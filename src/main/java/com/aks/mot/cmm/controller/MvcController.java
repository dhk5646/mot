package com.aks.mot.cmm.controller;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aks.mot.cmm.exception.MvcException;
import com.aks.mot.cmm.util.BeanUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@SuppressWarnings("unchecked")
@RestController
public class MvcController {
	
	private static Logger logger = LoggerFactory.getLogger(MvcController.class);
	
	@RequestMapping(value = "/{path}/{service}/{method}.mvc")
	public Map<String, Object> doGet(HttpServletRequest request, HttpServletResponse response, @PathVariable String service, @PathVariable String method) throws Exception {
		
		long startSec = System.currentTimeMillis();
		
		logger.info("MvcController.doGet Start");
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("headerParam", getParam(request.getParameter("headerParam")));
		map.put("inputParam" , getParam(request.getParameter("inputParam")));
		map.put("pagingParam", getParam(request.getParameter("pagingParam")));
		map.put("bodyParam", getParam(request.getParameter("bodyParam")));
		
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
    
    public static Map<String, Object> getParam(String param) throws JsonMappingException, JsonProcessingException {
    	
    	// 재사용 가능하고 전체코드에서 공유함. 
    	ObjectMapper mapper = new ObjectMapper();
		String bodyJson = "{\"school\" : \"kc\""
				   + ", \"students\" : [{\"name\":\"mkyong\", \"age\":37}, {\"name\":\"fong\", \"age\":38}]}";
	
	
		Map<String, Object> map = mapper.readValue(bodyJson, Map.class);
		System.out.println("map=>" + map.toString());
		
		List<Map<String, Object>> studentsMap = (List<Map<String, Object>>) map.get("students");
		
		System.out.println("name111 => " + studentsMap.get(0).get("name"));
		
    	return map;
    }
}