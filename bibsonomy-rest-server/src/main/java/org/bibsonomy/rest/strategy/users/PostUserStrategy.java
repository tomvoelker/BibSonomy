package org.bibsonomy.rest.strategy.users;

import java.io.Reader;
import java.io.Writer;

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
	private final Reader doc;
	
	public PostUserStrategy(final Context context) {
		super(context);
		this.doc = context.getDocument();
	}

	@Override
	public void perform(final Writer writer) throws InternServerException, BadRequestOrResponseException {
		final User user = this.getRenderer().parseUser(this.doc);
		// check this here, because its not checked in the renderer
		if (user.getPassword() == null || user.getPassword().length() == 0) throw new BadRequestOrResponseException("missing password");
		this.getLogic().createUser(user);
	}

	@Override
	public String getContentType() {
		// TODO no content-contenttype
		return null;
	}
}