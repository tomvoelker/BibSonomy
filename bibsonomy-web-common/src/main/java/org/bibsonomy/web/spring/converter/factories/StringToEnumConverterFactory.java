/**
 * BibSonomy-Web-Common - Common things for web
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
package org.bibsonomy.web.spring.converter.factories;


import org.bibsonomy.web.spring.converter.StringToEnumConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/**
 * like {@link org.springframework.core.convert.support.StringToEnumConverterFactory}
 * but case-insensitive
 * 
 * e.g. if an enum has a field ADDED "added" and "ADDED" are converted to ADDED
 * 
 * @author dzo
 * @param <E> 
 */
public class StringToEnumConverterFactory<E extends Enum<E>> implements ConverterFactory<String, Enum<E>> {
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T extends Enum<E>> Converter<String, T> getConverter(final Class<T> targetType) {
		return new StringToEnumConverter(targetType);
	}
}
