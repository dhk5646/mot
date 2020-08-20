package com.aks.mot.common.auth;

import java.util.HashMap;
import java.util.Map;

import com.aks.mot.common.exception.AuthException;
import com.aks.mot.common.session.SessionStore;

public class AuthChecker {

	private final AuthReader authReader;

	private static final int LIMIT_STORE_SIZE = 100;

	private static final String AUTH_MAP_KEY = "_authMapKey";

	public AuthChecker(AuthReader authReader) {
		this.authReader = authReader;
	}

	public void getAuth(String prgmId, String reqUri) {
		@SuppressWarnings("unchecked")
		Map<String, String> prgmIdMap = (Map<String, String>) SessionStore.get(AUTH_MAP_KEY);
		if (prgmIdMap == null || prgmIdMap.size() > LIMIT_STORE_SIZE) {
			prgmIdMap = new HashMap<String, String>();
			SessionStore.set(AUTH_MAP_KEY, prgmIdMap);
		}

		String key = "";
		if (prgmId == null || prgmId.equals("") || prgmId.equals("null") || prgmId.equals("undefined")) {
			prgmId = "";
			key = reqUri;
		} else {
			key = prgmId;
		}

		String saveAuth = prgmIdMap.get(key);

		if (saveAuth == null) {
			String authVal = authReader.read(prgmId, reqUri);
			prgmIdMap.put(key, authVal);

			if ("N".equals(authVal)) {
				throw new AuthException("Authority Check Fail!");
			}
		} else {
			if ("N".equals(saveAuth)) {
				throw new AuthException("Authority Check Fail!");
			}
		}
	}
}
