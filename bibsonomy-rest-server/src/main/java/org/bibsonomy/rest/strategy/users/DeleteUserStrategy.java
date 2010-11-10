package org.bibsonomy.rest.strategy.users;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.rest.strategy.AbstractDeleteStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class DeleteUserStrategy extends AbstractDeleteStrategy {

	private final String userName;
	
	/**
	 * @param context
	 * @param userName
	 */
	public DeleteUserStrategy(final Context context, final String userName) {
		super(context);
		this.userName = userName;
	}

	@Override
	protected boolean delete() throws InternServerException {
		this.getLogic().deleteUser(this.userName);
		// no exception -> assume success
		return true;
	}
}