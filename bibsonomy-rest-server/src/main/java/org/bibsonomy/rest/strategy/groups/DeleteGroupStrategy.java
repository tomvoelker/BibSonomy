package org.bibsonomy.rest.strategy.groups;

import java.io.Writer;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class DeleteGroupStrategy extends Strategy {
	private final String groupName;

	public DeleteGroupStrategy(final Context context, final String groupName) {
		super(context);
		this.groupName = groupName;
	}

	@Override
	public void perform(final Writer writer) throws InternServerException {
		this.getLogic().deleteGroup(this.groupName);
	}

	@Override
	public String getContentType() {
		// TODO no content-contenttype
		return null;
	}
}