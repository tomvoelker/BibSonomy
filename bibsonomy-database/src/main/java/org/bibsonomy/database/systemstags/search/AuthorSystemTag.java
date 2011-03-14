package org.bibsonomy.database.systemstags.search;

import org.bibsonomy.database.params.GenericParam;

/**
 * @author sdo
 * @version $Id$
 */
public class AuthorSystemTag extends AbstractSearchSystemTagImpl implements SearchSystemTag {
	
	public static final String NAME = "author";

	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public AuthorSystemTag newInstance() {
		return new AuthorSystemTag();
	}

	@Override
	public void handleParam(GenericParam param) {
		param.setAuthor(this.getArgument());
		log.debug("set search to " + this.getArgument() + " after matching for author system tag");
	}


}
