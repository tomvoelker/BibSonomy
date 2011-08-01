package org.bibsonomy.web.spring.converter;

import static org.bibsonomy.util.ValidationUtils.present;

import org.springframework.core.convert.converter.Converter;

/**
 * @author dzo
 * @version $Id$
 * @param <T> 
 */
public class StringToEnumConverter<T extends Enum<T>> implements Converter<String, T> {

	private final Class<T> enumType;

	/**
	 * 
	 * @param enumType
	 */
	public StringToEnumConverter(final Class<T> enumType) {
		this.enumType = enumType;
	}
	
	@Override
	public T convert(String source) {
		if (!present(source)) {
			// reset value
			return null;
		}
		
		/*
		 * to upper case (= case-insensitive)
		 */
		source = source.toUpperCase().trim();
		return Enum.valueOf(this.enumType, source);
	}
}