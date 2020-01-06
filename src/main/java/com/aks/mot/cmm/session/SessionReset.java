
package com.aks.mot.cmm.session;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;

import com.pantos.vms.cmm.util.SystemUtil;

/**
 *
 * WAS 기동 시 SSO 로그테이블의 로그아웃되지 않은 데이터를 Expire 시킨다
 */

public class SessionReset implements ApplicationContextAware {

	private JdbcTemplate jdbcTemplate;

	private static String resetSql;

	private static String expireSql;

	private static String context;

	private static String serverIp;

	static {
		StringBuffer sb1 = new StringBuffer();
		sb1.append("UPDATE TB_SM_SYS_CONN_HIST SET SESS_EXPR_YN = 'Y', LOGOUT_DT = NOW() WHERE SESS_EXPR_YN = 'N' AND WAS_NM = '$wasNm$' AND SRVR_IP_ADDR = '$svrIpAddr$' ");
		resetSql = sb1.toString().intern();

		StringBuffer sb2 = new StringBuffer();
		sb2.append("UPDATE TB_SM_SYS_CONN_HIST SET SESS_EXPR_YN = 'Y', LOGOUT_DT = NOW() WHERE SESS_EXPR_YN = 'N' AND WAS_NM = '$wasNm$' AND SRVR_IP_ADDR = '$svrIpAddr$' AND USER_CD = '$userCd$' ");
		expireSql = sb2.toString().intern();

		context = SystemUtil.getWasInstNm();

		try {
			serverIp = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException uhe) {
			serverIp = "unknown";
		}
	};

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void setApplicationContext(ApplicationContext args) throws BeansException {
		resetSession();
	}

	private void resetSession() {
		String qry = resetSql.replace("$wasNm$", context).replace("$svrIpAddr$", serverIp);
		jdbcTemplate.execute(qry);
	}

	public void expireSession(String userCd, String sessionId) {
		String qry = expireSql.replace("$userCd$", userCd).replace("$wasNm$", context).replace("$svrIpAddr$", serverIp);
		jdbcTemplate.execute(qry);
	}
}
