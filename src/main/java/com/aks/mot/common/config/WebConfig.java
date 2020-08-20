package com.aks.mot.common.config;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

/** 
 * WebApplicationInitializer 인터페이스를 이용하여 구현하였다.
 * AbstractAnnotationConfigDispatcherServletInitializer  (spring 3.2(?) 버전부터 존재)
 *  차이점 -> ContextLoadListner 및 DispatcherServlet 설정은 추상클래스에서 구현되어있기때문에 별도 설정하지 않아도됨.
 *  
 *  다음 설정은 web.xml 의 아래 내용과 동일하다
 * <?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee 
http://xmlns.jcp.org/xml/ns/javaee/webapp_3_1.xsd" version="3.1">
    <display-name>Hello World Xml Config</display-name>
    
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>
            classpath:rootContext.xml
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
public class WebConfig implements WebApplicationInitializer{

	private static Logger loger = LoggerFactory.getLogger(WebConfig.class);

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		
		loger.info("[AKS] WebConfig onStartup start");
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.register(RootConfig.class);
		
		servletContext.addListener(new ContextLoaderListener(rootContext));
		
		AnnotationConfigWebApplicationContext dispatcherServlet = new AnnotationConfigWebApplicationContext();
		dispatcherServlet.register(ServletConfig.class);
		
		ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcherServlet",  new DispatcherServlet(dispatcherServlet));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/");
		
		FilterRegistration.Dynamic characterEncodingFilter = servletContext.addFilter("characterEncodingFilter",  new CharacterEncodingFilter());
		characterEncodingFilter.setInitParameter("encoding", "UTF-8");
		characterEncodingFilter.setInitParameter("forceEncoding", "true");
		characterEncodingFilter.addMappingForServletNames(EnumSet.allOf(DispatcherType.class), true, "dispatcherServlet");
		//characterEncodingFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
		
	}
	
}
