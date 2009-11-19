package org.bibsonomy.lucene.param.typehandler;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.bibsonomy.model.Tag;

/**
 * convert date objects to a standardized string representation
 * 
 * @author fei
 */
public class LuceneTagsFormatter extends LuceneCollectionFormatter {

	@Override
	protected Collection<? extends Object> createCollection() {
		return new LinkedHashSet<Tag>();
	}

	@Override
	protected Object createItem(String token) {
		return new Tag(token);
	}

	@Override
	protected String convertItem(Object item) {
		return ((Tag)item).getName();
	}
}