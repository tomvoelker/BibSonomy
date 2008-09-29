package org.bibsonomy.rest.strategy.users;

import java.io.Writer;
import java.util.Date;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.InvalidModelException;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.Post;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.AbstractCreateStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class PostPostStrategy extends AbstractCreateStrategy {
	private final String userName;

	public PostPostStrategy(final Context context, final String userName) {
		super(context);
		this.userName = userName;
	}

	@Override
	public void validate() throws ValidationException {
		if (!this.userName.equals(this.getLogic().getAuthenticatedUser().getName())) throw new ValidationException("You are not authorized to perform the requested operation");
	}

	@Override
	public String getContentType() {
		return "resourcehash";
	}

	@Override
	protected String create() throws InternServerException, BadRequestOrResponseException {
		final Post<?> post = this.getRenderer().parsePost(this.doc);
		post.setDate(new Date(System.currentTimeMillis()));
		try {
			return this.getLogic().createPost(post);
		}
		catch ( InvalidModelException ex ) {
			throw new BadRequestOrResponseException(ex.getMessage());
		}
		catch ( ResourceNotFoundException ex ) {
			throw new NoSuchResourceException(ex.getMessage());
		}
		catch (IllegalArgumentException ex) {
			/*
			 * is thrown, when user already has post with this intra hash.
			 */
			throw new BadRequestOrResponseException(ex.getMessage());
		}
	}

	@Override
	protected void render(Writer writer, String resourceHash) {
		this.getRenderer().serializeResourceHash(writer, resourceHash);		
	}
}