package org.bibsonomy.lucene.search;

import org.bibsonomy.model.GoldStandardPublication;

/**
 * @author dzo
 * @version $Id$
 */
public class LuceneSearchGoldStandardPublications extends LuceneAbstractPublicationSearch<GoldStandardPublication> {
	
	private static final LuceneSearchGoldStandardPublications INSTANCE = new LuceneSearchGoldStandardPublications();

	/**
	 * @return the @{link:LuceneSearchGoldStandardPublication} instance
	 */
	public static LuceneSearchGoldStandardPublications getInstance() {
		return INSTANCE;
	}
	
	private LuceneSearchGoldStandardPublications() {
		this.reloadIndex(0);
	}

	@Override
	protected String getResourceName() {
		return GoldStandardPublication.class.getSimpleName();
	}
}
