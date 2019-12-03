package com.aks.mot.mvc.smp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SampleService {
	
	private static Logger logger = LoggerFactory.getLogger(SampleService.class);
	
	@Resource
	SqlSession sqlSession;
	

	@Value("${run.level}")
	private String runLevel;
	
	public Map<String, Object> read(Map<String, Object> map) {
		
		long startSec = System.currentTimeMillis();
		
		logger.info("SampleService.read Start[" + runLevel +"]");
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		/*1*/
		SampleMapper sampleMapper = sqlSession.getMapper(SampleMapper.class);
		result.put("read", sampleMapper.read(runLevel));
		
		/*2*/
		//String a = sqlSession.selectOne("com.aks.mot.mvc.smp.SampleMapper.selectOne", runLevel);
		//List<Map<String, Object>> b = sqlSession.selectList("com.aks.mot.mvc.smp.SampleMapper.selectList", runLevel);
		
		//result.put("a", a);
		//result.put("b", b);
		
		
		
		
		long totSec = (System.currentTimeMillis() - startSec)/1000;
		
		logger.info("MvcController.doGet End [" + totSec + "]"  );
		return result;
	}
}