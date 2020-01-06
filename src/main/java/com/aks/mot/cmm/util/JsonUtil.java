package com.aks.mot.cmm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;

/**
 * <pre>
 * 시스템 : EcplOpen API [ecpl-framework]
 * TYPE  : Utility
 * 한글명 :   
 * Desc. : jSON 데이터를 마샬링/언마샬링 하는 유틸리티 클래스. 
 * Author: 91117090 
 * Create: 2015. 12. 18.
 * </pre>
 * 
 * @see
 */
public final class JsonUtil {

	// ~ Instance fields. ~~~~~~~~~~~~~~
	/**
	 * The Constant LOG.
	 */
	private static final Logger looger = LoggerFactory.getLogger(JsonUtil.class);

	// ~ Constructors. ~~~~~~~~~~~~~~~~~

	private JsonUtil() {
		looger.info(JsonUtil.class.getName());
	}

	// ~ Implementation Method. ~~~~~~~~
	// ~ Self Methods. ~~~~~~~~~~~~~~~~~

	/**
	 * <p>
	 * 오브젝트를 JSON 형태로 마샬링 한다.
	 * </p>
	 * Marshalling json.
	 * 
	 * @param object the object
	 * @return the string
	 * @throws Exception the exception
	 */
	public static String marshallingJson(final Object object) throws Exception {

		return parseJson(false, object);
	}

	/**
	 * 오브젝트를 JSON 데이터로 사람이 보기 편한 형태로 마샬링 한다.
	 * 
	 * @param object the object
	 * @return the string
	 * @throws Exception the exception
	 */
	public static String marshallingJsonWithPretty(final Object object) throws Exception {
		return parseJson(true, object);
	}

	/**
	 * 실제로 오브젝트를 Json 텍스트로 마샬링 한다.
	 *
	 * @param pretty the pretty
	 * @param object the object
	 * @return the string
	 * @throws Exception the exception
	 */
	private static String parseJson(boolean pretty, Object object) throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(Include.ALWAYS);

		DefaultSerializerProvider provider = new DefaultSerializerProvider.Impl();
		provider.setNullValueSerializer(new NullSerializer());
		objectMapper.setSerializerProvider(provider);

		// 보기 편하게 출력
		if (pretty) {
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		}

		return objectMapper.writeValueAsString(object);
	}

	/**
	 * <p>
	 * JSON 형태의 스트링을 특정 오브젝트로 언마샬링 한다.
	 * </p>
	 * Unmarshalling json.
	 * 
	 * @param jsonText the json text
	 * @param valueType the value type
	 * @return the t
	 * @throws Exception the exception
	 */
	public static <T> T unmarshallingJson(final String jsonText, final Class<T> valueType) throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		String data;
		T t = null;

		data = jsonText.replaceAll("null", "\"\"");
		t = (T) objectMapper.readValue(data, valueType);

		return t;
	}

	public static String getPretty(String jsonString) {

		final String INDENT = "    ";
		StringBuffer prettyJsonSb = new StringBuffer();

		int indentDepth = 0;
		String targetString = null;
		for (int i = 0; i < jsonString.length(); i++) {
			targetString = jsonString.substring(i, i + 1);
			if (targetString.equals("{") || targetString.equals("[")) {
				prettyJsonSb.append(targetString).append("\n");
				indentDepth++;
				for (int j = 0; j < indentDepth; j++) {
					prettyJsonSb.append(INDENT);
				}
			} else if (targetString.equals("}") || targetString.equals("]")) {
				prettyJsonSb.append("\n");
				indentDepth--;
				for (int j = 0; j < indentDepth; j++) {
					prettyJsonSb.append(INDENT);
				}
				prettyJsonSb.append(targetString);
			} else if (targetString.equals(",")) {
				prettyJsonSb.append(targetString);
				prettyJsonSb.append("\n");
				for (int j = 0; j < indentDepth; j++) {
					prettyJsonSb.append(INDENT);
				}
			} else {
				prettyJsonSb.append(targetString);
			}

		}

		return prettyJsonSb.toString();

	}

	public static String getDefaultPretty(String jsonString) throws Exception {

		ObjectMapper jacksonMapper = new ObjectMapper();

		Object json = jacksonMapper.readValue(jsonString, Object.class);

		return jacksonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
	}
	// ~ Getter and Setter. ~~~~~~~~~~~~

}
