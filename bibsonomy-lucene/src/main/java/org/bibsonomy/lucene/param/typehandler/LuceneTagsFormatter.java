package org.bibsonomy.lucene.param.typehandler;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.bibsonomy.model.Tag;

/**
 * convert date objects to a standardized string representation
 * 
 * @author fei
 * @version $Id$
 */
public class LuceneTagsFormatter extends LuceneCollectionFormatter<Tag> {

	@Override
	protected Collection<Tag> createCollection() {
		return new LinkedHashSet<Tag>();
	}

	@Override
	protected Tag createItem(String token) {
		return new Tag(token);
	}

	@Override
	protected String convertItem(Tag item) {
		return item.getName();
	}
}