package org.bibsonomy.rest.strategy.clipboard;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.users.GetUserPostsStrategy;

/**
 * @author wla
 * @version $Id$
 */
public class GetClipboardStrategy extends GetUserPostsStrategy {

	/**
	 * 
	 * @param context
	 * @param userName 
	 */
	public GetClipboardStrategy(final Context context, final String userName) {
		super(context, userName);
		resourceType = BibTex.class;
	}

	@Override
	protected List<? extends Post<? extends Resource>> getList() {
		return this.getLogic().getPosts(this.resourceType, GroupingEntity.BASKET, this.userName, this.tags, null, this.search, null, null, null, null, this.getView().getStartValue(),
				this.getView().getEndValue());
	}

}
