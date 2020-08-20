package com.aks.mot.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Desc : Spring Bean을 가져오는 기능과 팩토리역할을 전담
 * @Author : 
 * @Date : 
 * @Version : 1.0
 */
public class BeanUtil implements ApplicationContextAware {

    private static ApplicationContext context;

    /**
     * Desc : ApplicationContext를 설정한다
     * @param context
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
    	BeanUtil.context = context;
    }

    /**
     * Desc : 스프링 Bean을 가져온다
     * @param sBeanName
     * @return
     */
    public static Object getBean( String sBeanName ) {
        return BeanUtil.context.getBean( sBeanName );
    }
}
