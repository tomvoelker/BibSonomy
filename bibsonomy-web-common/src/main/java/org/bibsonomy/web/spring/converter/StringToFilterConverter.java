/**
 * BibSonomy-Web-Common - Common things for web
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
