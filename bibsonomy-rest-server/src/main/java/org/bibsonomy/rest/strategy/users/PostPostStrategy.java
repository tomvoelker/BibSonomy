package org.bibsonomy.rest.strategy.users;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.Writer;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
import org.bibsonomy.rest.strategy.AbstractCreateStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class PostPostStrategy extends AbstractCreateStrategy {
	private final String userName;

	/**
	 * @param context
	 * @param userName
	 */
	public PostPostStrategy(final Context context, final String userName) {
		super(context);
		this.userName = userName;
	}

	@Override
	public void validate() throws ValidationException {
		// TODO: this check is also done by the dblogic
		if (!this.userName.equals(this.getLogic().getAuthenticatedUser().getName())) throw new ValidationException("You are not authorized to perform the requested operation");
	}

	@Override
	public String getContentType() {
		return "resourcehash";
	}

	@Override
	protected String create() throws InternServerException, BadRequestOrResponseException {
		final Post<?> post = this.parsePost();
		/*
		 * set postingdate to current time (i.e., users cannot create posts with their
		 * own (eventually faked) date
		 */
		post.setDate(new Date());
		try {
			final List<Post<?>> posts = new LinkedList<Post<?>>();
			posts.add(post);
			return this.getLogic().createPosts(posts).get(0); // throws DatabaseException
		}
		/* these 3 catches shouldn't be reached due to the ExceptionHandling in DBLogic
  		catch ( InvalidModelException ex ) {
			throw new BadRequestOrResponseException(ex.getMessage());
		}
		catch ( ResourceNotFoundException ex ) {
			throw new NoSuchResourceException(ex.getMessage());
		}
		catch (IllegalArgumentException ex) {
			// is thrown, when user already has post with this intra hash.
			throw new BadRequestOrResponseException(ex.getMessage());
		}*/
		catch (final DatabaseException de) {
			for (final String hash: de.getErrorMessages().keySet()) {
				for (final ErrorMessage em: de.getErrorMessages(hash)) {
					if (em instanceof DuplicatePostErrorMessage) {
						// duplicate post detected => handle this
						// before this would have been an IllegalArgumentException
						throw new BadRequestOrResponseException(em.toString());
					}
					if (em instanceof UpdatePostErrorMessage) {
						// a non-existing post was tried to be updated
						// this used to cause an ResourceNotFoundException
						throw new NoSuchResourceException(em.toString());
					}
					if (em instanceof IdenticalHashErrorMessage) {
						// the new post would have the same hash as an old one
						// this used to cause an IllegalArgumentException
						throw new BadRequestOrResponseException(em.toString());
					}
					if (em instanceof MissingFieldErrorMessage) {
						// some compulsory field of the post was missing
						// this used to cause an InvalidModelException
						throw new BadRequestOrResponseException(em.toString());
					}
					if (em instanceof UnspecifiedErrorMessage) {
						final Exception ex = ((UnspecifiedErrorMessage)em).getException();
						if (present(ex)) {
							if (ex instanceof InvalidModelException || ex instanceof IllegalArgumentException) {
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
	 * @return the post to create
	 */
	protected Post<? extends Resource> parsePost() {
		return this.getRenderer().parsePost(this.doc);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.AbstractCreateStrategy#render(java.io.Writer, java.lang.String)
	 */
	@Override
	protected void render(final Writer writer, final String resourceHash) {
		this.getRenderer().serializeResourceHash(writer, resourceHash);		
	}
}