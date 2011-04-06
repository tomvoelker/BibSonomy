package org.bibsonomy.rest.strategy.groups;

import java.io.ByteArrayOutputStream;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class RemoveUserFromGroupStrategy extends Strategy {
	private final String groupName;
	private final String userName;

	/**
	 * @param context
	 * @param groupName
	 * @param userName
	 */
	public RemoveUserFromGroupStrategy(final Context context, final String groupName, final String userName) {
		super(context);
		this.groupName = groupName;
		this.userName = userName;
	}

	@Override
	public void perform(final ByteArrayOutputStream outStream) throws InternServerException {
		this.getLogic().deleteUserFromGroup(this.groupName, this.userName);
		// no exception -> assume success
		this.getRenderer().serializeOK(writer);
	}
}