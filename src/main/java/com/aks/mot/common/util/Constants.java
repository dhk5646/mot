package com.aks.mot.common.util;

public class Constants {

	public static final int XLS_FRM_HEADER_ROW = 8; // 엑셀 양식의 Header Row 수

	// 기본 Log Appender
	public static final String DEFAULT_LOGGER = "defaultLog";

	// 언어설정 쿠키
	public static final String LANG_CD = "langCd";

	// 캐시 관련
	public static final String AUTH_BTN_CACHE = "authBtnCache";

	public static final String SYS_MSG_CACHE = "sysMsgCache";

	public static final String SYS_WORD_CACHE = "sysWordCache";

	public static final String LOG_CACHE = "logCache";

	// 파일업로드 타입구분
	public static final String FILE_TYPE_EXCEL = "EXCEL";

	public static final String FILE_TYPE_TEMPLATE = "TEMPLATE";

	public static final String FILE_TYPE_REPORT = "REPORT";

	public static final String FILE_TYPE_TEMP = "TEMP";

	public static final String FILE_TYPE_MAIL = "MAIL";

	public static final String FILE_TYPE_EDMS = "EDMS";


	// 화면버튼 기본권한
	public static final String AUTH_BTN_BASIC = "{\"INQ\":\"T\",\"INS\":\"T\",\"UPD\":\"T\",\"DEL\":\"T\",\"XLS\":\"T\",\"PRT\":\"T\"}";

	public static final String AUTH_BTN_REVOKE = "{\"INQ\":\"F\",\"INS\":\"F\",\"UPD\":\"F\",\"DEL\":\"F\",\"XLS\":\"F\",\"PRT\":\"F\"}";


	// Grid 관련
	public static final String CUD_KEY = "GRID_CUD_KEY";

	public static final String CUD_VAL_CREATE = "GRID_FILTER_CREATE";

	public static final String CUD_VAL_UPDATE = "GRID_FILTER_UPDATE";

	public static final String CUD_VAL_DELETE = "GRID_FILTER_DELETE";

	public static final String CUD_VAL_NORMAL = "GRID_FILTER_NORMAL";

	public static final String JSON_DATA_PARAM_KEY = "_dataset_";

	public static final String JSON_DATA_HEADER = "header";

	public static final String JSON_DATA_IN_PARAM = "IN_PARAM";

	public static final String JSON_DATA_PAGING = "PAGING";

	public static final String EAI_USER_ID = "eai";

	public static final String BASIC_LANG_CD = "en";

}
