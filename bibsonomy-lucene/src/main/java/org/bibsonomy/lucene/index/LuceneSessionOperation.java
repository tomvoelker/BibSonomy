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
package org.bibsonomy.lucene.index;

import org.apache.lucene.search.IndexSearcher;

/**
 * Callback interface in which all lucene operations should be done (typically using anonymous implementation classes).
 *
 * @author jil
 * @param <T> return type of the operation.
 * @param <E> the exceptions that may be thrown by the operation
 */
public interface LuceneSessionOperation<T, E extends Exception> {
	/**
	 * All Queries to a lucene index requiring access to an {@link IndexSearcher} should be done using an implementation of this method.
	 * 
	 * @param searcher on which the operation may operate
	 * @return the result of the operation.
	 * @throws E 
	 */
	public T doOperation(IndexSearcher searcher) throws E;
}
