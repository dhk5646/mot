package com.aks.mot.common.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * <pre>
 * 시스템 : EcplOpen API [ecpl-framework]
 * TYPE  : Utility
 * 한글명 :
 * Desc. : Json 마샬링 할경우 Object가 null이면 ""으로 처리 함.
 * Author: 91117090
 * Create: 2015. 12. 18.
 * </pre>
 * @see
 */
public class NullSerializer extends JsonSerializer<Object> {

	public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		jgen.writeString("");
	}

}
