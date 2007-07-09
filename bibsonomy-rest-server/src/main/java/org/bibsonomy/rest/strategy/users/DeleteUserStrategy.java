package org.bibsonomy.rest.strategy.users;

import java.io.Writer;

import org.bibsonomy.common.exceptions.InternServerException;
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
	public void perform(final Writer writer) throws InternServerException {
		this.getLogic().deleteUser(this.userName);
	}

	@Override
	public String getContentType() {
		// TODO no content-contenttype
		return null;
	}
}