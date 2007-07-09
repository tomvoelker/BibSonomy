package org.bibsonomy.rest.strategy.users;

import java.io.Reader;
import java.io.Writer;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.Post;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class PutPostStrategy extends Strategy {
	private final Reader doc;
	private final String userName;
	private final String resourceHash;

	public PutPostStrategy(final Context context, final String userName, final String resourceHash) {
		super(context);
		this.userName = userName;
		this.resourceHash = resourceHash;
		this.doc = context.getDocument();
	}

	@Override
	public void validate() throws ValidationException {
		if (!this.userName.equals(this.getLogic().getAuthenticatedUser())) throw new ValidationException("You are not authorized to perform the requested operation");
	}

	@Override
	public void perform(final Writer writer) throws InternServerException, BadRequestOrResponseException {
		final Post<?> post = this.getRenderer().parsePost(this.doc);
		// ensure using the right resource.
		// XXX: neither the client nor the REST API will calculate the new
		// hash - this will be done by the logic behind the LogicInterface!
		if (!post.getResource().getIntraHash().equals(this.resourceHash)) throw new BadRequestOrResponseException("wrong resource");
		this.getLogic().updatePost(post);
	}

	@Override
	public String getContentType() {
		// TODO no content-contenttype
		return null;
	}
}