package org.bibsonomy.database.systemstags.search;

import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.systemstags.AbstractSystemTagImpl;

/**
 * @author sdo
 * @version $Id$
 */
public class BibTexKeySystemTag extends AbstractSystemTagImpl implements SearchSystemTag {
	
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
