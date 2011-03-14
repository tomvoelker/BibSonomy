package org.bibsonomy.database.systemstags.search;

import org.bibsonomy.database.params.GenericParam;

/**
 * @author dzo
 * @version $Id$
 */
public class EntryTypeSystemTag extends AbstractSearchSystemTagImpl implements SearchSystemTag {

	public static final String NAME = "entrytype";

	@Override
	public String getName() {
		return NAME;
	}

	public EntryTypeSystemTag newInstance() {
		return new EntryTypeSystemTag();
	}

	@Override
	public void handleParam(GenericParam param) {
		param.addToSystemTags(this);
		log.debug("Set entry type to '" + this.getArgument() +"' after matching entrytype system tag");
	}

}
