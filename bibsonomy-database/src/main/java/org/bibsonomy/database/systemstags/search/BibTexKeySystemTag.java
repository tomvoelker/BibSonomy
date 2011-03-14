package org.bibsonomy.database.systemstags.search;

import org.bibsonomy.database.params.GenericParam;

/**
 * @author sdo
 * @version $Id$
 */
public class BibTexKeySystemTag extends AbstractSearchSystemTagImpl implements SearchSystemTag {
	
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
	public void handleParam(GenericParam param) {
		param.setBibtexKey(this.getArgument());
		log.debug("set bibtex key to " + this.getArgument() + " after matching for bibtexkey system tag");
	}

}
