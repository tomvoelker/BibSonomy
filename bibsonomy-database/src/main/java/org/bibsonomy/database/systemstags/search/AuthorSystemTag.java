package org.bibsonomy.database.systemstags.search;

import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Resource;

/**
 * @author sdo
 * @version $Id$
 */
public class AuthorSystemTag extends AbstractSearchSystemTagImpl {

	/**
	 * the name of the author system tag
	 */
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
	public void handleParam(final GenericParam param) {
		param.setAuthor(this.getArgument());
		log.debug("set search to " + this.getArgument() + " after matching for author system tag");
	}

	@Override
	public boolean allowsResource(final Class<? extends Resource> resourceClass) {
		return resourceClass != null && BibTex.class.isAssignableFrom(resourceClass);
	}
}
