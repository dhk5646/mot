package com.aks.mot.cmm.util;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.Query;
import javax.management.ReflectionException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.aks.mot.cmm.annotation.Comment;
import com.aks.mot.cmm.config.AppConst;

public class SystemUtil {

	private static Logger logger = LoggerFactory.getLogger(SystemUtil.class);

	private Pattern REGEX_PGM_ID = Pattern.compile(AppConst.EXPR_PGM_ID);

	private static PathMatcher matcher = new AntPathMatcher();

	//####################################### Pattern Match #######################################

	public static boolean pathMatch(String strPattern, String strPath) {
		String strSeparator = "\\|";
		String[] ptrns = strPattern.split(strSeparator, -1);
		for (String ptrn : ptrns) {
			if (matcher.match(ptrn, strPath)) {
				return true;
			}
		}
		return false;
	}

	//####################################### Annotation #######################################

	public static String getCommentForMethod(Signature signature) {
		String strComment = "";
		MethodSignature methodSignature = (org.aspectj.lang.reflect.MethodSignature) signature;
		Method method = methodSignature.getMethod();
		Comment comment = method.getAnnotation(Comment.class);
		if (comment != null) {
			strComment = comment.value();
		}
		return strComment;
	}

	protected static <T> T getTargetObject(Object proxyObj, Class<T> targetClassType) {
		if (AopUtils.isJdkDynamicProxy(proxyObj) || AopUtils.isCglibProxy(proxyObj)) {
			try {
				return (T) ((Advised) proxyObj).getTargetSource().getTarget();
			} catch (Exception e) {
				logger.error(e.getMessage());
				return (T) proxyObj;
			}
		}
		return (T) proxyObj;
	}

	//####################################### Namimg #######################################

	public String getPgmId(String str) {
		Matcher m = REGEX_PGM_ID.matcher(str);
		String captureStr = "";
		if (m.find()) {
			captureStr = m.group(1);
		} else {
			logger.trace("Can not find PrgmID => {}", str);
		}
		return captureStr;
	}

	//####################################### Host Env #######################################

	public static String getWasInstNm() {
		String wasNm = StringUtil.nvl(System.getProperty("system"), "vms");
		return wasNm;
	}

	public static String getSysEncoding() {
		String encoding = System.getProperty("sun.jnu.encoding");
		logger.debug("OS Encoding : {}", encoding);
		return encoding;
	}

	public static String getHostIp() {
		String hostIp = null;
		try {
			hostIp = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			logger.error(e.getMessage());
			hostIp = "unknown";
		}
		return hostIp;
	}

	public static String getHostNm() {
		String hostNm = null;
		try {
			hostNm = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			logger.error(e.getMessage());
			hostNm = "unknown";
		}
		return hostNm;
	}

	public static String getHostPort() throws MalformedObjectNameException, NullPointerException, UnknownHostException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		Set<ObjectName> objs = mbs.queryNames(new ObjectName("*:type=Connector,*"), Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
		for (Iterator<ObjectName> i = objs.iterator(); i.hasNext();) {
			ObjectName obj = i.next();
			String port = obj.getKeyProperty("port");
			return port;
		}
		return null;
	}

	public static List<String> getEndPoints() throws MalformedObjectNameException, NullPointerException, UnknownHostException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		Set<ObjectName> objs = mbs.queryNames(new ObjectName("*:type=Connector,*"), Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
		InetAddress addr = InetAddress.getLocalHost();
		ArrayList<String> endPoints = new ArrayList<String>();
		for (Iterator<ObjectName> i = objs.iterator(); i.hasNext();) {
			ObjectName obj = i.next();
			String scheme = mbs.getAttribute(obj, "scheme").toString();
			String port = obj.getKeyProperty("port");
			String host = addr.getHostAddress();
			String ep = scheme + "://" + host + ":" + port;
			endPoints.add(ep);
		}
		return endPoints;
	}

	public static String getLangCdCookie() {
		Cookie cookie = null;
		Cookie cookies[] = RequestUtil.getServletRequest().getCookies();
		for (int i = 0; cookies != null && i < cookies.length; i++) {
			Cookie cook = cookies[i];
			if (cook.getName().trim().equals(Constants.LANG_CD)) {
				cookie = cook;
				break;
			}
		}

		String rtn = "";
		if (cookie != null) {
			rtn = cookie.getValue();
		} else {
			rtn = "en";
		}

		return rtn;
	}

	public static String getCookieString() {
		HttpServletRequest req = RequestUtil.getServletRequest();
		if (req == null) {
			return "";
		}

		Cookie[] cookie = req.getCookies();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < cookie.length; i++) {
			sb.append(cookie[i].getName()).append("=").append(cookie[i].getValue()).append("; ");
		}

		return sb.toString();
	}

	public static boolean checkSvr() {
		if ("batchSvr".equals(getWasInstNm().toUpperCase())) {
			return true;
		} else {
			return false;
		}
	}

	public static Connection makeDataConnection() throws Exception {
		JdbcTemplate jdbcTemplate = (JdbcTemplate) BeanUtil.getBean("jdbcTemplate");
		Connection conn = jdbcTemplate.getDataSource().getConnection();
		return conn;
	}

	public static JdbcTemplate getJdbcTemplate() {
		JdbcTemplate jdbcTemplate = (JdbcTemplate) BeanUtil.getBean("jdbcTemplate");
		return jdbcTemplate;
	}

}
