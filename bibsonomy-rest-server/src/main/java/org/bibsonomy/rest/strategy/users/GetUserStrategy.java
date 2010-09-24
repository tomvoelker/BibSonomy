package org.bibsonomy.rest.strategy.users;

import java.io.ByteArrayOutputStream;
import java.io.Writer;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;
import org.bibsonomy.rest.util.EscapingPrintWriter;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetUserStrategy extends Strategy {

	private final String userName;
	private Writer writer;

	/**
	 * @param context
	 * @param userName
	 */
	public GetUserStrategy(final Context context, final String userName) {
		super(context);
		this.userName = userName;
	}

	@Override
	public void perform(final ByteArrayOutputStream outStream) throws InternServerException, NoSuchResourceException {
		writer = new EscapingPrintWriter(outStream);
		final User user = this.getLogic().getUserDetails(userName);
		// user cannot be null - if user is not found, an empty user object is given back by getUserDetails.
		// -> check for user name being null
		if (user.getName() == null) {
			throw new NoSuchResourceException("The requested user '" + userName + "' does not exist.");
		}
		//
		// delegate to the renderer
		this.getRenderer().serializeUser(writer, user, new ViewModel());
	}

	@Override
	public String getContentType() {
		return "user";
	}
}