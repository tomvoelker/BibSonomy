package org.bibsonomy.database.managers.hash.bibtex.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.hash.bibtex.BibTexHashElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;

/**
 * @author Andreas Koch
 * @version $Id$
 */
public class GetBibTexByAuthorAndTag extends BibTexHashElement {

	/**
	 * 
	 */
	public GetBibTexByAuthorAndTag() {
		setSearch(true);
		setTagIndex(true);
		setGroupingEntity(GroupingEntity.VIEWABLE);

		addToOrders(Order.ADDED);
	}

	/**
	 * return a list of bibtex by given tag/tags and author.
	 */
	@Override
	public List<Post<BibTex>> perform(final BibTexParam param, final DBSession session) {
		return this.db.getPostsByAuthorAndTag(param.getSearch(), param.getGroupId(), param.getRequestedUserName(), param.getRequestedGroupName(), param.getTagIndex(), param.getYear(), param.getFirstYear(), param.getLastYear(), param.getLimit(), param.getOffset(), session);
	}
}
