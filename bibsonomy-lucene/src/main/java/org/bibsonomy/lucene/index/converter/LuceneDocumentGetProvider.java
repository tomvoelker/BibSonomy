/**
 * BibSonomy-Lucene - Fulltext search facility of BibSonomy
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
package org.bibsonomy.lucene.index.converter;

import org.apache.lucene.document.Document;
import org.bibsonomy.util.GetProvider;

/**
 * Adapter to access a Lucene-{@link Document} via the {@link GetProvider} interface
 *
 * @author jensi
 */
@Deprecated // TODO: remove lucene
public class LuceneDocumentGetProvider implements GetProvider<String, Object> {
	private final Document doc;
	
	/**
	 * @param doc the {@link Document} to wrap
	 */
	public LuceneDocumentGetProvider(final Document doc) {
		this.doc = doc;
	}
	
	@Override
	public Object get(String arg) {
		return doc.get(arg);
	}

}
