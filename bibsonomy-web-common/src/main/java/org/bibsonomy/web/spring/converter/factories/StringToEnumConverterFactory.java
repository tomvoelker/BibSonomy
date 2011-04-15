package org.bibsonomy.web.spring.converter.factories;

import static org.bibsonomy.util.ValidationUtils.present;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/**
 * like {@link org.springframework.core.convert.support.StringToEnumConverterFactory}
 * but case-insensitive
 * 
 * e.g. if an enum has a field ADDED "added" and "ADDED" are converted to ADDED
 * 
 * @author dzo
 * @version $Id$
 * @param <E> 
 */
public class StringToEnumConverterFactory<E extends Enum<E>> implements ConverterFactory<String, Enum<E>> {
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T extends Enum<E>> Converter<String, T> getConverter(Class<T> targetType) {
		return new StringToEnum(targetType);
	}	
	
	private class StringToEnum<T extends Enum<T>> implements Converter<String, T> {

		private final Class<T> enumType;

		public StringToEnum(final Class<T> enumType) {
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
}
