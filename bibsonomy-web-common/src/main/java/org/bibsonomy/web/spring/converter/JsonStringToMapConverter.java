package org.bibsonomy.web.spring.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * converts json strings to maps
 *
 * @author dzo
 */
public class JsonStringToMapConverter implements Converter<String, Map<?, ?>> {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public Map<?, ?> convert(String source) {
		try {
			return this.objectMapper.readValue(source, HashMap.class);
		} catch (IOException e) {
			throw new ConversionFailedException(TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(Map.class), source, e);
		}
	}
}
