package com.aks.mot.common.dataset;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aks.mot.common.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.aks.mot.common.session.UserData;
import com.aks.mot.common.util.SessionUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

/**
 * Request / Response Json 타입과 List, Map 타입간의 데이터 변환처리
 * @author yongwon.seo
 */
public class JsonDataSet extends Constants {

	// 요청 헤더
	protected Map<String, Object> reqHeaderMap = new HashMap<String, Object>();

	// 요청 파라미터
	protected Map<String, Object> reqParamMap = new HashMap<String, Object>();

	// 요청 바디 => IN_DS1, IN_DS2, IN_DS3 ... IN_DSn
	protected Map<String, Object> reqBodyMap = new HashMap<String, Object>();

	// 요청 페이징
	protected Map<String, Object> reqPagingMap = new HashMap<String, Object>();

	// 응답 헤더
	protected Map<String, Object> resHeaderMap = new HashMap<String, Object>();

	// 응답 본문 => outDs1, outDs2, outDs3 ... outDsN
	protected Map<String, Object> resBodyMap = new HashMap<String, Object>();

	protected HttpServletRequest req = null;

	protected HttpServletResponse res = null;

	// 생성자
	public JsonDataSet(HttpServletRequest req, HttpServletResponse res) throws Exception {
		this.req = req;
		this.res = res;

		initDataSetRequest();
		initDataSetResponse();
	}

	// Request 초기작업
	protected void initDataSetRequest() throws Exception {
		if (this.req != null) {
			String jsonReq = req.getParameter(JSON_DATA_PARAM_KEY);
			if (jsonReq != null && !"".equals(jsonReq)) {
				StringBuffer sb = new StringBuffer();
				sb.append("[").append(jsonReq).append("]");
				this.parseRequestJson(sb.toString());
			}
		}
	}

	// Response 헤더 설정
	protected void initDataSetResponse() throws Exception {
		if (this.res != null) {
			this.res.setContentType(getHttpContentType("application/json", "utf-8"));
			this.res.setHeader("Content-Encoding", "utf-8");
		}

		UserData userData = SessionUtil.getUser();

		if (userData != null) {
			resHeaderMap.put("serverIp", userData.getSvrIpAddr());
			resHeaderMap.put("clientIp", userData.getClntIpAddr());
		} else {
			resHeaderMap.put("serverIp", "");
			resHeaderMap.put("clientIp", "");
		}
	}

	// 응답헤더에 Object 추가
	public void setHeader(String key, Object object) {
		resHeaderMap.put(key, object);
	}

	public Map<String, Object> getReqHeaderMap() {
		return (Map<String, Object>) reqHeaderMap.get(JSON_DATA_HEADER);
	}

	public Map<String, Object> getReqParamMap() {
		return (Map<String, Object>) reqParamMap.get(JSON_DATA_IN_PARAM);
	}

	public Map<String, Object> getReqBodyMap() {
		return reqBodyMap;
	}

	/*public PagingMap getPagingMap() {
		PagingMap pagingMap = createPagingMap();
		pagingMap.setMap(getReqParamMap());
		return pagingMap;
	}*/

	/*public PagingMap createPagingMap() {
		PagingMap pagingMap = new PagingMap();
		pagingMap.setTargetRow(getPagingTargetRow());
		pagingMap.setRowSize(getPagingRowSize());
		return pagingMap;
	}*/

	/*public PagingList createPagingList() {
		PagingList pagingList = new PagingList();
		pagingList.setTargetRow(getPagingTargetRow());
		pagingList.setRowSize(getPagingRowSize());
		return pagingList;
	}*/

	public int getPagingTargetRow() {
		int targetRow = 1;
		int startPage = 1;
		int rowSize = 10;

		if (reqPagingMap.get("startPage") != null) {
			startPage = Integer.parseInt(reqPagingMap.get("startPage").toString());
		}
		if (reqPagingMap.get("rowSize") != null) {
			rowSize = Integer.parseInt(reqPagingMap.get("rowSize").toString());
		}

		targetRow = ((startPage - 1) * rowSize) + 1;
		return targetRow;
	}

	public int getPagingRowSize() {
		int rowSize = 1;
		if (reqPagingMap.get("rowSize") != null) {
			rowSize = Integer.parseInt(reqPagingMap.get("rowSize").toString());
		}
		return rowSize;
	}

	// #################################### Response 관련 ####################################

	public Map<String, Object> getResHeaderMap() {
		return resHeaderMap;
	}

	// Json Response 헤더 가져오기
	public String getResHeader(String key) {
		return (String) resHeaderMap.get(key);
	}

	public Map<String, Object> getResBodyMap() {
		return resBodyMap;
	}

	public static String getHttpContentType(String mimeType, String encoding) {
		return mimeType + "; charset=" + encoding;
	}

	/**
	 * IN_DS 가져오기(다건형 데이타의 경우 : List)
	 * @param dsName
	 * @return
	 */
	public List getInDsForList(String dsName) {
		return (List) reqBodyMap.get(dsName);
	}

	/**
	 * IN_DS 가져오기(다건형 데이타의 경우 : List)
	 * @param dsName
	 * @return
	 */
	/*public PagingList getPagingList(String dsName) {
		PagingList pagingList = createPagingList();
		pagingList.addAll(getInDsForList(dsName));
		return pagingList;
	}*/

	/**
	 * IN_DS 가져오기
	 * @param dsName
	 * @return
	 */
	public Object getInDs(String dsName) {
		return reqBodyMap.get(dsName);
	}

	/**
	 * OUT DS 설정을 위한 List를 입력받는다
	 * @param dsName
	 * @param list
	 */
	public void setOutDs(String dsName, List list) {
		if (list == null) {
			list = new ArrayList();
		}

		resBodyMap.put(dsName, list);

		/*
		 * if (list instanceof devonframe.paging.model.PagingList) { PagingList
		 * pagingList = (PagingList) list; int rows = pagingList.getRows(); int rowSize
		 * = pagingList.getRowSize(); int pageSize = pagingList.getPageSize(); int
		 * currentPage = pagingList.getCurrentPage(); Map<String, Object> pagingInfo =
		 * new HashMap<String, Object>(); pagingInfo.put("rows", rows);
		 * pagingInfo.put("rowSize", rowSize); pagingInfo.put("pageSize", pageSize);
		 * pagingInfo.put("currentPage", currentPage); resBodyMap.put(dsName +
		 * "_paging", pagingInfo); }
		 */
	}

	/**
	 * OUT DS 설정을 위한 Map을 입력받는다
	 * @param dsName
	 * @param map
	 */
	public void setOutDs(String dsName, Map map) {
		if (map == null)
			map = new HashMap();
		resBodyMap.put(dsName, map);
	}

	/**
	 * Request으로 부터 Json 파싱 및 CUD_FILTER_KEY 설정
	 * @param jsonReq
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void parseRequestJson(String jsonReq) throws Exception {

		JSONArray jsonArr = JSONArray.fromObject(replaceContentString(jsonReq));
		int jsonArrSize = jsonArr.size();

		JSONObject jsonObj = null;
		JSONObject jsonInObj = null;

		for (int k = 0; k < jsonArrSize; k++) {
			jsonObj = jsonArr.getJSONObject(k);
			Iterator iter = jsonObj.keys();
			String dsKey = null;
			Map rowMap = null;

			while (iter.hasNext()) {
				dsKey = (String) iter.next();
				jsonInObj = jsonObj.getJSONObject(dsKey);

				if (JSON_DATA_HEADER.equals(dsKey)) {
					rowMap = convertJsonToMap(jsonInObj);
					reqHeaderMap.put(dsKey, setComInfo(rowMap));

				} else if (JSON_DATA_PAGING.equals(dsKey)) {
					rowMap = convertJsonToMap(jsonInObj);
					reqPagingMap.putAll(setComInfo(rowMap));

				} else if (JSON_DATA_IN_PARAM.equals(dsKey)) {
					rowMap = convertJsonToMap(jsonInObj);
					reqParamMap.put(dsKey, setComInfo(rowMap));

				} else {
					JSONArray createArr = null;
					JSONArray updateArr = null;
					JSONArray deleteArr = null;
					JSONArray searchArr = null;

					if (jsonInObj.containsKey("createData"))
						createArr = jsonInObj.getJSONArray("createData");
					if (jsonInObj.containsKey("updateData"))
						updateArr = jsonInObj.getJSONArray("updateData");
					if (jsonInObj.containsKey("deleteData"))
						deleteArr = jsonInObj.getJSONArray("deleteData");
					if (jsonInObj.containsKey("searchData")) {
						if (jsonInObj.get("searchData") instanceof JSONArray) {
							searchArr = jsonInObj.getJSONArray("searchData");
						} else {
							searchArr = new JSONArray();
							searchArr.add(jsonInObj.getJSONObject("searchData"));
						}
					}

					JSONObject obj = null;
					List eachInDs = new ArrayList();

					if (createArr != null) {
						for (int i = 0; i < createArr.size(); i++) {
							obj = createArr.getJSONObject(i);
							rowMap = convertJsonToMap(obj);
							rowMap.put(CUD_KEY, CUD_VAL_CREATE);
							eachInDs.add(setComInfo(rowMap));
						}
					}

					if (updateArr != null) {
						for (int i = 0; i < updateArr.size(); i++) {
							obj = updateArr.getJSONObject(i);
							rowMap = convertJsonToMap(obj);
							rowMap.put(CUD_KEY, CUD_VAL_UPDATE);
							eachInDs.add(setComInfo(rowMap));
						}
					}

					if (deleteArr != null) {
						for (int i = 0; i < deleteArr.size(); i++) {
							obj = deleteArr.getJSONObject(i);
							rowMap = convertJsonToMap(obj);
							rowMap.put(CUD_KEY, CUD_VAL_DELETE);
							eachInDs.add(setComInfo(rowMap));
						}
					}

					if (searchArr != null) {
						for (int i = 0; i < searchArr.size(); i++) {
							obj = searchArr.getJSONObject(i);
							rowMap = convertJsonToMap(obj);
							rowMap.put(CUD_KEY, CUD_VAL_NORMAL);
							eachInDs.add(setComInfo(rowMap));
						}
					}

					reqBodyMap.put(dsKey, eachInDs);
				}
			}
		}
	}

	private Map<String, Object> setComInfo(Map<String, Object> rowMap) {
		try {
			rowMap.put("_comCd", SessionUtil.getComCd());
			rowMap.put("_userCd", SessionUtil.getUserId());
			rowMap.put("_timeZone", SessionUtil.getUser().getTimeZone());
			rowMap.put("_langCd", SessionUtil.getUser().getLangCd());
			rowMap.put("_clntIp", SessionUtil.getUser().getClntIpAddr());
		} catch (Exception e) {

		}

		return rowMap;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private HashMap convertJsonToMap(JSONObject json) {
		HashMap result = new HashMap();
		if (json != null) {
			for (Object key : json.keySet()) {
				Object value = json.get(key);
				if (value != null && !value.getClass().equals(JSONNull.class)) {
					result.put(key, value);
				}
			}
		}

		return result;
	}

	// #################################### 문자열 관련 ####################################

	public static String replaceContentString(String value) {
		String str = value;
		if (str != null && str.length() > 0) {
			str = replace(str, "&quot;", "'");
			str = replace(str, "&amp;", "&");
			str = replace(str, "<!--<p>-->", "");
			str = replace(str, "&lt;", "<");
			str = replace(str, "&gt;", ">");
		}

		return str;
	}

	public static String replace(String value, String sourceStr, String targetStr) {
		String str = value;
		if (str == null || sourceStr == null || targetStr == null || str.length() == 0 || sourceStr.length() == 0) {
			return str;
		}

		int position = 0;
		int sourceStrLength = sourceStr.length();
		int targetStrLength = targetStr.length();

		while (true) {
			position = str.indexOf(sourceStr, position);
			if (position != -1) {
				if ((position + sourceStrLength) < str.length()) {
					str = str.substring(0, position) + targetStr + str.substring(position + sourceStrLength);
				} else {
					str = str.substring(0, position) + targetStr;
				}

				position = position + targetStrLength;

				if (position > str.length()) {
					position = str.length();
				}
			} else {
				break;
			}
		}

		return str;
	}

	public void send() throws Exception {
		if (res != null) {
			Map<String, Object> outputMap = new HashMap<String, Object>();
			outputMap.put(JSON_DATA_HEADER, resHeaderMap);
			outputMap.putAll(resBodyMap);

			ObjectMapper mapper = new ObjectMapper();
			mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
			String newJson = mapper.writeValueAsString(outputMap);
			res.getWriter().print(newJson);
		}
	}

}