package com.aks.mot.cmm.session;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionStore {

	public static HttpSession getCachedSession() {
		HttpSession session = (HttpSession) UserDataStore.get("userSession");

		if (session != null)
			return session;

		session = getSession();

		UserDataStore.put("userSession", session);

		return session;
	}

	private static HttpSession getSession() {
		HttpServletRequest servletRequest = ServletRequestHolder.get();

		if (servletRequest == null) {
			throw new RuntimeException("ServletRequest is null");
		}

		return servletRequest.getSession();
	}

	public static void set(String attribute, Object value) {
		try {
			getCachedSession().setAttribute(attribute, value);
		} catch (IllegalStateException e) {
			HttpSession session = getSession();
			session.setAttribute(attribute, value);
			UserDataStore.put("userSession", session);
		}
	}

	public static Object get(String attribute) {
		try {
			return getCachedSession().getAttribute(attribute);
		} catch (IllegalStateException e) {
			return getSession().getAttribute(attribute);
		}
	}

	public static void remove(String attribute) {
		try {
			getCachedSession().removeAttribute(attribute);
		} catch (IllegalStateException e) {
			throw new RuntimeException("Fail to remove Session Attribute => " + attribute, e);
		}
	}

	public static void setMap(String cls) throws Exception {
		HashMap<String, Object> map = new HashMap<String, Object>();

		Object obj = getCachedSession().getAttribute(cls);
		for (Field field : obj.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			map.put(field.getName(), field.get(obj));
		}

		set("sessionMap", map);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getMap() {
		return (Map<String, Object>) get("sessionMap");
	}

	public static boolean isServletRequestNotExsist() {
		return ServletRequestHolder.get() == null;
	}

	public static void invalidate() {
		getCachedSession().invalidate();
	}
}
