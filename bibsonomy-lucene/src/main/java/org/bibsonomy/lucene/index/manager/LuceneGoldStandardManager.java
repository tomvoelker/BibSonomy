package org.bibsonomy.lucene.index.manager;

import java.util.Date;
import java.util.List;

import org.apache.lucene.document.Document;
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
 * @version $Id$
 * @param <R> 
 */
public class LuceneGoldStandardManager<R extends Resource & GoldStandard<?>> extends LuceneResourceManager<GoldStandardPublication> {
	
	@Override
	protected int updateIndex(final long currentLogDate, int lastId, final long lastLogDate) {
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
	    
	    this.updatingIndex.deleteDocumentsInIndex(contentIdsToDelete);

	    final Date currentDate = new Date(currentLogDate);
	    
	    /*
	     * add all new posts to the index 
	     */
	    for (final LucenePost<GoldStandardPublication> post : newPosts) {
	    	post.setLastLogDate(currentDate);
	    	post.setLastTasId(lastId);
	    	final Document postDoc = this.resourceConverter.readPost(post);
	    	this.updatingIndex.insertDocument(postDoc);
	    }
	    
	    return lastId;
	}
}
