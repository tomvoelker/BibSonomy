package org.bibsonomy.database.managers.hash.bibtex.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.managers.hash.bibtex.BibTexHashElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * Returns a list of BibTex's for a given hash and a user.
 * 
 * @author Andreas Koch
 * @version $Id$
 */
public class GetBibtexByHashForUser extends BibTexHashElement {

	/**
	 * TODO: improve documentation
	 */
	public GetBibtexByHashForUser() {
		setHash(true);
		setRequestedUserName(true);
		setGroupingEntity(GroupingEntity.USER);
	}

	/**
	 * return a list of bibtex by a given hash and a logged user.
	 */
	@Override
	public List<Post<BibTex>> perform(final BibTexParam param, final DBSession session) {
		return this.db.getPostsByHashForUser(param.getUserName(), param.getHash(), param.getRequestedUserName(), param.getGroups(), HashID.getSimHash(param.getSimHash()), session);
	}
}