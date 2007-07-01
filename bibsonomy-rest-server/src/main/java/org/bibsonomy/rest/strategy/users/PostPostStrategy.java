package org.bibsonomy.rest.strategy.users;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.Post;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class PostPostStrategy extends Strategy {

	private final String userName;

	public PostPostStrategy(final Context context, final String userName) {
		super(context);
		this.userName = userName;
	}

	@Override
	public void validate() throws ValidationException {
		if (!this.userName.equals(this.context.getLogic().getAuthenticatedUser())) throw new ValidationException("You are not authorized to perform the requested operation");
	}

	@Override
	public void perform(final HttpServletRequest request, final Writer writer) throws InternServerException {
		try {
			final Post<?> post = this.context.getRenderer().parsePost(new InputStreamReader(request.getInputStream()));
			this.context.getLogic().createPost(post);
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