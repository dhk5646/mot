package com.aks.mot.cmm.util;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.aks.mot.cmm.session.ServletRequestHolder;
import com.aks.mot.cmm.session.SessionStore;
import com.aks.mot.cmm.session.UserData;

public class SessionUtil {

	private SessionUtil() {
		throw new AssertionError("Can not create instance!");
	}

	private final static String getString(String key) {
		Object rtn = null;

		try {
			rtn = SessionStore.get(key);
		} catch (Exception e) {

		}

		if (rtn == null) {
			return "";
		} else {
			return (String) rtn;
		}
	}

	public final static UserData getUser() {
		return (UserData) SessionStore.get("userData");
	}

	public final static String getUserId() {
		return getString("userId");
	}

	public final static String getUserCd() {
		return getString("userCd");
	}

	public final static String getComCd() {
		return getString("comCd");
	}

	public final static String getComGrpCd() {
		return getString("comGrpCd");
	}

	public final static String getNatnCd() {
		return getString("natnCd");
	}

	public final static String getAuthGrpId() {
		return getString("authGrpId");
	}

	public final static String getMapKey() {
		return getString("mapKey");
	}

	public final static String getLangCd() {
		String rtn = getString("langCd");
		if ("".equals(rtn)) {
			rtn = Constants.BASIC_LANG_CD;
		}
		return rtn;
	}

	public final static String getSessionId() {
		return getString("sessionId");
	}

	public final static String getSapSso() {
		return getString("sapSso");
	}

	public final static String getClntIpAddr() {
		return getUser().getClntIpAddr();
	}

	public final static String getSsoToken() {
		return getString("ssoToken").equals("") ? "1234567890" : getString("ssoToken");
	}

	public final static String getNowToken() {
		String nowToken = getString("nowToken");
		if ("".equals(nowToken)) {
			HttpSession ses = (ServletRequestHolder.get()).getSession();
			nowToken = (String) ses.getAttribute("nowToken");
		}

		return nowToken;
	}

	public final static String getPrevToken() {
		return getString("prevToken");
	}

	public final static String getNewToken(String uri) {
		String nowToken = "";

		if (!"".equals(getUserId())) {
			SessionStore.set("prevToken", getNowToken());
			nowToken = getUserId() + uri + System.nanoTime();
			SessionStore.set("nowToken", nowToken);
		}

		return nowToken;
	}

	public final static void setDebug(String flag) {
		if (!"".equals(getUserId())) {
			SessionStore.set("debug", flag);
		}
	}

	public final static boolean getDebug() {
		boolean rtn = false;
		try {
			if ("true".equals(getString("debug"))) {
				rtn = true;
			} else {
				rtn = false;
			}
		} catch (Exception e) {
			rtn = false;
		}
		return rtn;
	}

	public final static void setXlsPwd(String pwd) {
		if (!"".equals(getUserId())) {
			SessionStore.set("xlsPwd", pwd);
		}
	}

	public final static String getXlsPwd() {
		return getString("xlsPwd");
	}

	public final static void invalidate() {
		SessionStore.invalidate();
	}

	public static Map getMap() {
		return SessionStore.getMap();
	}

	public static MockHttpServletRequest createMockSession(UserData userData) {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpSession mockSession = new MockHttpSession();
		mockSession.setAttribute("userData", userData);
		request.setSession(mockSession);

		SessionStore.set("userId", userData.getUserId());
		SessionStore.set("userCd", userData.getUserCd());
		SessionStore.set("comCd", userData.getComCd());
		SessionStore.set("langCd", userData.getLangCd());

		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		ServletRequestHolder.hold(request);

		return request;
	}

}
