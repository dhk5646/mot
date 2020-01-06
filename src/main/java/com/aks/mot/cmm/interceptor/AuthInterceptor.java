
package com.aks.mot.cmm.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.aks.mot.cmm.annotation.Comment;
import com.aks.mot.cmm.util.PropertyUtil;


@Comment("미완료")
public class AuthInterceptor extends HandlerInterceptorAdapter {
	
	private static Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);
	
	private static String runLevel = PropertyUtil.getString("run.level");
	
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		logger.info("AuthInterceptor.preHandle Start[" + runLevel +"]");
		
		long startSec = System.currentTimeMillis();
		
		String strUri = request.getRequestURI();
		
		if(strUri.indexOf(".do") > -1) { // 화면호출인 경우 세션체크 후 권한체크
			if(strUri.indexOf("test") > -1) {
				return false;
			}else {
				return true;
			}
			
			
		} else if(strUri.indexOf(".mvc") > -1 && skipUrlCheck()) { // 서비스호출인 경우 세션체크
			
			
			return true;
		}
		
		long totSec = (System.currentTimeMillis() - startSec)/1000;
		
		logger.info("MvcController.doGet End [" + totSec + "]"  );
		
		return true;
	}
	
	public boolean skipUrlCheck() {
		
		boolean skipFlag = false;
		
		
		
		return skipFlag;
	}
}

