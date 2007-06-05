package org.bibsonomy.rest.strategy.groups;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.exceptions.ValidationException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class AddUserToGroupStrategy extends Strategy {

	private final String groupName;

	public AddUserToGroupStrategy(final Context context, final String groupName) {
		super(context);
		this.groupName = groupName;
	}

	@Override
	public void validate() throws ValidationException {
		// TODO only groupmembers may add users to a group
	}

	@Override
	public void perform(final HttpServletRequest request, final Writer writer) throws InternServerException {
		try {
			final User user = this.context.getRenderer().parseUser(new InputStreamReader(request.getInputStream()));
			this.context.getLogic().addUserToGroup(this.groupName, user.getName());
		} catch (final IOException e) {
			throw new InternServerException(e);
		}
	}

	@Override
	public String getContentType(String userAgent) {
		// TODO no content-contenttype
		return null;
	}
}