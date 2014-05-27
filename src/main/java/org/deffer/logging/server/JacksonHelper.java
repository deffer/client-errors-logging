package org.deffer.logging.server;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


/**
 * This is a static helper utility class for quickly serialising and de-serialising JAXB-mapped structures. Uses a
 * Jackson {@link com.fasterxml.jackson.core.JsonFactory} to perform the operations.
 */
@SuppressWarnings("deprecation")
public class JacksonHelper{

	private static ObjectMapper mapper;

	static {
		JsonFactory factory = new JsonFactory();

		mapper = new ObjectMapper(factory);

		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.disable(MapperFeature.AUTO_DETECT_IS_GETTERS);
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}

	public static String serialize(Object dataToSerialize) throws Exception {
			return mapper.writeValueAsString(dataToSerialize);
	}

	public static <T> T deserialize(String text, Class<T> type) throws Exception {
			return mapper.reader(type).readValue(text);
	}
}
