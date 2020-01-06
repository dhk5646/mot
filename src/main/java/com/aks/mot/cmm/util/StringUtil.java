package com.aks.mot.cmm.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aks.mot.cmm.exception.AksException;

/**
 * <pre>
 * String Handling 을 위한  Utility Class
 * </pre>
 *
 * @since 2005. 6. 27.
 * @version 1.0
 * @author 박수현, shypark@lgcns.com<br>
 * LG CNS Application Architecture<br>
 */
public class StringUtil {

	private static Logger logger = LoggerFactory.getLogger(StringUtil.class);

	// 한글이 3 byte 일 경우
	public static final int UNICODE_BYTE = 3;

	// "" 일 경우
	public static final String EMPTY = "";

	public StringUtil() {
	}

	/**
	 * Y / N 을 예/아니오로 바꾸어 준다
	 *
	 * @param ynStr Y/N
	 * @return 에/아니오
	 * @exception AksException
	 */
	public static String convertYn(String ynStr) throws AksException {
		try {
			if (ynStr == null)
				return "";
			if (ynStr.trim().toUpperCase().equals("Y"))
				return "예";
			if (ynStr.trim().toUpperCase().equals("N"))
				return "아니오";

		} catch (Exception e) {
			throw new AksException("[StringUtil][convertYn]" + e.getMessage(), e);
		}
		return "";
	}

	/**
	 * 대상문자열(strTarget)에서 특정문자열(strSearch)을 찾아 지정문자열(strReplace)로 변경한 문자열을 반환한다.
	 *
	 * @param strTarget 대상문자열
	 * @param strSearch 변경대상의 특정문자열
	 * @param strReplace 변경 시키는 지정문자열
	 * @exception AksException
	 * @return 변경완료된 문자열
	 */
	public static String replace(String strTarget, String strSearch, String strReplace) throws AksException {
		String result = null;
		try {

			String strCheck = strTarget;
			StringBuffer strBuf = new StringBuffer();
			while (strCheck.length() != 0) {
				int begin = strCheck.indexOf(strSearch);
				if (begin == -1) {
					strBuf.append(strCheck);
					break;
				} else {
					int end = begin + strSearch.length();
					strBuf.append(strCheck.substring(0, begin));
					strBuf.append(strReplace);
					strCheck = strCheck.substring(end);
				}
			}

			result = strBuf.toString();
		} catch (Exception e) {
			throw new AksException("[StringUtil][replace]" + e.getMessage(), e);
		}
		return result;
	}

	/**
	 * 대상문자열(strTarget)의 임의의 위치(loc)에 지정문자열(strInsert)를 추가한 문자열을 반환한다.
	 *
	 * @param strTarget 대상문자열
	 * @param loc 지정문자열을 추가할 위치로서 대상문자열의 첫문자 위치를 0으로 시작한 상대 위치. loc가 0 보다 작은 값일 경우는 대상문자열의 끝자리를 0으로 시작한 상대적 위치. 맨앞과 맨뒤는 문자열 + 연산으로 수행가능함으로 제공하지 않는다.
	 * @param strInsert 추가할 문자열
	 * @exception AksException
	 * @return 추가완료된 문자열
	 */
	public static String insert(String strTarget, int loc, String strInsert) throws AksException {
		String result = null;
		int loc2 = loc;
		try {

			StringBuffer strBuf = new StringBuffer();
			int lengthSize = strTarget.length();
			if (loc2 >= 0) {
				if (lengthSize < loc2) {
					loc2 = lengthSize;
				}
				strBuf.append(strTarget.substring(0, loc2));
				strBuf.append(strInsert);
				strBuf.append(strTarget.substring(loc2 + strInsert.length()));
			} else {
				if (lengthSize < Math.abs(loc2)) {
					loc2 = lengthSize * (-1);
				}
				strBuf.append(strTarget.substring(0, (lengthSize - 1) + loc2));
				strBuf.append(strInsert);
				strBuf.append(strTarget.substring((lengthSize - 1) + loc2 + strInsert.length()));
			}
			result = strBuf.toString();
		} catch (Exception e) {
			throw new AksException("[StringUtil][insert]" + e.getMessage(), e);
		}
		return result;
	}

	public static String[] split(String str) {
		return split(str, null, -1);
	}

	public static String[] split(String str, char separatorChar) {
		return splitWorker(str, separatorChar, false);
	}

	public static String[] split(String str, String separatorChars) {
		return splitWorker(str, separatorChars, -1, false);
	}

	public static String[] split(String str, String separatorChars, int max) {
		return splitWorker(str, separatorChars, max, false);
	}

	public static String[] splitByWholeSeparator(String str, String separator) {
		return splitByWholeSeparator(str, separator, -1);
	}

	public static String[] splitByWholeSeparator(String str, String separator, int max) {
		if (str == null)
			return null;
		int len = str.length();
		if (len == 0)
			return new String[0];
		if (separator == null || "".equals(separator))
			return split(str, null, max);
		int separatorLength = separator.length();
		ArrayList<String> substrings = new ArrayList<String>();
		int numberOfSubstrings = 0;
		int beg = 0;
		for (int end = 0; end < len;) {
			end = str.indexOf(separator, beg);
			if (end > -1) {
				if (end > beg) {
					if (++numberOfSubstrings == max) {
						end = len;
						substrings.add(str.substring(beg));
					} else {
						substrings.add(str.substring(beg, end));
						beg = end + separatorLength;
					}
				} else {
					beg = end + separatorLength;
				}
			} else {
				substrings.add(str.substring(beg));
				end = len;
			}
		}

		return substrings.toArray(new String[substrings.size()]);
	}

	public static String[] splitPreserveAllTokens(String str) {
		return splitWorker(str, null, -1, true);
	}

	public static String[] splitPreserveAllTokens(String str, char separatorChar) {
		return splitWorker(str, separatorChar, true);
	}

	public static String[] splitPreserveAllTokens(String str, String separatorChars) {
		return splitWorker(str, separatorChars, -1, true);
	}

	public static String[] splitPreserveAllTokens(String str, String separatorChars, int max) {
		return splitWorker(str, separatorChars, max, true);
	}

	private static String[] splitWorker(String str, char separatorChar, boolean preserveAllTokens) {
		if (str == null)
			return null;
		int len = str.length();
		if (len == 0)
			return new String[0];
		List<String> list = new ArrayList<String>();
		int inx = 0;
		int start = 0;
		boolean match = false;
		boolean lastMatch = false;
		while (inx < len) {
			if (str.charAt(inx) == separatorChar) {
				if (match || preserveAllTokens) {
					list.add(str.substring(start, inx));
					match = false;
					lastMatch = true;
				}
				start = ++inx;
			} else {
				lastMatch = false;
				match = true;
				inx++;
			}
		}
		if (match || preserveAllTokens && lastMatch)
			list.add(str.substring(start, inx));
		return list.toArray(new String[list.size()]);
	}

	private static String[] splitWorker(String str, String separatorChars, int max, boolean preserveAllTokens) {
		if (str == null)
			return null;
		int len = str.length();
		if (len == 0)
			return new String[0];
		List<String> list = new ArrayList<String>();
		int sizePlus1 = 1;
		int inx = 0;
		int start = 0;
		boolean match = false;
		boolean lastMatch = false;
		if (separatorChars == null) {
			while (inx < len) {
				if (Character.isWhitespace(str.charAt(inx))) {
					if (match || preserveAllTokens) {
						lastMatch = true;
						if (sizePlus1++ == max) {
							inx = len;
							lastMatch = false;
						}
						list.add(str.substring(start, inx));
						match = false;
					}
					start = ++inx;
				} else {
					lastMatch = false;
					match = true;
					inx++;
				}
			}
		} else if (separatorChars.length() == 1) {
			char sep = separatorChars.charAt(0);
			while (inx < len) {
				if (str.charAt(inx) == sep) {
					if (match || preserveAllTokens) {
						lastMatch = true;
						if (sizePlus1++ == max) {
							inx = len;
							lastMatch = false;
						}
						list.add(str.substring(start, inx));
						match = false;
					}
					start = ++inx;
				} else {
					lastMatch = false;
					match = true;
					inx++;
				}
			}
		} else {
			while (inx < len) {
				if (separatorChars.indexOf(str.charAt(inx)) >= 0) {
					if (match || preserveAllTokens) {
						lastMatch = true;
						if (sizePlus1++ == max) {
							inx = len;
							lastMatch = false;
						}
						list.add(str.substring(start, inx));
						match = false;
					}
					start = ++inx;
				} else {
					lastMatch = false;
					match = true;
					inx++;
				}
			}
		}
		if (match || preserveAllTokens && lastMatch)
			list.add(str.substring(start, inx));
		return list.toArray(new String[list.size()]);
	}

	/**
	 * 입력된 스트링에서 특정 문자를 제거한다.
	 */
	public static String strip(String str, String delims) {
		if (str == null || str.length() == 0 || delims == null)
			return str;
		StringBuffer sb = new StringBuffer();
		StringTokenizer st = new StringTokenizer(str, delims);
		while (st.hasMoreTokens()) {
			sb.append(st.nextToken());
		}
		return sb.toString();
	}

	/**
	 * 입력된 문자열의 첫번째 문자를 대문자로 치환하여 리턴한다.
	 */
	public static String upperFirst(String str) {
		if (str == null || str.length() < 1)
			return str;
		return (str.substring(0, 1).toUpperCase() + str.substring(1));
	}

	/**
	 * String array 정보를 gubun 자를 포함한 하나의 문자열로 리턴한다.
	 */
	public static String arrayToString(String[] values, String gubun) {
		StringBuffer sb = new StringBuffer();
		if (values == null || values.length < 1)
			return "";
		sb.append(values[0]);
		for (int i = 1; i < values.length; i++) {
			sb.append(gubun).append(values[i]);
		}
		return sb.toString();
	}

	/**
	 * 입력된 문자열을 gubun을 delimeter로 분할하여 String array로 리턴한다.
	 */
	public static String[] stringToArray(String text, String gubun) {
		ArrayList<String> array = new ArrayList<String>();
		String cur = text;
		while (cur != null) {
			int inx = cur.indexOf(gubun);
			if (inx < 0) {
				array.add(cur);
				cur = null;
			} else {
				array.add(cur.substring(0, inx));
				cur = cur.substring(inx + gubun.length());
			}
		}
		return array.toArray(new String[array.size()]);
	}

	public static String str2alert(String str) {
		if (str == null)
			return null;
		StringBuffer buf = new StringBuffer();
		char[] ch = str.toCharArray();
		int len = ch.length;
		for (int inx = 0; inx < len; inx++) {
			if (ch[inx] == '\n') {
				buf.append("\\n");
			} else if (ch[inx] == '\t') {
				buf.append("\\t");
			} else if (ch[inx] == '"') {
				buf.append("'");
			} else {
				buf.append(ch[inx]);
			}
		}
		return buf.toString();
	}

	public static String java2msg(String str) {
		if (str == null)
			return null;
		StringBuffer buf = new StringBuffer();
		char[] ch = str.toCharArray();
		int len = ch.length;
		for (int inx = 0; inx < len; inx++) {
			if (ch[inx] == '\n') {
				buf.append("\\n");
			} else if (ch[inx] == '\t') {
				buf.append("\\t");
			} else {
				buf.append(ch[inx]);
			}
		}
		return buf.toString();
	}

	public static String java2html(String str) {
		if (str == null)
			return null;
		StringBuffer buf = new StringBuffer();
		char[] ch = str.toCharArray();
		int len = ch.length;
		for (int inx = 0; inx < len; inx++) {
			if (ch[inx] == '&') {
				buf.append("&amp;");
			} else if (ch[inx] == '<') {
				buf.append("&lt;");
			} else if (ch[inx] == '>') {
				buf.append("&gt;");
			} else if (ch[inx] == '"') {
				buf.append("&quot;");
			} else if (ch[inx] == '\'') {
				buf.append("&#039;");
			} else {
				buf.append(ch[inx]);
			}
		}
		return buf.toString();
	}

	public static String toOneLine(String string) {
		if (string == null) {
			return null;
		} else {
			return string.replace('\n', ' ');
		}
	}

	public static String java2mysql(String str) {
		if (str == null)
			return null;
		StringBuffer buf = new StringBuffer();
		char[] ch = str.toCharArray();
		int len = ch.length;
		for (int inx = 0; inx < len; inx++) {
			if (ch[inx] == '\n') {
				buf.append("\\n");
			} else if (ch[inx] == '\t') {
				buf.append("\\t");
			} else if (ch[inx] == '\r') {
				buf.append("\\r");
			} else if (ch[inx] == '\'') {
				buf.append("\\'");
			} else if (ch[inx] == '"') {
				buf.append("\\\"");
			} else if (ch[inx] == '%') {
				buf.append("\\%");
			} else {
				buf.append(ch[inx]);
			}
		}
		return buf.toString();
	}

	/**
	 *
	 * String input을 double로 Return한다.
	 */
	public static double stringToDouble(String input) {
		String str = input;
		if (input == null) {
			str = "0.0";
		}
		if (input.equals("")) {
			str = "0.0";
		}
		if (input.indexOf(".") < 0) {
			str = new StringBuffer(str).append(".0").toString();
		}
		double dBmQty = Double.parseDouble(str);
		return dBmQty;
	}

	/**
	 *
	 * String data가 null 일 경우 null String으로 리턴
	 */
	public static String isNull(String data) {
		String result = data;
		if (data == null) {
			result = "";
		}
		return result;
	}

	/**
	 * 문자열이 null인지 판별하여 null이면 defaultValue를 리턴하고 아니면 원 문자열을 그대로 리턴한다.<br>
	 * <br>
	 *
	 * @param str 문자열
	 * @param defaultValue 디폴트값
	 * @return String 치환된 문자열
	 */
	public static String envl(String str, String defaultValue) {
		if (str == null) {
			return defaultValue;
		} else if (str.equals("")) {
			return defaultValue;
		} else {
			return str;
		}
	}

	/**
	 *
	 * number 포맷의 String data가 null 일 경우 "0"으로 리턴
	 */
	public static String isNullNum(String data) {
		String result = data;
		if (data == null) {
			result = "0";
		}
		if (data.equals("")) {
			result = "0";
		}
		if (!data.equals("0")) {
			if (data.indexOf("-") > 0) {
				result = data.substring(data.indexOf("-"), data.length());
			}
		}
		return result;
	}

	/**
	 *
	 * String data가 null or 공백 인지 여부
	 */
	public static boolean isNullBoolean(String data) {
		if (data == null || data.trim().length() <= 0) {
			return true;
		}
		return false;
	}

	/**
	 *
	 * data가 null or 공백 인지 여부
	 */
	public static boolean isNullBoolean(Object data) {
		if (data == null) {
			return true;
		} else if (data instanceof String && "".equals(StringUtil.nvl(data))) {
			return true;
		}
		return false;
	}

	/* Left Trim */
	public static String ltrim(String source) {
		return source.replaceAll("^\\s+", "");
	}

	/* Right Trim */
	public static String rtrim(String source) {
		return source.replaceAll("\\s+$", "");
	}

	/* replace multiple whitespaces between words with single blank */
	public static String itrim(String source) {
		return source.replaceAll("\\b\\s{2,}\\b", " ");
	}

	/* remove all superfluous whitespaces in source string */
	public static String trim(String source) {
		return itrim(ltrim(rtrim(source)));
	}

	/* Left / Right Trim */
	public static String lrtrim(String source) {
		return ltrim(rtrim(source));
	}

	/**
	 * <pre>
	 *
	 * desc : 해당 객체의 값이 널일경우 대체 문자열을 리턴
	 *
	 * </pre>
	 *
	 * @date 2011. 10. 27. 오후 3:39:43
	 * @param obj 널 체크 대상 Object
	 * @param nullStr 널일 경우 리턴할 문자열 default: ""
	 * @return
	 */
	public static String nvl(Object obj, String nullStr) {

		if (obj == null || obj.toString().trim().length() == 0) {
			if (nullStr == null) {
				return "";
			}
			return nullStr;
		}

		return obj.toString();
	}

	/**
	 * <pre>
	 *
	 * desc : 해당 객체의 값이 널일경우 대체 문자열 ""을 리턴
	 *
	 * </pre>
	 *
	 * @date 2011. 10. 27. 오후 3:39:43
	 * @param obj 널 체크 대상 Object
	 * @param nullStr 널일 경우 리턴할 문자열 default: ""
	 * @return
	 */
	public static String nvl(Object obj) {
		return nvl(obj, "");
	}

	// HD_SAP_EMPNO
	public static String cusStringByBytes(String raw, int len, String encoding) {
		if (raw == null)
			return "";

		String arSplitByLength[] = parseStringByBytes(raw, len, encoding);

		if (arSplitByLength.length <= 0)
			return "";

		return arSplitByLength[0];

	}

	public static String[] parseStringByBytes(String raw, int len, String encoding) {

		if (raw == null)
			return null;

		String[] ary = null;

		try {
			// raw 의 byte

			byte[] rawBytes = raw.getBytes(encoding);

			int rawLength = rawBytes.length;

			int index = 0;

			int minus_byte_num = 0;

			int offset = 0;

			int hangul_byte_num = encoding.equals("UTF-8") ? 3 : 2;

			if (rawLength > len) {
				int aryLength = (rawLength / len) + (rawLength % len != 0 ? 1 : 0);
				ary = new String[aryLength];

				for (int i = 0; i < aryLength; i++) {
					minus_byte_num = 0;
					offset = len;

					if (index + offset > rawBytes.length) {
						offset = rawBytes.length - index;
					}

					for (int j = 0; j < offset; j++) {
						if ((rawBytes[index + j] & 0x80) != 0) {
							minus_byte_num++;
						}
					}

					if (minus_byte_num % hangul_byte_num != 0) {
						offset -= minus_byte_num % hangul_byte_num;
					}

					ary[i] = new String(rawBytes, index, offset, encoding);
					index += offset;
				}

			} else {
				ary = new String[] {raw};
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return ary;
	}

	/**
	 * 문자열을 바이트 단위로 자르기
	 */
	public static String subStringByte(String str, int startIdx, int endIndex) {

		StringBuffer sbStr = new StringBuffer();
		int nowIdx = 0;

		for (char c : str.toCharArray()) {
			nowIdx += String.valueOf(c).getBytes().length;

			if (nowIdx > endIndex) {
				break;
			}

			if (nowIdx > startIdx) {
				sbStr.append(c);
			}
		}

		return sbStr.toString();
	}

	public static String substring(Object strObj, int bytes) {
		if (strObj == null) {
			return "";
		}
		return substring(strObj.toString(), bytes);
	}

	public static String substring(String str, int bytes) {
		if (str == null) {
			return "";
		}
		String result = "";
		byte[] buf = str.getBytes();
		boolean isSameLength = buf.length > bytes;
		int limit = isSameLength ? bytes : buf.length;
		if (isSameLength) {
			limit -= 5;
			result = new String(buf, 0, limit);
			result += " ... ";
		} else {
			result = new String(buf, 0, limit);
		}
		return result;
	}

}
