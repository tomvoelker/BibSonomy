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

import static org.bibsonomy.lucene.util.LuceneBase.CFG_LIST_DELIMITER;

import java.util.Collection;

/**
 * convert date objects to a standardized string representation
 * 
 * @author fei
 * @param <T> 
 */
public abstract class LuceneCollectionFormatter<T> extends AbstractTypeHandler<Collection<T>> {
	
	@Override
	public String getValue(final Collection<T> collection) {		
		final StringBuilder retVal = new StringBuilder("");
		for (final T item : collection) {
			retVal.append(CFG_LIST_DELIMITER).append(convertItem(item));
		}
			
		return retVal.toString().trim();
	}
	
	@Override
	public Collection<T> setValue(final String str) {
		final Collection<T> retVal = this.createCollection();
		
		final String[] tokens = str.split(CFG_LIST_DELIMITER);
		
		for (final String token : tokens) {
			retVal.add(this.createItem(token));
		}
		return retVal;
	}

	protected abstract Collection<T> createCollection();
	protected abstract T createItem(String token);
	protected abstract String convertItem(T item);
}