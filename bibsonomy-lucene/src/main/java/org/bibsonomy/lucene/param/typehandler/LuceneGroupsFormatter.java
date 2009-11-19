package org.bibsonomy.lucene.param.typehandler;

import java.util.Collection;
import java.util.HashSet;

import org.bibsonomy.model.Group;

/**
 * convert date objects to a standardized string representation
 * 
 * @author fei
 */
public class LuceneGroupsFormatter extends LuceneCollectionFormatter {

	@Override
	protected Collection<? extends Object> createCollection() {
		return new HashSet<Group>();
	}

	@Override
	protected Object createItem(String token) {
		return new Group(token);
	}

	@Override
	protected String convertItem(Object item) {
		return ((Group)item).getName();
	}
}