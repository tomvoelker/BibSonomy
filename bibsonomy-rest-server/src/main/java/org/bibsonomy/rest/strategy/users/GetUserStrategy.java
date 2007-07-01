package org.bibsonomy.rest.strategy.users;

import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetUserStrategy extends Strategy {

	private final String userName;

	public GetUserStrategy(final Context context, final String userName) {
		super(context);
		this.userName = userName;
	}

	@Override
	public void perform(final HttpServletRequest request, final Writer writer) throws InternServerException, NoSuchResourceException {
		final User user = this.context.getLogic().getUserDetails(userName);
		if (user == null) throw new NoSuchResourceException("The requested user '" + this.userName + "' does not exist.");
		// delegate to the renderer
		this.context.getRenderer().serializeUser(writer, user, new ViewModel());
	}

	@Override
	public String getContentType(final String userAgent) {
		if (this.context.apiIsUserAgent(userAgent)) return "bibsonomy/user+" + this.context.getRenderingFormat().toString();
		return RestProperties.getInstance().getContentType();
	}
}