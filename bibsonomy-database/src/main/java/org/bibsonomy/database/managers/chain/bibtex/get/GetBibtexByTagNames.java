package org.bibsonomy.database.managers.chain.bibtex.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;

/**
 * Returns a list of BibTex's tagged with the given tags.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBibtexByTagNames extends BibTexChainElement {
	
	// Tag pages can only contain public posts. Take notice when adapt it for new method / db
	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
		return this.db.getPostsByTagNames(GroupID.PUBLIC.getId(), param.getTagIndex(), param.getOrder(), param.getLimit(), param.getOffset(), session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return (param.getGrouping() == GroupingEntity.ALL &&
				!present(param.getBibtexKey()) &&
				present(param.getTagIndex()) &&
				param.getNumSimpleConcepts() == 0 &&
				param.getNumSimpleTags() > 0 &&
				param.getNumTransitiveConcepts() == 0 &&
				!present(param.getHash()) &&
				nullOrEqual(param.getOrder(), Order.ADDED, Order.FOLKRANK) &&
				!present(param.getSearch()) &&
				!present(param.getAuthor()) &&
				!present(param.getTitle()) );
	}
}