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
package org.bibsonomy.lucene.index.manager;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.es.IndexType;
import org.bibsonomy.lucene.database.LuceneDBLogic;
import org.bibsonomy.lucene.database.LuceneGoldStandardLogic;
import org.bibsonomy.lucene.index.LuceneFieldNames;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.model.GoldStandard;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Resource;

/**
 * Updates the gold standard publication posts
 * uses the {@link LuceneFieldNames#LAST_TAS_ID} for the latest content id
 * (gold standard posts have no tags)
 * 
 * {@link LuceneGoldStandardLogic} overrides {@link LuceneDBLogic#getLastTasId()}
 * to query for the latest content id
 * 
 * @author dzo
 * @param <R> 
 */
public class LuceneGoldStandardManager<R extends Resource & GoldStandard<?>> extends LuceneResourceManager<GoldStandardPublication> {
	
	@SuppressWarnings("unchecked")
	@Override
	protected int updateIndex(final long currentLogDate, int lastId, final long lastLogDate, final IndexType searchType) {
		/*
		 * get new posts 
		 */
		final List<LucenePost<GoldStandardPublication>> newPosts = this.dbLogic.getNewPosts(lastId);

		/*
		 * get posts to delete
		 */
		final List<Integer> contentIdsToDelete = this.dbLogic.getContentIdsToDelete(new Date(lastLogDate - QUERY_TIME_OFFSET_MS));

		/*
		 * remove new and deleted posts from the index
		 * and update field 'lastTasId'
		 */
		for (final LucenePost<GoldStandardPublication> post : newPosts) {
			final Integer contentId = post.getContentId();
			contentIdsToDelete.add(contentId);
			lastId = Math.max(contentId, lastId);
		}
		

		final Date currentDate = new Date(currentLogDate);
		
		/*
		 * add all new posts to the index 
		 */
		if (IndexType.LUCENE == searchType) {
			this.updatingIndex.deleteDocumentsInIndex(contentIdsToDelete);
			for (final LucenePost<GoldStandardPublication> post : newPosts) {
				post.setLastLogDate(currentDate);
				post.setLastTasId(lastId);
				final Document postDoc = (Document) this.resourceConverter.readPost(post, searchType);
				this.updatingIndex.insertDocument(postDoc);
			}
		} else if (IndexType.ELASTICSEARCH == searchType) {
			this.sharedIndexUpdater.setContentIdsToDelete(contentIdsToDelete);

			for (final LucenePost<GoldStandardPublication> post : newPosts) {
				post.setLastLogDate(currentDate);
				post.setLastTasId(lastId);
				final Map<String, Object> postDoc = (Map<String, Object>)this.resourceConverter.readPost(post, searchType);
				this.sharedIndexUpdater.insertDocument(postDoc);
			}			
		} else if (IndexType.BOTH == searchType) {
			this.updatingIndex.deleteDocumentsInIndex(contentIdsToDelete);
			this.sharedIndexUpdater.setContentIdsToDelete(contentIdsToDelete);
			for (final LucenePost<GoldStandardPublication> post : newPosts) {
				post.setLastTasId(lastId);
				post.setLastLogDate(currentDate);
				final Document postDoc = (Document)this.resourceConverter.readPost(post, IndexType.LUCENE);
				final Map<String, Object> postJsonDoc = (Map<String, Object>)this.resourceConverter.readPost(post, IndexType.ELASTICSEARCH);
				this.sharedIndexUpdater.insertDocument(postJsonDoc);
				this.updatingIndex.insertDocument(postDoc);
			}
				
		}
		
		return lastId;
	}
}
