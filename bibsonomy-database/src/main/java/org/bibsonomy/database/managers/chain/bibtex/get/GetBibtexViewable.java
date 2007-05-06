package org.bibsonomy.database.managers.chain.bibtex.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

public class GetBibtexViewable extends BibTexChainElement {

	/**
	 * 
	 * @author mgr
	 * 
	 */

	/*
	 * return a list of bibtex by a given group (which is only viewable for
	 * groupmembers excluded public option regarding setting a post). Following
	 * arguments have to be given:
	 * 
	 * grouping:viewable name:given tags:NULL hash:NULL popular:falses
	 * added:false
	 * 
	 */
	@Override
	protected List<Post<BibTex>> handle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end, final Transaction transaction) {
		final BibTexParam param = new BibTexParam();

		param.setRequestedGroupName(groupingName);
		param.setUserName(authUser);
		param.setOffset(start);
		int limit = end - start;
		param.setLimit(limit);

		param.setGroupId(generalDb.getGroupIdByGroupName(param, transaction));
		param.setGroups(generalDb.getGroupsForUser(param, transaction));

		List<Post<BibTex>> posts = db.getBibTexViewable(param, transaction);
		if (posts.size() != 0) {
			System.out.println("GetBibtexViewable");
		}
		return posts;
	}

	/*
	 * prove arguments as mentioned above
	 */
	@Override
	protected boolean canHandle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		return authUser != null && grouping == GroupingEntity.VIEWABLE && groupingName != null && (tags == null || tags.size() == 0) && hash == null && popular == false && added == false;
	}
}