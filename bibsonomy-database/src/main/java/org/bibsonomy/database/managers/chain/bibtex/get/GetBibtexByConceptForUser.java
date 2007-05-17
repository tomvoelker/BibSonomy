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
public class GetBibtexByConceptForUser extends BibTexChainElement {

	/**
	 * return a list of bibtex by a tag-concept. All bookmarks will be return
	 * for a given "super-tag". Following arguments have to be given:
	 * 
	 * grouping:user name:given tags:given hash:null popular:false added:true
	 */
	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final Transaction session) {
		param.setGroups(this.generalDb.getGroupsForUser(param, session));
		return this.db.getBibTexByConceptForUser(param, session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return param.getUserName() != null && param.getGrouping() == GroupingEntity.USER && param.getRequestedGroupName() != null && param.getTagIndex() != null && param.getHash() == null && param.isPopular() == false && param.isAdded() == true;
	}
}