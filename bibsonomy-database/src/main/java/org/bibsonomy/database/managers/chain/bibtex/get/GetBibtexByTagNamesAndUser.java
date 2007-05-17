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
public class GetBibtexByTagNamesAndUser extends BibTexChainElement {

	/**
	 * return a list of bibtex by given tag/tags and User. Following arguments
	 * have to be given:
	 * 
	 * grouping:User name:given tags:given hash:null popular:false added:false
	 */
	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final Transaction session) {
		return this.db.getBibTexByTagNamesForUser(param, session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return (param.getUserName() != null) && (param.getGrouping() == GroupingEntity.USER) && (param.getTagIndex() != null) && (param.getRequestedUserName() != null) && (param.getRequestedUserName().length() > 0) && ((param.getHash() == null) || (param.getHash().length() == 0)) && (param.isPopular() == false);
	}

}
