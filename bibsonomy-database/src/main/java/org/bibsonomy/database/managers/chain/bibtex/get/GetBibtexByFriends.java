package org.bibsonomy.database.managers.chain.bibtex.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * TODO check
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBibtexByFriends extends BibTexChainElement {

	/**
	 * TODO extension with user restriction rearding returned bibtex and
	 * appropriate namming of URL in REST interface
	 * 
	 * grouping:friend name:given tags:NULL hash:NULL popular:false added:false
	 * 
	 * /user/friend
	 */
	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final Transaction session) {
		return this.db.getBibTexByUserFriends(param, session);
	}

	/*
	 * TODO username: semantik fehlt in API
	 */
	@Override
	protected boolean canHandle(final BibTexParam param) {
		return param.getUserName() != null && param.getGrouping() == GroupingEntity.FRIEND && param.getRequestedGroupName() != null && param.getTagIndex() == null && param.getHash() == null && param.isPopular() == false && param.isAdded() == false;
	}
}