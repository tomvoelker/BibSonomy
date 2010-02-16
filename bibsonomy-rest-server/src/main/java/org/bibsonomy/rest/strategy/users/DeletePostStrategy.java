package org.bibsonomy.rest.strategy.users;

import java.util.Collections;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.rest.strategy.AbstractDeleteStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class DeletePostStrategy extends AbstractDeleteStrategy {

	private final String userName;
	private final String resourceHash;

	/**
	 * @param context
	 * @param userName
	 * @param resourceHash
	 */
	public DeletePostStrategy(final Context context, final String userName, final String resourceHash) {
		super(context);
		this.userName = userName;
		this.resourceHash = resourceHash;
	}

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	protected boolean delete() throws InternServerException {
		this.getLogic().deletePosts(this.userName, Collections.singletonList(this.resourceHash));
		// no exception -> assume success
		return true;
	}
}