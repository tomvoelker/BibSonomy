package org.bibsonomy.database.managers.chain.bibtex.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBibtexByHash extends BibTexChainElement {

	/**
	 * return a list of bibtex by a given hash. Following arguments have to be
	 * given:
	 * 
	 * grouping:all name:irrelevant tags:NULL hash:given popular:false
	 * added:false
	 */
	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final Transaction session) {
		return this.db.getBibTexByHash(param, session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return param.getHash() != null && param.getHash().length() > 0 && param.getGrouping() == GroupingEntity.ALL && param.getTagIndex() == null && param.isPopular() == false && param.isAdded() == false;
	}
}