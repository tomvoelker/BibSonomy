package org.bibsonomy.database.systemstags.search;

import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.model.Resource;

/**
 * @author dzo
 * @version $Id$
 */
public class EntryTypeSystemTag extends AbstractSearchSystemTagImpl {

	/**
	 * the name of the entry type system tag
	 */
	public static final String NAME = "entrytype";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public EntryTypeSystemTag newInstance() {
		return new EntryTypeSystemTag();
	}

	@Override
	public void handleParam(final GenericParam param) {
		param.addToSystemTags(this);
		log.debug("Set entry type to '" + this.getArgument() +"' after matching entrytype system tag");
	}

	@Override
	public boolean allowsResource(final Class<? extends Resource> resourceClass) {
		return isPublicationClass(resourceClass);
	}

}
