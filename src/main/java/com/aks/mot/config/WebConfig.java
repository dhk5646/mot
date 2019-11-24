package com.aks.mot.config;

import javax.servlet.Filter;
import javax.servlet.ServletRegistration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/* web.xml
 * 
 * */

@Configuration
public class WebConfig extends AbstractAnnotationConfigDispatcherServletInitializer{

	/*appliation-context.xml 설정*/
	@Override
	protected Class<?>[] getRootConfigClasses() {
		System.out.println("WebConfig.getRootConfigClasses()");
		return new Class[] {RootConfig.class};
	}
	
	/*servlet-context.xml 설정*/
	@Override
	protected Class<?>[] getServletConfigClasses() { 
		System.out.println("WebConfig.getServletConfigClasses()");
		return new Class[] {ServletConfig.class};
	}
	
	/*servlet-mapping 설정*/
	@Override
	protected String[] getServletMappings() {
		System.out.println("WebConfig.getServletMappings()");
		return new String[] {"/"};
	}
	
	/* Exception 설정*/
	@Override
	protected void customizeRegistration(ServletRegistration.Dynamic registration) {
		
		registration.setInitParameter("throwExceptionIfNoHandlerFound", "true");
	}
	
	/* Filter 설정*/
	@Override
    protected Filter[] getServletFilters() {
		System.out.println("WebConfig.getServletFilters()");
		CharacterEncodingFilter encoding = new CharacterEncodingFilter();
		encoding.setEncoding("UTF-8");
		encoding.setForceEncoding(true);
        return new Filter[]{encoding};
    }
}
