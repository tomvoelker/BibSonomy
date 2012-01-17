package org.bibsonomy.rest.strategy.groups;

import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.util.List;

import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class AddUserToGroupStrategy extends Strategy {
	private final Reader doc;
	private final String groupName;
	
	/**
	 * @param context
	 * @param groupName
	 */
	public AddUserToGroupStrategy(final Context context, final String groupName) {
		super(context);
		this.groupName = groupName;
		this.doc = context.getDocument();
	}

	@Override
	public void perform(final ByteArrayOutputStream outStream) throws InternServerException {
		final List<User> users = this.getRenderer().parseUserList(this.doc);
		final Group group = new Group(this.groupName);
		group.setUsers(users);
		this.getLogic().updateGroup(group, GroupUpdateOperation.ADD_NEW_USER);
		// no exception -> assume success
		this.getRenderer().serializeOK(this.writer);
	}
}