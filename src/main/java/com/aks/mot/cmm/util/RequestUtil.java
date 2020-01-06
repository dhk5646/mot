package com.aks.mot.cmm.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;

import com.aks.mot.cmm.annotation.Comment;
import com.aks.mot.cmm.config.AppConst;
import com.aks.mot.cmm.dataset.JsonDataSet;
import com.aks.mot.cmm.session.ServletRequestHolder;

public final class RequestUtil {

	private static Logger logger = LoggerFactory.getLogger(RequestUtil.class);

	private RequestUtil() {
		throw new AssertionError("Can not create Instance!!");
	}

	public static HttpServletRequest getServletRequest() {
		return ServletRequestHolder.get();
	}

	/***
	 * Desc : 서버의 도메인명을 반환한다.
	 * @Method Name : getServerName
	 * @return
	 */
	public static String getServerName() {
		return getServletRequest().getServerName();
	}

	/***
	 * Desc : 서버의 포트를 반환한다.
	 * @Method Name : getServerPort
	 * @return
	 */
	public static String getServerPort() {
		return String.valueOf(getServletRequest().getServerPort());
	}

	/**
	 * @name_ko http + 도메인 + 포트까지의 정보를 반환한다.(http만 반환한다.)
	 * @param
	 */
	public static String getDomainURI() {
		return "http://" + RequestUtil.getServerName() + ":" + RequestUtil.getServerPort();
	}

	/**
	 * @name_ko 요청된 URI를 반환한다.
	 * @param
	 */
	public static String getRequestURI() {
		HttpServletRequest req = getServletRequest();
		if (req == null) {
			return "";
		}
		return req.getRequestURI();
	}

	/**
	 * @name_ko 컨텍스트의 절대물리경로를 반환한다.
	 * @param
	 */
	public static String getContextRealPath() {
		return getServletRequest().getSession().getServletContext().getRealPath("/");
	}

	/**
	 * @name_ko context path를 반환한다.
	 * @param
	 */
	public static String getContextPath() {
		return getServletRequest().getContextPath();
	}

	/**
	 * @name_ko GET querystring을 반환한다.
	 * @param
	 */
	public static String getQuerystring() {
		return getServletRequest().getQueryString();
	}

	/**
	 * @name_ko 요청 IP를 반환한다.
	 * @param
	 */
	public static String getRemoteAddr() {
		String clntIp = getServletRequest().getHeader("Proxy-Client-IP");
		if (clntIp == null) {
			clntIp = getServletRequest().getHeader("WL-Proxy-Client-IP");
			if (clntIp == null) {
				clntIp = getServletRequest().getHeader("X-Forwarded-For");
				if (clntIp == null) {
					clntIp = getServletRequest().getRemoteAddr();
				}
			}
		}

		return clntIp;
	}

	/**
	 * @name_ko 서버 IP를 반환한다.
	 * @param
	 */
	public static String getServerIpAddr() {
		return getServletRequest().getLocalAddr();
	}

	public static Map<String, Object> getRequestMap(HttpServletRequest req, HttpServletResponse res) throws Exception {
		JsonDataSet jsonDataSet = new JsonDataSet(req, res);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("_header", jsonDataSet.getReqHeaderMap());
		map.put("_param", jsonDataSet.getReqParamMap());
		map.put("_data", jsonDataSet.getReqBodyMap());
		//map.put("_paging", jsonDataSet.getPagingMap());
		return map;
	}

	//###################################### Logging Aspect ###########################################

	public static Object getAttr(String attrId) {
		HttpServletRequest req = getServletRequest();
		if (req == null) {
			req = new MockHttpServletRequest();
			String TXID = AppConst.UUID_TXID + "-" + UUID.randomUUID().toString();
			req.setAttribute(AppConst.UUID_TXID, TXID);
			ServletRequestHolder.hold(req);
			logger.debug("created MockHttpServletRequest");
		}
		return req.getAttribute(attrId);
	}

	public static void setAttr(String attrId, Object obj) {
		HttpServletRequest req = getServletRequest();
		if (req != null) {
			req.setAttribute(attrId, obj);
		} else {
			req = new MockHttpServletRequest();
			String TXID = AppConst.UUID_TXID + "-" + UUID.randomUUID().toString();
			req.setAttribute(AppConst.UUID_TXID, TXID);
			req.setAttribute(attrId, obj);
			ServletRequestHolder.hold(req);
			logger.debug("created MockHttpServletRequest");
		}
	}

	public static void removeAttr(String attrId) {
		HttpServletRequest req = getServletRequest();
		if (req != null) {
			req.removeAttribute(attrId);
		}
	}
	
	@Comment("XSS 문자치환")
	public static String xss(String str) {
		if (str == null) return "";
		str = str.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
		str = str.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
		str = str.replaceAll("'", "& #39;");
		str = str.replaceAll("eval\\((.*)\\)", "");
		str = str.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
		str = str.replaceAll("script", "");
		return str;
	}

	@Comment("XSS 문자치환(상대경로 포함)")
	public static String xss2(String str) {
		str = xss(str).replaceAll("\\.\\.", "");
		return str;
	}

	public static String nvl(Object obj) {
		if (obj == null || obj.toString().trim().length() == 0) {
			return "";
		}
		return obj.toString();
	}

	public static String nvl(Object obj, String str) {
		if (obj == null || obj.toString().trim().length() == 0) {
			if (str == null) {
				return "";
			} else {
				return str;
			}
		}
		return obj.toString();
	}

}
