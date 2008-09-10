package org.bibsonomy.rest.strategy.users;

import java.io.Writer;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.InvalidModelException;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.Post;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.AbstractUpdateStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class PutPostStrategy extends AbstractUpdateStrategy {
	private final String userName;
	private final String resourceHash;

	public PutPostStrategy(final Context context, final String userName, final String resourceHash) {
		super(context);
		this.userName = userName;
		this.resourceHash = resourceHash;
	}

	@Override
	public void validate() throws ValidationException {
		if (!this.userName.equals(this.getLogic().getAuthenticatedUser().getName())) throw new ValidationException("You are not authorized to perform the requested operation++");
	}

	@Override
	public String getContentType() {
		// TODO no content-contenttype
		return null;
	}

	@Override
	protected void render(Writer writer, String newResourceHash) {
		this.getRenderer().serializeResourceHash(writer, newResourceHash);		
	}

	@Override
	protected String update() throws InternServerException, BadRequestOrResponseException {
		final Post<?> post = this.getRenderer().parsePost(this.doc);
		// ensure using the right resource.
		// XXX: neither the client nor the REST API will calculate the new
		// hash - this will be done by the logic behind the LogicInterface!
		if (post.getResource().getInterHash() != null && !post.getResource().getIntraHash().equals(this.resourceHash)) throw new BadRequestOrResponseException("wrong resource");
		try {
			return this.getLogic().updatePost(post);
		}
		catch (InvalidModelException ex) {
			throw new BadRequestOrResponseException(ex);
		}
		catch ( ResourceNotFoundException ex ) {
			throw new NoSuchResourceException(ex.getMessage());
		}		
	}
}