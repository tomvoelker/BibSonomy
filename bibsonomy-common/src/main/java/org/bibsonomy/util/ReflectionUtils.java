/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

/**
 * @author dzo
 */
public class ReflectionUtils {
	private ReflectionUtils() { }
	
	/**
	 * Get the actual type arguments a child class has used to extend a generic base class.
	 * e.g. you have a class X extends Y<G,…> this methods returns the types
	 * of G,…
	 * 
	 * @param clazz
	 * @return the actual type arguments for the provided clazz
	 */
	public static Type[] getActualTypeArguments(final Class<?> clazz) {
		Type type = clazz;
		while (type instanceof Class<?>) {
			type = ((Class<?>) type).getGenericSuperclass();
		}
		final ParameterizedType parameterizedType = (ParameterizedType) type;
		return parameterizedType.getActualTypeArguments();
	}
	
	/**
	 * @param clazz
	 * @return {@link #getActualTypeArguments(Class)} as a list of {@link Class}es
	 */
	public static List<Class<?>> getActualClassArguments(final Class<?> clazz) {
		final Type[] types = getActualTypeArguments(clazz);
		final List<Class<?>> classes = new LinkedList<Class<?>>();
		
		for (final Type type : types) {
			classes.add((Class<?>) type);
		}
		return classes;
	}
}
