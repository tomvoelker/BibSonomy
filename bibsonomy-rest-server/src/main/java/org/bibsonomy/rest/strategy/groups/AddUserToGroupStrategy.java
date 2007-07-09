package org.bibsonomy.rest.strategy.groups;

import java.io.Reader;
import java.io.Writer;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class AddUserToGroupStrategy extends Strategy {
	private final Reader doc;
	private final String groupName;

	public AddUserToGroupStrategy(final Context context, final String groupName) {
		super(context);
		this.groupName = groupName;
		this.doc = context.getDocument();
	}

	@Override
	public void perform(final Writer writer) throws InternServerException {
		final User user = this.getRenderer().parseUser(this.doc);
		this.getLogic().addUserToGroup(this.groupName, user.getName());
	}

	@Override
	public String getContentType() {
		// TODO no content-contenttype
		return null;
	}
}