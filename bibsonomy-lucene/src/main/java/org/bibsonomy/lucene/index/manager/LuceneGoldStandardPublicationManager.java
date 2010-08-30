package org.bibsonomy.lucene.index.manager;

import java.util.Date;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.lucene.util.LuceneBase;
import org.bibsonomy.lucene.util.LuceneSpringContextWrapper;
import org.bibsonomy.model.GoldStandardPublication;

/**
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
			INSTANCE.recovery();
		}
		
		return INSTANCE;
	}

	private LuceneGoldStandardPublicationManager() {
	}
	
	@Override
	protected void updatePredictions() {
		// currently nothing to do
	}
	
	@Override
	protected void updateIndexes() {
		synchronized(this) {
			// set the active resource index
			this.resourceIndex = this.resourceIndices.get(idxSelect);
			
			final Date now = new Date();
			final long lastDateLong = this.resourceIndex.getLastDate();
			final Date lastDate = new Date(lastDateLong);
			
			//  1. get new posts
			final List<LucenePost<GoldStandardPublication>> newPosts = this.dbLogic.getNewPosts(lastDate, now);
			for (final LucenePost<GoldStandardPublication> newPost : newPosts) {
				final Document doc = this.resourceConverter.readPost(newPost);
				
				doc.add(new Field(LuceneBase.FLD_LAST_DATE, String.valueOf(now.getTime()), Store.YES, Index.NOT_ANALYZED));
				this.resourceIndex.insertDocument(doc);
			}
			
			// 2. get posts to delete
			final List<LucenePost<GoldStandardPublication>> oldPosts = this.dbLogic.getPostsToDelete(lastDate, now);
			this.resourceIndex.addAllToPostsToDeleteFromIndex(oldPosts);

			// 3. commit changes 
			this.resourceIndex.flush();
			
			// 4. update variables 
			this.resourceIndex.setLastDate(now.getTime());
		}
		
		// all done.
		alreadyRunning = 0;
	}
}
