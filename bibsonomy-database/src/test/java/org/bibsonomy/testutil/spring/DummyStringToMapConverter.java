package org.bibsonomy.testutil.spring;

import org.springframework.core.convert.converter.Converter;

import java.util.HashMap;
import java.util.Map;

/**
 * dummy string -> map converter
 *
 * @author dzo
 */
public class DummyStringToMapConverter implements Converter<String, Map<?, ?>> {

	@Override
	public Map<?, ?> convert(String source) {
		return new HashMap<>();
	}
}
