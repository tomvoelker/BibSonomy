package org.bibsonomy.rest.strategy.groups;

import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.util.List;

import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
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
		/*
		 * parse users
		 */
		final List<User> users = this.getRenderer().parseUserList(this.doc);
		/*
		 * create empty group with desired name
		 */
		final Group group = new Group(this.groupName);
		/*
		 * all users which are written here into the group are ADDED to the group - i.e., existing
		 * users within the group are not touched. 
		 */ 
		group.setUsers(users);
		/*
		 * add users to group
		 */
		try {
			this.getLogic().updateGroup(group, GroupUpdateOperation.ADD_NEW_USER);
		}
		catch (ValidationException ve) {
			throw new BadRequestOrResponseException(ve.getMessage());
		}
		/*
		 * no exception -> assume success
		 */
		this.getRenderer().serializeGroupId(this.writer, this.groupName); // serializeOK(this.writer);
	}
}