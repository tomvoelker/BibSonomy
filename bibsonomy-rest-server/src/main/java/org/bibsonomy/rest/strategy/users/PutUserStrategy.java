package org.bibsonomy.rest.strategy.users;

import java.io.Reader;
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
 * @version $Id$
 */
public class PutUserStrategy extends AbstractUpdateStrategy {
	private final Reader doc;
	private final String userName;

	/**
	 * @param context
	 * @param userName
	 */
	public PutUserStrategy(final Context context, final String userName) {
		super(context);
		this.userName = userName;
		this.doc = context.getDocument();
	}

	@Override
	public String getContentType() {
		// TODO no content-contenttype
		return null;
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
		return this.getLogic().updateUser(user, UserUpdateOperation.UPDATE_ALL);
	}
}