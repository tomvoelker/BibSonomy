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
import org.bibsonomy.model.enums.Order;

/**
 * Returns a list of BibTex entries for the homepage.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBibtexForHomePage extends BibTexChainElement {

	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
		return this.db.getPostsForHomepage(param.getFilter(), param.getLimit(), param.getOffset(), param.getSystemTags().values(), session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return (param.getGrouping() == GroupingEntity.ALL &&
				!present(param.getBibtexKey()) &&
				!present(param.getTagIndex()) &&
				!(present(param.getHash())) &&
				nullOrEqual(param.getOrder(), Order.ADDED) &&
				!present(param.getTitle()) &&
				!present(param.getAuthor()) &&
				!present(param.getSearch()));
	}
}