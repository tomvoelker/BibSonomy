package org.bibsonomy.rest.strategy.users;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.Writer;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.errors.DuplicatePostErrorMessage;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.errors.IdenticalHashErrorMessage;
import org.bibsonomy.common.errors.MissingFieldErrorMessage;
import org.bibsonomy.common.errors.UnspecifiedErrorMessage;
import org.bibsonomy.common.errors.UpdatePostErrorMessage;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.InvalidModelException;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.common.exceptions.database.DatabaseException;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
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
	protected final String resourceHash;

	/**
	 * Create new PutPostStrategy
	 * 
	 * @param context
	 * 			- the context of the request
	 * @param userName
	 * 			- user name of the user who submitted the request
	 * @param resourceHash
	 * 			- intraHash of the resource to be updated
	 */
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
		return "resourcehash";
	}

	@Override
	protected void render(Writer writer, String newResourceHash) {
		this.getRenderer().serializeResourceHash(writer, newResourceHash);		
	}

	@Override
	protected String update() throws InternServerException, BadRequestOrResponseException {
		final Post<?> post = this.getPost();
		/*
		 * XXX: neither the client nor the REST API will calculate the new
		 * hash - this will be done by the logic behind the LogicInterface!
		 */ 		
		try {
			final List<Post<?>> posts = new LinkedList<Post<?>>();
			posts.add(post);
			return this.getLogic().updatePosts(posts, PostUpdateOperation.UPDATE_ALL).get(0); //throws DatabaseException
		}
/*		these 2 catches shouldn't be reached due to the ExceptionHandling in DBLogic
  		catch (InvalidModelException ex) {
			throw new BadRequestOrResponseException(ex);
		}
		catch ( ResourceNotFoundException ex ) {
			throw new NoSuchResourceException(ex.getMessage());
		}*/		
		catch ( DatabaseException de ) {
			for (String hash: de.getErrorMessages().keySet()) {
				for (ErrorMessage em: de.getErrorMessages(hash)) {
					if (em instanceof DuplicatePostErrorMessage ) {
						// duplicate post detected => handle this
						// before this would have been an IllegalArgumentException
						throw new BadRequestOrResponseException(em.toString());
					}
					if ( em instanceof UpdatePostErrorMessage ) {
						// a non-existing post was tried to be updated
						// this used to cause an ResourceNotFoundException
						throw new NoSuchResourceException(em.toString());
					}
					if ( em instanceof IdenticalHashErrorMessage ) {
						// the new post would have the same hash as an old one
						// this used to cause an IllegalArgumentException
						throw new BadRequestOrResponseException(em.toString());
					}
					if (em instanceof MissingFieldErrorMessage ) {
						// some compulsory field of the post was missing
						// this used to cause an InvalidModelException
						throw new BadRequestOrResponseException(em.toString());
					}
					if (em instanceof UnspecifiedErrorMessage) {
						Exception ex = ((UnspecifiedErrorMessage)em).getException();
						if (present(ex)) {
							if (ex instanceof InvalidModelException) {
								throw new BadRequestOrResponseException(ex.getMessage());
							}
							if (ex instanceof ResourceNotFoundException) {
								throw new NoSuchResourceException(ex.getMessage());
							}
						}
					}
				}
			}
			// If none of the errors handled above occurred we throw the original Exception
			throw de;
		}
	}

	/**
	 * @return the post to update
	 */
	protected Post<? extends Resource> getPost() {
		final Post<? extends Resource> post = this.getRenderer().parsePost(this.doc);
		/*
		 * set postingdate to current time
		 */
		post.setDate(new Date(System.currentTimeMillis()));				
		/*
		 * set the (old) intrahash of the resource as specified in the URL
		 */
		post.getResource().setIntraHash(this.resourceHash);
		return post;
	}
}