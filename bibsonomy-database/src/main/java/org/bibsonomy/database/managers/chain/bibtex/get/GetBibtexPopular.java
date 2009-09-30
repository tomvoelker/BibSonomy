package org.bibsonomy.database.managers.chain.bibtex.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;

/**
 * Return all popular BibTex entries.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBibtexPopular extends BibTexChainElement {

	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
		return this.db.getPostsPopular(param.getDays(), param.getLimit(), param.getOffset(), HashID.getSimHash(param.getSimHash()), session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return (param.getGrouping() == GroupingEntity.ALL &&
				!present(param.getBibtexKey()) &&
				param.getDays() >= 0 &&
				!present(param.getHash()) &&
				nullOrEqual(param.getOrder(), Order.POPULAR) &&
				!present(param.getSearch()));
	}
}