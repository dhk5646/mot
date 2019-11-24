package com.aks.mot.mvc.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class SampleController {

	@Value("${run.level}")
	private String runLevel;
	
	@Autowired
	private SampleService sampleService;
	
	/* *.do 호출부는 인터셉터를 통해 삭제 예정 */
	@RequestMapping("/sample.do")
	public String view() {
		
		log.info("/sample/view.do: " + runLevel);
		
		
		return "sample/sample"; 
	}
	
	/*Create, Read, Update, Delete*/
	@RequestMapping("/sample/read.crud")
	@ResponseBody
	public String read() {
		
		log.info("/SampleController/sample//read.crud: " + runLevel);
		
		return sampleService.read(runLevel);
	}
}
