package org.bibsonomy.rest.strategy.users;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author ema
 * @version $Id$
 */
public class GetFriendsForUserStrategy extends AbstractGetListStrategy<List<User>> {

	/**
	 * Request Attribute ?relation="incoming/outgoing"
	 */
	public static final String ATTRIBUTE_KEY_RELATION = "relation";
	public static final String INCOMING_ATTRIBUTE_VALUE_RELATION = "incoming";
	public static final String OUTGOING_ATTRIBUTE_VALUE_RELATION = "outgoing";
	public static final String DEFAULT_ATTRIBUTE_VALUE_RELATION = INCOMING_ATTRIBUTE_VALUE_RELATION;

	String userName = null;
	String relation = null;

	public GetFriendsForUserStrategy(Context context, String userName) {
		super(context);

		this.userName = userName;
		relation = context.getStringAttribute(ATTRIBUTE_KEY_RELATION, DEFAULT_ATTRIBUTE_VALUE_RELATION);
		if (relation == null || (!relation.equals(INCOMING_ATTRIBUTE_VALUE_RELATION) && !relation.equals(INCOMING_ATTRIBUTE_VALUE_RELATION))) {
			relation = DEFAULT_ATTRIBUTE_VALUE_RELATION;
		}
	}

	@Override
	protected void render(Writer writer, List<User> resultList) {
		this.getRenderer().serializeUsers(writer, resultList, getView());

	}

	@Override
	protected List<User> getList() {
		User requestingUser = this.getLogic().getUserDetails(userName);
		if (requestingUser == null) throw new ResourceNotFoundException("The user with the name '" + userName + "' does not exist.");

		List<User> result = new ArrayList<User>();
		if (relation.equals(INCOMING_ATTRIBUTE_VALUE_RELATION)) {
			result = this.getLogic().getUserFriends(requestingUser);
		} else if (relation.equals(OUTGOING_ATTRIBUTE_VALUE_RELATION)) {
			result = this.getLogic().getFriendsOfUser(requestingUser);
		}

		return result;
	}

	@Override
	protected StringBuilder getLinkPrefix() {
		return new StringBuilder(RestProperties.getInstance().getApiUrl() + RestProperties.getInstance().getFriendsUrl());
	}

	@Override
	protected void appendLinkPostFix(StringBuilder sb) {
	}

	@Override
	protected String getContentType() {
		return "users";
	}

}
