/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.strategy.users;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.Writer;
import java.util.Arrays;

import org.bibsonomy.common.errors.DuplicatePostErrorMessage;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.errors.IdenticalHashErrorMessage;
import org.bibsonomy.common.errors.MissingFieldErrorMessage;
import org.bibsonomy.common.errors.UnspecifiedErrorMessage;
import org.bibsonomy.common.errors.UpdatePostErrorMessage;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.common.exceptions.DatabaseException;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.InvalidModelException;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.AbstractCreateStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
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
	public void canAccess() {
		if (!this.userName.equalsIgnoreCase(this.getLogic().getAuthenticatedUser().getName())) throw new AccessDeniedException();
	}

	@Override
	public String getContentType() {
		return "resourcehash";
	}

	@Override
	protected String create() throws InternServerException, BadRequestOrResponseException {
		final Post<? extends Resource> post = this.parsePost();
		try {
			return this.getLogic().createPosts(Arrays.<Post<?>>asList(post)).get(0);
		} catch (final DatabaseException de) {
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
							if (ex instanceof ObjectNotFoundException) {
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
		return this.getRenderer().parsePost(this.doc, this.getUploadAccessor());
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