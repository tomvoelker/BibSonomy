package org.bibsonomy.database.managers.chain.bibtex.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.logic.Order;

/**
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBibtexByTagNames extends BibTexChainElement {

	/**
	 * Returns a list of posts (bibtex) tagged with the given tags.
	 */
	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
		return this.db.getBibTexByTagNames(param, session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return (param.getGrouping() == GroupingEntity.ALL) && present(param.getTagIndex())&& (param.getNumSimpleConcepts() == 0) && (param.getNumSimpleTags() > 0) && (param.getNumTransitiveConcepts() == 0) && !present(param.getHash()) && nullOrEqual(param.getOrder(), Order.ADDED);
	}
}