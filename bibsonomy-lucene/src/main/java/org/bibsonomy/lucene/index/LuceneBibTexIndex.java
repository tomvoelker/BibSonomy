package org.bibsonomy.lucene.index;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Resource;

/**
 * TODO: rename to LucenePublicationIndex
 * class for managing the lucene bibtex index
 * 
 * @author fei
 * @version $Id$
 */
public class LuceneBibTexIndex extends LuceneResourceIndex<BibTex> {

	protected LuceneBibTexIndex(int indexId) {
		super(indexId);
	}

	@Override
	protected Class<? extends Resource> getResourceType() {
		return BibTex.class;
	}

}
