package org.bibsonomy.rest.strategy.users;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.User;
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
		writer = new PrintWriter(outStream);
		final User user = this.getLogic().getUserDetails(userName);
		if (user == null) throw new NoSuchResourceException("The requested user '" + this.userName + "' does not exist.");
		// delegate to the renderer
		this.getRenderer().serializeUser(writer, user, new ViewModel());
	}

	@Override
	public String getContentType() {
		return "user";
	}
}