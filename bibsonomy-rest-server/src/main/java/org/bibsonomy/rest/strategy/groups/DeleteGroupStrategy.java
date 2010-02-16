package org.bibsonomy.rest.strategy.groups;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.rest.strategy.AbstractDeleteStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class DeleteGroupStrategy extends AbstractDeleteStrategy {
	private final String groupName;
	
	/**
	 * @param context
	 * @param groupName
	 */
	public DeleteGroupStrategy(final Context context, final String groupName) {
		super(context);
		this.groupName = groupName;
	}

	@Override
	public String getContentType() {
		// TODO no content-contenttype
		return null;
	}

	@Override
	protected boolean delete() throws InternServerException {
		this.getLogic().deleteGroup(this.groupName);
		// no exceptions at this point - assume success
		return true;
	}
}