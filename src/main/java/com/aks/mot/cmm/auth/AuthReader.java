package com.aks.mot.cmm.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.aks.mot.cmm.exception.AuthException;

public class AuthReader {

	private static Logger logger = LoggerFactory.getLogger(AuthReader.class);

	private JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public String read(String prgmId, String reqUri) {
		if (reqUri == null || "".equals(reqUri)) {
			throw new AuthException("Request URI does not exists!");
		}

		//return selectUserAuth(SessionUtil.getUserId(), prgmId, reqUri);
		return "Y";
	}

	private String selectUserAuth(String userCd, String prgmId, String reqUri) {
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT ");
		sb.append("    IF(B.CNT = 0, 'Y', A.RTN) RTN ");
		sb.append("FROM ( ");
		sb.append("    SELECT ");
		sb.append("        IF(COUNT(*) = 0, 'N', 'Y') RTN ");
		sb.append("    FROM ");
		sb.append("        TB_SM_PGM A, ");
		sb.append("        ( ");
		sb.append("            SELECT PGM_ID FROM TB_SM_AUTH_GRP_PGM WHERE AUTH_GRP_ID IN ( ");
		sb.append("                SELECT AUTH_GRP_ID FROM TB_SM_USER WHERE USER_CD = ? ");
		sb.append("            ) ");
		sb.append("        ) B ");
		sb.append("    WHERE ");
		sb.append("        A.PGM_ID = B.PGM_ID AND ");
		if (!"".equals(prgmId)) {
			sb.append("        A.PGM_ID = ? AND ");
		} else {
			sb.append("        ? IS NULL AND ");
		}
		sb.append("        A.PGM_ROUT_NM || A.PGM_FILE_NM = ? ");
		sb.append("    ) A,  ");
		sb.append("    ( ");
		sb.append("        SELECT COUNT(PGM_ID) CNT FROM TB_SM_PGM WHERE PGM_ROUT_NM || PGM_FILE_NM = ? ");
		sb.append("    ) B ");

		String rtn = "";

		try {
			rtn = jdbcTemplate.queryForObject(sb.toString(), new Object[] { userCd, prgmId, reqUri, reqUri }, String.class);
		} catch (Exception e) {
			logger.debug("[AuthCheck] No Page Authority >>> " + userCd);
			throw new AuthException("Authority Check Fail!");
		}

		return rtn;
	}
}
