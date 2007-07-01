package org.bibsonomy.rest.strategy.groups;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Group;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class AddGroupStrategy extends Strategy {

	public AddGroupStrategy(final Context context) {
		super(context);
	}

	@Override
	public void perform(final HttpServletRequest request, final Writer writer) throws InternServerException {
		try {
			final Group group = this.context.getRenderer().parseGroup(new InputStreamReader(request.getInputStream()));
			this.context.getLogic().storeGroup(group);
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