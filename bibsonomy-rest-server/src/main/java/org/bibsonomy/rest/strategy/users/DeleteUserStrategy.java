package org.bibsonomy.rest.strategy.users;

import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.rest.exceptions.ValidationException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class DeleteUserStrategy extends Strategy {

	private final String userName;

	public DeleteUserStrategy(final Context context, final String userName) {
		super(context);
		this.userName = userName;
	}

	@Override
	public void validate() throws ValidationException {
		if (!this.userName.equals(this.context.getAuthUserName())) throw new ValidationException("You are not authorized to perform the requested operation");
	}

	@Override
	public void perform(final HttpServletRequest request, final Writer writer) throws InternServerException {
		this.context.getLogic().deleteUser(this.userName);
	}

	@Override
	public String getContentType(final String userAgent) {
		// TODO no content-contenttype
		return null;
	}
}