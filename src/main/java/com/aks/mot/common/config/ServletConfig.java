package com.aks.mot.common.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import com.aks.mot.common.annotation.Comment;
import com.aks.mot.common.interceptor.AuthInterceptor;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages= {"com.aks.mot"}, useDefaultFilters = false, includeFilters = {@ComponentScan.Filter(Controller.class)})
public class ServletConfig implements WebMvcConfigurer{
	
	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setViewClass(JstlView.class);
		viewResolver.setPrefix("/WEB-INF/jsp/views/");
		viewResolver.setSuffix(".jsp");
		viewResolver.setOrder(2);
		registry.viewResolver(viewResolver);
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
	}
	
	@Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	  registry.addInterceptor(new AuthInterceptor()).addPathPatterns(new String[] {"/**/*.do", "/**/*.svc"});
	}
	
}