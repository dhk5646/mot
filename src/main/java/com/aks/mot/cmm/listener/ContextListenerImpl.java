package com.aks.mot.cmm.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.aks.mot.cmm.config.AppConst;
import com.aks.mot.cmm.util.BeanUtil;
import com.aks.mot.cmm.util.PropertyUtil;

@WebListener
public class ContextListenerImpl implements ServletContextListener {

	private static Logger logger = LoggerFactory.getLogger(ContextListenerImpl.class);

	private static ServletContext SERVLET_CTX;

	private static WebApplicationContext SPRING_WEB_CTX;

	@Autowired
	private BeanUtil beanUtil;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		holdServletContext(sce.getServletContext());
		
		String wasNm = "was";
		//JdbcTemplate jdbcTemplate = (JdbcTemplate) beanUtil.getBean("jdbcTemplate");
		//jdbcTemplate.update("UPDATE TB_SM_SYS_CONN_HIST SET SESS_EXPR_YN = 'Y', LOGOUT_DT = NOW() WHERE WAS_NM = '" + wasNm + "' AND SESS_EXPR_YN = 'N'");

		// 개발서버 AOP 로깅 활성화
		if ("prd".equals(PropertyUtil.getString("host.run.mode"))) {
			AppConst.IS_DEBUG = true;
		}

		logger.info("ServletContext initialized");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		logger.info("ServletContext destroyed");
	}

	public static ServletContext getServletContext() {
		return SERVLET_CTX;
	}

	public static WebApplicationContext getSpringWebContext() {
		if (SPRING_WEB_CTX == null) {
			SPRING_WEB_CTX = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		}
		return SPRING_WEB_CTX;
	}

	public static void holdServletContext(ServletContext ctx) {
		SERVLET_CTX = ctx;
	}

	public static void releaseServletContext() {
		SERVLET_CTX = null;
	}

	public static <T> T getBean(Class<T> classType) {
		WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		return wac.getBean(classType);
	}

	public static Object getBean(String beanId) {
		WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		return wac.getBean(beanId);
	}

	public static int printBeanNames(String strConfig) {
		WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		if (wac == null) {
			logger.error("WebApplicationContext is null");
			return -1;
		}
		int beanCnt = wac.getBeanDefinitionCount();
		logger.info("[created bean][{}][{}개]", strConfig, beanCnt);
		String[] beanNames = wac.getBeanDefinitionNames();
		for (String nm : beanNames) {
			logger.info("[created bean][{}][{}][{}]", strConfig, nm, wac.getBean(nm).getClass());
		}
		return beanCnt;
	}
}
