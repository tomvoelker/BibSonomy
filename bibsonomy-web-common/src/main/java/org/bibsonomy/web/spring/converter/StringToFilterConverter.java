package org.bibsonomy.web.spring.converter;

import java.util.HashSet;
import java.util.Set;

import org.bibsonomy.common.enums.Filter;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.UserFilter;
import org.bibsonomy.util.Sets;
import org.springframework.core.convert.converter.Converter;

/**
 * @author dzo
 */
public class StringToFilterConverter implements Converter<String, Filter> {
	
	private static final Set<Class<?>> FILTER_CLASSES = Sets.<Class<?>>asSet(FilterEntity.class, UserFilter.class);
	
	private Set<StringToEnumConverter<? extends Filter>> converters = new HashSet<StringToEnumConverter<? extends Filter>>();
	
	/**
	 * 
	 */
	public StringToFilterConverter() {
		for (Class<?> class1 : FILTER_CLASSES) {
			converters.add(new StringToEnumConverter(class1));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
	 */
	@Override
	public Filter convert(String source) {
		for (StringToEnumConverter<? extends Filter> converter : this.converters) {
			try {
				return converter.convert(source);
			} catch (IllegalArgumentException e) {
				// ignore
			}
		}
		return null;
	}
}
