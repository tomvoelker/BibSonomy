package org.bibsonomy.rest.strategy.users;

import java.io.Writer;

import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.strategy.AbstractUpdateStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * strategy for updating an user
 * 		- users/USERNAME (HTTP-Method: PUT)
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public class PutUserStrategy extends AbstractUpdateStrategy {
	private final String userName;

	/**
	 * @param context
	 * @param userName
	 */
	public PutUserStrategy(final Context context, final String userName) {
		super(context);
		this.userName = userName;
	}

	@Override
	protected void render(final Writer writer, final String userID) {
		this.getRenderer().serializeUserId(writer, userID);
	}

	@Override
	protected String update() throws InternServerException {
		final User user = this.getRenderer().parseUser(this.doc);
		// ensure to use the right user name
		user.setName(this.userName);
		/*
		 * FIXME: better heuristic! e.g., ensure that
		 * - calling user is admin (?)
		 */
		final UserUpdateOperation userUpdateOperation;
		if ((user.getPrediction() != null) || (user.getSpammer() != null)) {
			userUpdateOperation = UserUpdateOperation.UPDATE_SPAMMER_STATUS;
		} else {
			userUpdateOperation = UserUpdateOperation.UPDATE_ALL;
		}
		return this.getLogic().updateUser(user, userUpdateOperation);
	}
}