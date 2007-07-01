package org.bibsonomy.rest.strategy.users;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class PostUserStrategy extends Strategy {

	public PostUserStrategy(final Context context) {
		super(context);
	}

	@Override
	public void perform(final HttpServletRequest request, final Writer writer) throws InternServerException, BadRequestOrResponseException {
		try {
			final User user = this.context.getRenderer().parseUser(new InputStreamReader(request.getInputStream()));
			// check this here, because its not checked in the renderer
			if (user.getPassword() == null || user.getPassword().length() == 0) throw new BadRequestOrResponseException("missing password");
			this.context.getLogic().storeUser(user);
		} catch (final IOException e) {
			throw new InternServerException(e);
		}
	}

	@Override
	public String getContentType(String userAgent) {
		// TODO no content-contenttype
		return null;
	}
}