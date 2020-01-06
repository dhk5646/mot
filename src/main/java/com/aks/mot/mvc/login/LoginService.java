package com.aks.mot.mvc.login;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pantos.vms.cmm.annotation.Comment;
import com.pantos.vms.cmm.session.SessionMaker;
import com.pantos.vms.cmm.util.SessionUtil;

@RestController
@RequestMapping(value = "/sso")
public class LoginController {

	private static Logger logger = ECLoggerFactory.getLogger(LoginController.class);

	@Autowired
	private SessionMaker sessionMaker;

	@Comment("로그인 처리")
	@RequestMapping(value = "/login.dev")
	public Map<String, Object> login(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String userId = xss(req.getParameter("userId"));
		String passwd = xss(req.getParameter("passwd"));
		String remember = xss(req.getParameter("remember"));

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		map.put("passwd", passwd);

		try {
			sessionMaker.makeSession(map);
			Cookie langCk = new Cookie("langCd", SessionUtil.getLangCd());
			langCk.setMaxAge(3600 * 24 * 30);
			langCk.setPath("/");
			res.addCookie(langCk);

			// 아이디 쿠키저장 및 삭제처리
			if ("true".equals(remember)) {
				Cookie userCk = new Cookie("userId", userId);
				userCk.setMaxAge(3600 * 24 * 30);
				userCk.setPath("/");
				res.addCookie(userCk);
			} else {
				Cookie userCk = new Cookie("userId", null);
				userCk.setMaxAge(0);
				userCk.setPath("/");
				res.addCookie(userCk);
			}

			map.clear();
			map.put("rtnCode", "success");
			map.put("rtnMessage", "success");
		} catch (Exception e) {
			map.clear();
			map.put("rtnCode", "fail");
			map.put("rtnMessage", e.getMessage());
			//map.put("rtnMessage", "System Error");
		}

		return map;
	}

	@Comment("모바일 로그인 처리")
	@CrossOrigin("*")
	@RequestMapping(value = "/mlogin.dev", method = RequestMethod.POST)
	public Map<String, Object> mlogin(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String userId = xss(req.getParameter("userId"));
		String passwd = xss(req.getParameter("passwd"));

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userId", userId);
		param.put("passwd", passwd);

		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			rtnMap = sessionMaker.mlogin(param);
			rtnMap.put("rtnCode", "success");
			rtnMap.put("rtnMessage", "success");
		} catch (Exception e) {
			rtnMap.put("rtnCode", "fail");
			rtnMap.put("rtnMessage", e.getMessage());
		}

		return rtnMap;
	}

	@Comment("로그아읏 처리")
	@RequestMapping(value = "/logout.dev")
	public HashMap<String, String> logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SessionUtil.invalidate();

		HashMap<String, String> rtnMap = new HashMap<String, String>();
		rtnMap.put("rslt", "success");

		response.setContentType("application/json");
		response.setStatus(200);

		return rtnMap;
	}
}
