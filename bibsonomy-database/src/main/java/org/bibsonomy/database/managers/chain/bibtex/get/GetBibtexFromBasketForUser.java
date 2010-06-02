package org.bibsonomy.database.managers.chain.bibtex.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * Returns a list of BibTexs contained in a certain user's basket
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class GetBibtexFromBasketForUser extends BibTexChainElement {
	
	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
		return this.db.getPostsFromBasketForUser(param.getUserName(), param.getLimit(), param.getOffset(), session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return (param.getGrouping() == GroupingEntity.BASKET &&
				present(param.getUserName()) && 
				!present(param.getBibtexKey()) &&
				!present(param.getSearch()) &&
				!present(param.getHash()) &&
				!present(param.getTagIndex())
		);
	}
}