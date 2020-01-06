package com.pantos.vms.cmm.listener;

import java.util.UUID;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.cache.ehcache.EhCacheCacheManager;

import com.pantos.vms.cmm.config.AppConst;
import com.pantos.vms.cmm.log.ECLogger;
import com.pantos.vms.cmm.log.ECLoggerFactory;
import com.pantos.vms.cmm.session.SessionReset;
import com.pantos.vms.cmm.util.BeanUtil;
import com.pantos.vms.cmm.util.CacheUtil;
import com.pantos.vms.cmm.util.Constants;
import com.pantos.vms.cmm.util.PropertyUtil;

public class SessionListenerImpl implements HttpSessionListener {

	private static final ECLogger logger = ECLoggerFactory.getLogger(SessionListenerImpl.class);

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
				EhCacheCacheManager cacheManager = (EhCacheCacheManager) BeanUtil.getBean("cacheManager");
				if (cacheManager != null) {
					CacheUtil.removeCacheValue(cacheManager, Constants.LOG_CACHE, sessionId);
					CacheUtil.removeCacheValue(cacheManager, Constants.AUTH_BTN_CACHE, userCd);
				}
			} catch (Exception e) {
				logger.error("Error when removing cache => " + e.getMessage());
			}

			logger.debug("[ Session Destroyed ] " + userCd + " (" + sessionId + ")");
		}
	}

}
