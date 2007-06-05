package org.bibsonomy.rest.strategy.groups;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Group;
import org.bibsonomy.rest.exceptions.ValidationException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id: UpdateGroupDetailsStrategy.java,v 1.5 2007/04/15 11:05:07 mbork
 *          Exp $
 */
public class UpdateGroupDetailsStrategy extends Strategy {

	private final String groupName;

	public UpdateGroupDetailsStrategy(final Context context, final String groupName) {
		super(context);
		this.groupName = groupName;
	}

	@Override
	public void validate() throws ValidationException {
		// TODO only groupmembers may change a group?
	}

	@Override
	public void perform(final HttpServletRequest request, final Writer writer) throws InternServerException {
		try {
			// ensure right groupname
			final Group group = this.context.getRenderer().parseGroup(new InputStreamReader(request.getInputStream()));
			group.setName(this.groupName);

			this.context.getLogic().storeGroup(group, true);
		} catch (final IOException e) {
			throw new InternServerException(e);
		}
	}

	@Override
	public String getContentType(final String userAgent) {
		// TODO no content-contenttype
		return null;
	}
}