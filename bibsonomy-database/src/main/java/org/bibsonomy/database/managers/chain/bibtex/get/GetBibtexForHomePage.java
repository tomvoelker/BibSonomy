package org.bibsonomy.database.managers.chain.bibtex.get;

import java.util.List;

import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBibtexForHomePage extends BibTexChainElement {

	/**
	 * return a list of bibtex by a logged user. Following arguments have to be
	 * given:
	 * 
	 * grouping:null name:irrelevant tags:irrelevant hash:irrelevant
	 * popular:false added:false
	 */
	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final Transaction session) {
		return this.db.getBibTexForHomePage(param, session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return param.getGrouping() == null && param.isPopular() == false && param.isAdded() == false;
	}
}