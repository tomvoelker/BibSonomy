package org.bibsonomy.lucene.index;

import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Resource;

/**
 * 
 * @author dzo
 * @version $Id$
 */
public class LuceneGoldStandardPublicationIndex extends LuceneResourceIndex<GoldStandardPublication> {

	protected LuceneGoldStandardPublicationIndex(int indexId) {
		super(indexId);
	}

	@Override
	protected Class<? extends Resource> getResourceType() {
		return GoldStandardPublication.class;
	}

}
