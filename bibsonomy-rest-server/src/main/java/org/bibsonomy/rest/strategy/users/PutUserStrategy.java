package org.bibsonomy.rest.strategy.users;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.exceptions.ValidationException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class PutUserStrategy extends Strategy {

	private final String userName;

	public PutUserStrategy(final Context context, final String userName) {
		super(context);
		this.userName = userName;
	}

	@Override
	public void validate() throws ValidationException {
		// ensure username equals auth-username
		if (!this.userName.equals(this.context.getAuthUserName())) throw new ValidationException("The operation is not permitted for the logged-in user.");
	}

	@Override
	public void perform(final HttpServletRequest request, final Writer writer) throws InternServerException {
		try {
			final User user = this.context.getRenderer().parseUser(new InputStreamReader(request.getInputStream()));
			// ensure to use the right user name
			user.setName(this.userName);
			this.context.getLogic().storeUser(user, true);
		} catch (final IOException e) {
			throw new InternServerException(e);
		}
	}

	@Override
	public String getContentType(final String userAgent) {
		// TODO no content-contenttype
		return null;
	}
}