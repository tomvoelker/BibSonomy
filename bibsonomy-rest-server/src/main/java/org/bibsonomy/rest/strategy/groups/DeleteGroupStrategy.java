package org.bibsonomy.rest.strategy.groups;

import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

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
	public void perform(final HttpServletRequest request, final Writer writer) throws InternServerException {
		this.context.getLogic().deleteGroup(this.groupName);
	}

	@Override
	public String getContentType(final String userAgent) {
		// TODO no content-contenttype
		return null;
	}
}