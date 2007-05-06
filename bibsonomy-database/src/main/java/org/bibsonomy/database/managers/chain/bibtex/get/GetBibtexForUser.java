package org.bibsonomy.database.managers.chain.bibtex.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

public class GetBibtexForUser extends BibTexChainElement {
	/**
	 * 
	 * @author mgr
	 * 
	 */

	/*
	 * return a list of bibtex by a logged user. Following arguments have to be
	 * given:
	 * 
	 * grouping:user name:given tags:NULL hash:NULL popular:false added:false
	 * 
	 */
	@Override
	protected List<Post<BibTex>> handle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end, final Transaction transaction) {
		final BibTexParam param = new BibTexParam();
		param.setRequestedUserName(groupingName);
		param.setUserName(authUser);
		param.setOffset(start);
		int limit = end - start;
		param.setLimit(limit);

		param.setGroups(generalDb.getGroupsForUser(param, transaction));
		List<Post<BibTex>> posts = db.getBibTexForUser(param, transaction);
		if (posts.size() != 0) {
			System.out.println("GetBibtexForUser");
		}
		return posts;
	}

	/*
	 * prove arguments as mentioned above
	 */
	@Override
	protected boolean canHandle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		return authUser != null && grouping == GroupingEntity.USER && groupingName != null && (tags == null || tags.size() == 0) && hash == null && popular == false && added == false;
	}

}