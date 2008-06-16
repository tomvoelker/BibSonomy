package org.bibsonomy.database.managers.chain.bibtex.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * Returns a list of BibTex's for a given search.
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class GetBibtexSearch extends BibTexChainElement {

	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
		return this.db.getBibTexSearch(param, session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return  (param.getGrouping() == GroupingEntity.ALL) && 
				!present(param.getBibtexKey()) && 
				present(param.getSearch());
	}
}