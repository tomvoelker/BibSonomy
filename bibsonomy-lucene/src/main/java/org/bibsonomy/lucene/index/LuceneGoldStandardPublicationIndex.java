package org.bibsonomy.lucene.index;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Resource;

/**
 * 
 * @author dzo
 * @version $Id$
 */
public class LuceneGoldStandardPublicationIndex extends LuceneResourceIndex<GoldStandardPublication> {

	protected LuceneGoldStandardPublicationIndex(final int indexId) {
		super(indexId);
	}

	@Override
	protected Class<? extends Resource> getResourceType() {
		return GoldStandardPublication.class;
	}
	
	@Override
	public void flush() {
		synchronized(this) {
			if( !isIndexEnabled() ) {
				return;
			}
			
			if (postsToDeleteFromIndex.size() == 0 && postsToInsert.size() == 0) {
				return;
			}
			
			// remove cached posts from index
			log.debug("Performing " + postsToDeleteFromIndex.size() + " delete operations");
			if (postsToDeleteFromIndex.size() > 0) {
				this.ensureWriteAccess();
				
				// remove each cached post from index
				for (final LucenePost<GoldStandardPublication> post : this.postsToDeleteFromIndex) {
					final String interHash = post.getResource().getInterHash();
					final Term searchTerm = new Term("interhash", interHash);
					try {
						this.indexWriter.deleteDocuments(searchTerm);
					} catch (final IOException e) {
						// TODO TODODZ
						log.error("can't delete document '" + interHash + "' from index", e);
					}
				}
			}
			
			// add cached posts to index
			log.debug("Performing " + postsToInsert.size() + " insert operations");
			if (this.postsToInsert.size() > 0) {
				this.ensureWriteAccess();
				
				for (final Document doc : this.postsToInsert) {
					try {
						this.indexWriter.addDocument(doc);
					} catch (final IOException e) {
						// TODO TODODZ
						log.error("can't add document " + doc + " to index (goldStandardPublication)", e);
					}
				}
			}
			
			// clear all cached data
			this.postsToInsert.clear();
			this.postsToDeleteFromIndex.clear();
			
			// TODO TODODZs
			this.ensureReadAccess();
			this.ensureWriteAccess();
			this.ensureReadAccess();
		}
	}
}
