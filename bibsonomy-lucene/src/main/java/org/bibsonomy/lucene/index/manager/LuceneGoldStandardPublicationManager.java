package org.bibsonomy.lucene.index.manager;

import java.util.Date;
import java.util.List;

import org.apache.lucene.document.Document;
import org.bibsonomy.lucene.database.LuceneDBLogic;
import org.bibsonomy.lucene.database.LuceneGoldStandardPublicationLogic;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.lucene.util.LuceneBase;
import org.bibsonomy.lucene.util.LuceneSpringContextWrapper;
import org.bibsonomy.model.GoldStandardPublication;

/**
 * Updates the gold standard publication posts
 * uses the {@link LuceneBase#FLD_LAST_TAS_ID} for the latest content id
 * (gold standard posts have no tags)
 * 
 * {@link LuceneGoldStandardPublicationLogic} overrides {@link LuceneDBLogic#getLastTasId()}
 * to query for the latest content id
 * TODO: as soon as Lucene supports renaming fields (https://issues.apache.org/jira/browse/LUCENE-2160)
 * the latestTasId property should be renamed
 * 
 * @author dzo
 * @version $Id$
 */
public class LuceneGoldStandardPublicationManager extends LuceneResourceManager<GoldStandardPublication> {
	
	private static LuceneGoldStandardPublicationManager INSTANCE = null;

	/**
	 * @return the @{link:LuceneGoldStandardPublicationManager} instance
	 */
	public static LuceneGoldStandardPublicationManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new LuceneGoldStandardPublicationManager();
			LuceneSpringContextWrapper.init();
		}
		
		return INSTANCE;
	}

	private LuceneGoldStandardPublicationManager() {
	}
	
	@Override
	protected int updateIndex(long currentLogDate, int lastId, long lastLogDate) {
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
	    
	    this.resourceIndex.deleteDocumentsInIndex(contentIdsToDelete);

	    final Date currentDate = new Date(currentLogDate);
	    /*
	     * add all new posts to the index 
	     */
	    for (final LucenePost<GoldStandardPublication> post : newPosts) {
		post.setLastLogDate(currentDate);
	    	final Document postDoc = this.resourceConverter.readPost(post);
	    	this.resourceIndex.insertDocument(postDoc);
	    }
	    
	    return lastId;
	}
}
