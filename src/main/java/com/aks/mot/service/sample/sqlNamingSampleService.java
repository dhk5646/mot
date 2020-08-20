package com.aks.mot.service.sample;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("smp/sqlNamingSampleService")
public class sqlNamingSampleService {
	
	private static Logger logger = LoggerFactory.getLogger(sqlNamingSampleService.class);
	
	@Resource
	SqlSession sqlSession;
	

	@Value("${run.level}")
	private String runLevel;
	
	public Map<String, Object> read(Map<String, Object> map) {
		
		/* sql 연동시 Name을 이용한 방법
		 *  
		 * */
		
		long startSec = System.currentTimeMillis();
		
		logger.info("SampleService.read1 Start[" + runLevel +"]");
		
		Map<String, Object> outputMap = new HashMap<String, Object>();
		
		String a = sqlSession.selectOne("smp.sqlNamingSampleService.test", runLevel);
		
		//result.put("a", a);
		//result.put("b", b);
		
		long totSec = (System.currentTimeMillis() - startSec)/1000;
		
		logger.info("MvcController.doGet End [" + totSec + "]"  );
		return outputMap;
	}
}