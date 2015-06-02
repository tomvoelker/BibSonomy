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
package org.bibsonomy.es;

import java.util.Date;
import java.util.List;

import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.model.Resource;

/**
 * Common interface for updating indices
 * 
 * @param <R> the type of resource stored by the index
 * 
 * @author lutful
 * @author jil
 */
public interface IndexUpdater<R extends Resource> {

	/**
	 * @return the latest log_date from the index. This is null if the index is not existing or empty.
	 */
	Date getLastLogDate();

	/**
	 * @return LastTasId
	 */
	Integer getLastTasId();

	/**
	 * @param contentId
	 */
	void deleteDocumentForContentId(final Integer contentId);

	/**
	 * @param userName
	 */
	void deleteIndexForUser(String userName);

	/**
	 * @param indexId
	 */
	void deleteIndexForIndexId(long indexId);

	/**
	 * @param contentIdsToDelete
	 */
	void deleteDocumentsForContentIds(List<Integer> contentIdsToDelete);
	
	void insertDocument(LucenePost<R> post, final Date currentLogDate);

	void flagUser(final String username);
	
	void unFlagUser(final String userName);
	
	void flush();
	
	/**
	 * updates information about the up-to-dateness of the system
	 * @param state 
	 */
	public void setSystemInformation(final IndexUpdaterState state);
}
