package org.bibsonomy.database.systemstags.search;

import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.model.Resource;

/**
 * @author sdo
 * @version $Id$
 */
public class BibTexKeySystemTag extends AbstractSearchSystemTagImpl {

	/**
	 * the name of the BibTeX key system tag
	 */
	public static final String NAME = "bibtexkey";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public BibTexKeySystemTag newInstance() {
		return new BibTexKeySystemTag();
	}

	@Override
	public void handleParam(final GenericParam param) {
		param.setBibtexKey(this.getArgument());
		log.debug("set bibtex key to " + this.getArgument() + " after matching for bibtexkey system tag");
	}

	@Override
	public boolean allowsResource(final Class<? extends Resource> resourceClass) {
		return isPublicationClass(resourceClass);
	}
}
