package org.bibsonomy.rest.strategy.users;

import java.io.Reader;
import java.io.Writer;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class PutUserStrategy extends Strategy {
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
		if (!this.userName.equals(this.getLogic().getAuthenticatedUser())) throw new ValidationException("The operation is not permitted for the logged-in user.");
	}

	@Override
	public void perform(final Writer writer) throws InternServerException {
		final User user = this.getRenderer().parseUser(this.doc);
		// ensure to use the right user name
		user.setName(this.userName);
		this.getLogic().updateUser(user);
	}

	@Override
	public String getContentType() {
		// TODO no content-contenttype
		return null;
	}
}