package com.aks.mot.com.config;

import javax.servlet.Filter;
import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.aks.mot.com.annotation.Comment;

/** 다음 설정은 web.xml 의 아래 내용과 동일하다
 * 다만 ContextLoadListner 및 DispatcherServlet 설정은 AbstractAnnotationConfigDispatcherServletInitializer 추상클래스에서 구현되어있음
 * <?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee 
http://xmlns.jcp.org/xml/ns/javaee/webapp_3_1.xsd" version="3.1">
    <display-name>Hello World Xml Config</display-name>
    
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>
            classpath:applicationContext.xml
        </param-value>
    </context-param>
    
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>
    
    <servlet>
        <servlet-name>dispatcher</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:servletContext.xml</param-value>
        </init-param>
    </servlet>
    
    <servlet-mapping>
        <servlet-name>dispatcher</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>
    
    <filter>
        <filter-name>encodingFilter</filter-name>
        <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
        <init-param>
        <param-name>encoding</param-name>
        <param-value>UTF-8</param-value>
        </init-param>
    </filter>
    
    <filter-mapping>
        <filter-name>encodingFilter</filter-name>
        <servlet-name>dispatcher</servlet-name>
    </filter-mapping>
</web-app>
 * */
@Comment("web.xml Role")
@Configuration
public class WebConfig extends AbstractAnnotationConfigDispatcherServletInitializer{

	private static Logger loger = LoggerFactory.getLogger(WebConfig.class);
	
	@Comment("application-context.xml Config")
	@Override
	protected Class<?>[] getRootConfigClasses() {
		loger.info("WebConfig.getRootConfigClasses()");
		
		return new Class[] {ApplicationConfig.class};
	}
	
	@Comment("servlet-context.xml Config")
	@Override
	protected Class<?>[] getServletConfigClasses() { 
		logger.info("WebConfig.getServletConfigClasses()");
		
		return new Class[] {ServletConfig.class};
	}
	
	@Comment("Servlet Mapping")
	@Override
	protected String[] getServletMappings() {
		logger.info("WebConfig.getServletMappings()");
		
		return new String[] {"*.do", "*.mvc"};
	}
	
	@Comment("Filter Config")
	@Override
    protected Filter[] getServletFilters() {
		logger.info("WebConfig.getServletFilters()");
		CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter("UTF-8", true, true);
        return new Filter[]{ encodingFilter };
    }
	
	/*  설정*/
	@Comment("Customizing Config Q. 확인필요")
	@Override
	protected void customizeRegistration(ServletRegistration.Dynamic registration) {
		logger.info("WebConfig.customizeRegistration()");
		
		/* 각종 커스터마이징이 필요한 것들을 선언한다.*/
		//registration.setMultipartConfig(new MultipartConfigElement("/tmp/spittr/uploads",2097152,419434,0));
		//registration.setInitParameter("throwExceptionIfNoHandlerFound", "true");
	}
	
}
