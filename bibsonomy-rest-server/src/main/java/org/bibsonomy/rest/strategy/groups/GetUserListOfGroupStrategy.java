package org.bibsonomy.rest.strategy.groups;

import java.io.Writer;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetUserListOfGroupStrategy extends AbstractGetListStrategy<List<User>> {

	private final String groupName;

	/**
	 * Constructor
	 * 
	 * @param context - the context of the request
	 * @param groupName - the group name
	 */
	public GetUserListOfGroupStrategy(final Context context, final String groupName) {
		super(context);
		this.groupName = groupName;
	}

	@Override
	public String getContentType() {
		return "users";
	}

	@Override
	protected void appendLinkPostFix(StringBuilder sb) {
	}

	@Override
	protected StringBuilder getLinkPrefix() {
		return new StringBuilder( RestProperties.getInstance().getApiUrl() ).append( RestProperties.getInstance().getGroupsUrl() ).append("/").append(groupName).append("/").append( RestProperties.getInstance().getUsersUrl() );
	}

	@Override
	protected List<User> getList() {
		return this.getLogic().getUsers(null, GroupingEntity.GROUP, this.groupName, null, null, null, null, null, getView().getStartValue(), getView().getEndValue());
	}

	@Override
	protected void render(Writer writer, List<User> resultList) {
		this.getRenderer().serializeUsers(writer, resultList, getView());
	}
}