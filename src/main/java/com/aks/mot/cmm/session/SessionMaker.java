package com.aks.mot.cmm.session;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;

import com.pantos.vms.cmm.annotation.Comment;
import com.pantos.vms.cmm.exception.BizException;
import com.pantos.vms.cmm.log.ECLogger;
import com.pantos.vms.cmm.log.ECLoggerFactory;
import com.pantos.vms.cmm.service.UserService;
import com.pantos.vms.cmm.util.CacheUtil;
import com.pantos.vms.cmm.util.Constants;
import com.pantos.vms.cmm.util.PropertyUtil;
import com.pantos.vms.cmm.util.RequestUtil;
import com.pantos.vms.cmm.util.SessionUtil;
import com.pantos.vms.cmm.util.StringUtil;
import com.pantos.vms.cmm.util.SystemUtil;

import devon.core.util.locale.LLocale;

public class SessionMaker {

	private static Logger logger = LoggerFactory.getLogger(SessionMaker.class);

	private static DelegatingPasswordEncoder delegatingPasswordEncoder;

	static {
		delegatingPasswordEncoder = (DelegatingPasswordEncoder) PasswordEncoderFactories.createDelegatingPasswordEncoder();
		delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());
	}

	@Autowired
	private UserService userService;

	@Autowired
	private EhCacheCacheManager cacheManager;

	@Comment("사용자 세션생성")
	public void makeSession(Map<String, Object> param) throws Exception {

		// 1. 사용자 아이디를 기준으로 내부사용자 우선 정보를 가져온다.
		Map<String, Object> userInfo = userService.getUserInfo(param);

		if (userInfo == null || userInfo.isEmpty() || !userInfo.containsKey("userCd") || userInfo.get("userCd") == null || "".equals(userInfo.get("userCd"))) {
			throw new BizException("msg.login.chk_1"); //User does not exists.
		}

		if (userInfo.get("lockYn") == "Y") {
			throw new BizException("msg.login.chk_2");  //Your account is locked.
		}

		if ((int) userInfo.get("pwdErrCnt") >= 5) {
			throw new BizException("msg.login.chk_3");  //The password is incorrect five times.
		}

		String passwd = (String) param.get("passwd");
		String savePasswd = (String) userInfo.get("userPwd");
		String remote = StringUtil.nvl(param.get("remote"), "");

		if (!remote.equals("session")) {
			if (!delegatingPasswordEncoder.matches(passwd, savePasswd)) {
				userService.updateFailCnt(param);
				throw new BizException("msg.login.chk_4"); //Password invalid!
			} else {
				userService.updateFailCntInit(param);
			}
		}

		setSessionStore(userInfo, param);

	}

	@Comment("SSO 사용자 세션생성")
	public void makeSsoSession(Map<String, Object> param) throws Exception {
		Map<String, Object> userInfo = userService.getSsoUserInfo(param);

		if (userInfo == null || userInfo.isEmpty() || !userInfo.containsKey("userCd") || userInfo.get("userCd") == null || "".equals(userInfo.get("userCd"))) {
			throw new BizException("msg.login.chk_1");
		}

		if (userInfo.get("lockYn") == "Y") {
			throw new BizException("msg.login.chk_2");
		}

		if ((int) userInfo.get("pwdErrCnt") >= 5) {
			throw new BizException("msg.login.chk_3");
		}

		setSessionStore(userInfo, param);
	}

	public void setSessionStore(Map<String, Object> userInfo, Map<String, Object> param) throws Exception {
		String userId = (String) userInfo.get("userId");
		String userCd = (String) userInfo.get("userCd");
		String comCd = (String) userInfo.get("comCd");
		String comGrpCd = (String) userInfo.get("comGrpCd");
		String natnCd = (String) userInfo.get("natnCd");
		String authGrpId = (String) userInfo.get("authGrpId");
		String mapKey = (String) userInfo.get("mapKey");

		UserData userData = new UserData();
		userData.setUserId(userId);
		userData.setUserCd(userCd);
		userData.setComCd(comCd);
		userData.setComGrpCd(comGrpCd);
		userData.setNatnCd(natnCd);
		userData.setAuthGrpId(authGrpId);
		userData.setMapKey(mapKey);
		userData.setUserSprCd(StringUtil.nvl(userInfo.get("userSprCd")));
		userData.setLangCd(StringUtil.nvl(userInfo.get("langCd")));
		userData.setYmdForm(StringUtil.nvl(userInfo.get("ymdForm")));
		userData.setEmailAddr(StringUtil.nvl(userInfo.get("emailAddr")));
		userData.setMbpNo(StringUtil.nvl(userInfo.get("mbpNo")));
		userData.setSid(SessionUtil.getSessionId().replace("SSID-", ""));

		InetAddress inet;
		String svrIpAddr = "0.0.0.0";
		String clntIpAddr = "0.0.0.0";
		String wasInstNm = "";

		try {
			inet = InetAddress.getLocalHost();
			svrIpAddr = (inet.toString().split("/"))[1];
			clntIpAddr = RequestUtil.getRemoteAddr();
			wasInstNm = SystemUtil.getWasInstNm();
		} catch (UnknownHostException e) {
			logger.error("Get Host Info Failed => ", e.getMessage());
		}

		userData.setClntIpAddr(clntIpAddr);
		userData.setSvrIpAddr(svrIpAddr);
		userData.setWasInstNm(wasInstNm);

		// 사용자 언어설정
		String strLang = userData.getLangCd();
		LLocale.setLocale(strLang);
		LLocale.setDefaultLocale(new Locale(strLang));

		SessionStore.set("userData", userData);
		SessionStore.set("userId", userId);
		SessionStore.set("userCd", userCd);
		SessionStore.set("comCd", comCd);
		SessionStore.set("comGrpCd", comGrpCd);
		SessionStore.set("natnCd", natnCd);
		SessionStore.set("authGrpId", authGrpId);
		SessionStore.set("mapKey", mapKey);
		SessionStore.set("langCd", strLang);
		SessionStore.set("sessionId", SessionUtil.getSessionId());
		SessionStore.setMap("userData");

		// 표준버튼권한 캐시저장
		param.put("userCd", userCd);
		List<Map<String, Object>> useAuth = userService.selAuthBtn(param);
		CacheUtil.setCacheValue(cacheManager, Constants.AUTH_BTN_CACHE, userCd, useAuth);

		if ("prd".equals(PropertyUtil.getString("host.run.mode"))) {
			insertSsoLogging();
		}
	}

	@Comment("모바일 사용자 로그인")
	public Map<String, Object> mlogin(Map<String, Object> param) throws Exception {

		Map<String, Object> userInfo = userService.getUserInfo(param);
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		if (userInfo != null) {
			if (userInfo == null || userInfo.isEmpty() || !userInfo.containsKey("userCd") || userInfo.get("userCd") == null || "".equals(userInfo.get("userCd"))) {
				throw new BizException("User does not exists!");
			}

			String passwd = (String) param.get("passwd");
			String savePasswd = (String) userInfo.get("userPwd");

			if (!delegatingPasswordEncoder.matches(passwd, savePasswd)) {
				userService.updateFailCnt(param);
				throw new BizException("Password invalid!");
			} else {
				userService.updateFailCntInit(param);
			}

			rtnMap.put("userId", (String) userInfo.get("userId"));
			rtnMap.put("userCd", (String) userInfo.get("userCd"));
			rtnMap.put("userNm", (String) userInfo.get("userNm"));
			rtnMap.put("comCd", (String) userInfo.get("comCd"));
			rtnMap.put("comGrpCd", (String) userInfo.get("comGrpCd"));
		} else {
			throw new BizException("msg.cm.outeruser.user.invalid");
		}

		return rtnMap;
	}

	@Comment("사용자 로그인 로깅")
	public void insertSsoLogging() {
		HashMap<String, Object> map = new HashMap<>();
		map.put("userCd", SessionUtil.getUserCd());
		map.put("comCd", SessionUtil.getComCd());
		map.put("langCd", SessionUtil.getUser().getLangCd());
		map.put("clntIp", SessionUtil.getUser().getClntIpAddr());
		map.put("sessionId", SessionUtil.getSessionId());
		map.put("svrIp", SessionUtil.getUser().getSvrIpAddr());
		map.put("wasIst", SessionUtil.getUser().getWasInstNm());

		try {
			userService.insSsoLog(map);
			userService.updateLoginDate(map);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

}