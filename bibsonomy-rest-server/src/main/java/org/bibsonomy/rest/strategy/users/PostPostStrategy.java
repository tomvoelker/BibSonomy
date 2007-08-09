package org.bibsonomy.rest.strategy.users;

import java.io.Reader;
import java.io.Writer;
import java.util.Date;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.InvalidModelException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.Post;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class PostPostStrategy extends Strategy {
	private final Reader doc;
	private final String userName;

	public PostPostStrategy(final Context context, final String userName) {
		super(context);
		this.doc = context.getDocument();
		this.userName = userName;
	}

	@Override
	public void validate() throws ValidationException {
		if (!this.userName.equals(this.getLogic().getAuthenticatedUser())) throw new ValidationException("You are not authorized to perform the requested operation");
	}

	@Override
	public void perform(final Writer writer) throws InternServerException {
		final Post<?> post = this.getRenderer().parsePost(this.doc);
		post.setDate(new Date(System.currentTimeMillis()));
		try {
			this.getLogic().createPost(post);
		}
		catch (InvalidModelException ex) {
			throw new BadRequestOrResponseException(ex);
		}
	}

	@Override
	public String getContentType() {
		// TODO no content-contenttype
		return null;
	}
}