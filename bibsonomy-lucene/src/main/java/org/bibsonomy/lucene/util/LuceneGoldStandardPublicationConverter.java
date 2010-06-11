package org.bibsonomy.lucene.util;

import org.bibsonomy.model.GoldStandardPublication;

/**
 * @author dzo
 * @version $Id$
 */
public class LuceneGoldStandardPublicationConverter extends LuceneResourceConverter<GoldStandardPublication> {

	@Override
	protected GoldStandardPublication createNewResource() {
		return new GoldStandardPublication();
	}

}
