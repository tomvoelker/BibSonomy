package org.bibsonomy.lucene.param.typehandler;

import java.util.Collection;
import java.util.HashSet;

import org.bibsonomy.model.Group;

/**
 * convert date objects to a standardized string representation
 * 
 * @author fei
 * @version $Id$
 */
public class LuceneGroupsFormatter extends LuceneCollectionFormatter<Group> {

	@Override
	protected Collection<Group> createCollection() {
		return new HashSet<Group>();
	}

	@Override
	protected Group createItem(String token) {
		return new Group(token);
	}

	@Override
	protected String convertItem(Group item) {
		return item.getName();
	}
}