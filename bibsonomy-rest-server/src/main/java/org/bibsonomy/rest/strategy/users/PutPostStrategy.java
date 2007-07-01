package org.bibsonomy.rest.strategy.users;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

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

	private final String userName;
	private final String resourceHash;

	public PutPostStrategy(final Context context, final String userName, final String resourceHash) {
		super(context);
		this.userName = userName;
		this.resourceHash = resourceHash;
	}

	@Override
	public void validate() throws ValidationException {
		if (!this.userName.equals(this.context.getLogic().getAuthenticatedUser())) throw new ValidationException("You are not authorized to perform the requested operation");
	}

	@Override
	public void perform(final HttpServletRequest request, final Writer writer) throws InternServerException, BadRequestOrResponseException {
		try {
			final Post<?> post = this.context.getRenderer().parsePost(new InputStreamReader(request.getInputStream()));

			// ensure using the right resource.
			// XXX: neither the client nor the REST API will calculate the new
			// hash - this will be done by the logic behind the LogicInterface!
			if (!post.getResource().getIntraHash().equals(this.resourceHash)) throw new BadRequestOrResponseException("wrong resource");
			this.context.getLogic().storePost(post);
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