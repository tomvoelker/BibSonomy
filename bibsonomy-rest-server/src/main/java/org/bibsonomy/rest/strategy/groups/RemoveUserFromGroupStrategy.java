package org.bibsonomy.rest.strategy.groups;

import java.io.Writer;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class RemoveUserFromGroupStrategy extends Strategy {
	private String groupName;
	private String userName;

	public RemoveUserFromGroupStrategy(final Context context, final String groupName, final String userName) {
		super(context);
		this.groupName = groupName;
		this.userName = userName;
	}

	@Override
	public void perform(final Writer writer) throws InternServerException {
		this.getLogic().removeUserFromGroup(this.groupName, this.userName);
	}

	@Override
	public String getContentType() {
		return null;
	}
}