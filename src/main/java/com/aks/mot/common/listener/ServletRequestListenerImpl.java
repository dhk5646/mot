package com.aks.mot.common.listener;

import java.util.UUID;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.aks.mot.common.config.AppConst;
import com.aks.mot.common.session.ServletRequestHolder;
import com.aks.mot.common.session.UserDataStore;
import com.aks.mot.common.util.SessionUtil;

public class ServletRequestListenerImpl implements ServletRequestListener {

	private static Logger logger = LoggerFactory.getLogger(ServletRequestListenerImpl.class);
	
	@Override
	public void requestInitialized(ServletRequestEvent servletrequestevent) {
		HttpServletRequest request = (HttpServletRequest) servletrequestevent.getServletRequest();
		String uri = request.getRequestURI();

		if (uri.indexOf(".do") > 1 || uri.indexOf(".dev") > 1 || uri.indexOf(".jsp") > 1) {
			try {
				request.setAttribute("tranId", AppConst.UUID_TXID + "-" + UUID.randomUUID().toString().replace("-", ""));
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			ServletRequestHolder.hold(request);

			if (uri.indexOf(".do") != -1) {
				setMDC(uri, "do");
			} else if (uri.indexOf(".dev") != -1) {
				setMDC(uri, "dev");
			} else if (uri.indexOf(".jsp") != -1) {
				setMDC(uri, "jsp");
			}
		}
	}

	@Override
	public void requestDestroyed(ServletRequestEvent servletrequestevent) {
		HttpServletRequest request = (HttpServletRequest) servletrequestevent.getServletRequest();
		String uri = request.getRequestURI();

		if (uri.indexOf(".do") > 1 || uri.indexOf(".dev") > 1 || uri.indexOf(".jsp") > 1) {
			ServletRequestHolder.unhold();
			UserDataStore.clear();
			MDC.clear();
		}
	}

	private void setMDC(String uri, String type) {
		String[] arr = uri.split("/");
		int i = arr.length;

		MDC.put("uid", SessionUtil.getUserId());

		if ("do".equals(type)) {
			MDC.put("logFileNm", !"".equals(arr[i - 2]) ? arr[i - 2] : arr[i - 1]);
			MDC.put("method", "Controller");
		} else if ("dev".equals(type)) {
			MDC.put("logFileNm", !"".equals(arr[i - 2]) ? arr[i - 2] : arr[i - 1]);
			MDC.put("method", arr[i - 1]);
		} else if ("jda".equals(type)) {
			MDC.put("logFileNm", arr[0]);
			MDC.put("method", arr[1]);
		} else {
			MDC.put("logFileNm", "JSP");
			MDC.put("method", arr[i - 1]);
		}
	}

}
