package com.aks.mot.common.listener;

import java.util.UUID;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aks.mot.common.config.AppConst;
import com.aks.mot.common.session.SessionReset;
import com.aks.mot.common.util.BeanUtil;
import com.aks.mot.common.util.PropertyUtil;

public class SessionListenerImpl implements HttpSessionListener {

	private static Logger logger = LoggerFactory.getLogger(SessionListenerImpl.class);

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		HttpSession session = se.getSession();

		if (session.getAttribute("sessionId") == null) {
			session.setAttribute("sessionId", AppConst.UUID_SSID + "-" + UUID.randomUUID().toString().replace("-", ""));
		}

		int second = Integer.parseInt(PropertyUtil.getString("was.session.timeout"));
		session.setMaxInactiveInterval(second);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		HttpSession session = se.getSession();

		String sessionId = (String) session.getAttribute("sessionId");
		String userCd = (String) session.getAttribute("userCd");

		if (userCd != null) {
			try {
				SessionReset svc = (SessionReset) BeanUtil.getBean("sessionReset");
				svc.expireSession(userCd, sessionId);
			} catch (NullPointerException e) {
				logger.error("[ SessionReset ] " + "Session Expire Error");
			}

			try {
				/*
				 * EhCacheCacheManager cacheManager = (EhCacheCacheManager)
				 * BeanUtil.getBean("cacheManager"); if (cacheManager != null) {
				 * CacheUtil.removeCacheValue(cacheManager, Constants.LOG_CACHE, sessionId);
				 * CacheUtil.removeCacheValue(cacheManager, Constants.AUTH_BTN_CACHE, userCd); }
				 */
			} catch (Exception e) {
				logger.error("Error when removing cache => " + e.getMessage());
			}

			logger.debug("[ Session Destroyed ] " + userCd + " (" + sessionId + ")");
		}
	}

}
