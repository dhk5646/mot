package com.aks.mot.cmn.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class EncodingFilter implements Filter {
    
	@Override
    public void init(FilterConfig filterConfig) throws ServletException {
		//filter 생성 시 처리
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //다음 Filter 실행 전 처리 (preHandle)
        
        //다음 filter-chain에 대한 실행 (filter-chain의 마지막에는 Dispatcher servlet실행)
		filterChain.doFilter(servletRequest, servletResponse);
        
        //다음 Filter 실행 후 처리 (postHandle)
    }
    
    @Override
    public void destroy() {
		//filter 제거 시 처리 (보통 자원의 해제처리를 한다.)
    }
}
