package org.bibsonomy.rest.strategy.users;

import java.io.Writer;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.strategy.AbstractCreateStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class PostUserStrategy extends AbstractCreateStrategy {
	
	/**
	 * @param context
	 */
	public PostUserStrategy(final Context context) {
		super(context);
	}

	@Override
	protected String create() throws InternServerException, BadRequestOrResponseException {
		final User user = this.getRenderer().parseUser(this.doc);
		// (old comment) check this here, because its not checked in the renderer
		/*
		 * FIXME: our renderer does not render the password - thus we can't 
		 * expect one here. Furthermore, probably more checks are necessary. 
		 */
		// if (user.getPassword() == null || user.getPassword().length() == 0) throw new BadRequestOrResponseException("missing password");
		return this.getLogic().createUser(user);
	}

	@Override
	protected void render(Writer writer, String userID) {
		this.getRenderer().serializeUserId(writer, userID);		
	}
}