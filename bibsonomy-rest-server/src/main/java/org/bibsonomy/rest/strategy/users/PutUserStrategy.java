package org.bibsonomy.rest.strategy.users;

import java.io.Reader;
import java.io.Writer;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.strategy.AbstractUpdateStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class PutUserStrategy extends AbstractUpdateStrategy {
	private final Reader doc;
	private final String userName;

	public PutUserStrategy(final Context context, final String userName) {
		super(context);
		this.userName = userName;
		this.doc = context.getDocument();
	}

	@Override
	public void validate() throws ValidationException {
		// ensure username equals auth-username
		// FIXME: there is no chance to retrieve the role of the user from here, but only the name (so I check admin access via name)
		if (!this.userName.equals(this.getLogic().getAuthenticatedUser()) && !this.getLogic().getAuthenticatedUser().equals("classifier")) 
			throw new ValidationException("The operation is not permitted for the logged-in user.");
	}

	@Override
	public String getContentType() {
		// TODO no content-contenttype
		return null;
	}

	@Override
	protected void render(Writer writer, String userID) {
		this.getRenderer().serializeUserId(writer, userID);
	}

	@Override
	protected String update() throws InternServerException {
		final User user = this.getRenderer().parseUser(this.doc);
		// ensure to use the right user name
		user.setName(this.userName);
		return this.getLogic().updateUser(user);
	}
}