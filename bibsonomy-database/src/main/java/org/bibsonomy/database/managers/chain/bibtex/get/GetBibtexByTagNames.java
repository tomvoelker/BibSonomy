package org.bibsonomy.database.managers.chain.bibtex.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.Order;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * TODO implement compartible method for concept structure
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBibtexByTagNames extends BibTexChainElement {

	/**
	 * Returns a list of posts (bibtex) tagged with the given tags.
	 */
	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final Transaction session) {
		return this.db.getBibTexByTagNames(param, session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return (param.getGrouping() == GroupingEntity.ALL) && present(param.getTagIndex())&& (param.getNumSimpleConcepts() == 0) && (param.getNumSimpleTags() > 0) && (param.getNumTransitiveConcepts() == 0) && !present(param.getHash()) && nullOrEqual(param.getOrder(), Order.ADDED);
	}
}