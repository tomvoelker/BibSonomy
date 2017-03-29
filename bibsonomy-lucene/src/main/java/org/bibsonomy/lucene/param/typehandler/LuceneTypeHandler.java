/**
 * BibSonomy - A blue social bookmark and publication sharing system.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.lucene.param.typehandler;

/**
 * interface for converting objects to corresponding string representations 
 * for storing into the lucene index
 * 
 * @author fei
 * @param <T> the type to handle
 */
public interface LuceneTypeHandler<T> {
	
	/**
	 * get given object's string representation
	 * @param obj 
	 * @return the string representation of obj
	 */
	public String getValue(T obj);

	/**
	 * 
	 * @param str 
	 * @return the object to the str value
	 */
	public T setValue(String str);
}
