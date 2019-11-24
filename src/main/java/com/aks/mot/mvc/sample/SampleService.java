package com.aks.mot.mvc.sample;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SampleService {
	
	@Autowired
	SqlSession sqlSession;
	
	public String read(String input) {
		
		log.info("/SampleService/sample//read.crud: " + input);
		
		SampleMapper sampleMapper = sqlSession.getMapper(SampleMapper.class);
	
		return sampleMapper.read(input);
		
		 
	}
}