/**
 * BibSonomy-Rest-Client - The REST-client.
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.client.queries.put;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.StringWriter;
import java.util.Set;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandard;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * Use this Class to change details of an existing post - change tags, for
 * example.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public final class ChangePostQuery extends AbstractQuery<String> {
	private final Post<? extends Resource> post;
	private final String username;
	private final String resourceHash;

	/**
	 * Changes details of an existing post.
	 * 
	 * @param username
	 *            the username under which the post is to be created
	 * @param resourceHash
	 *            hash of the resource to change
	 * @param post
	 *            the new value for the post
	 * @throws IllegalArgumentException
	 *             if
	 *             <ul>
	 *             <li>username or resourcehash are not specified</li>
	 *             <li>no resource is connected with the post</li>
	 *             <li>the resource is a bookmark: if no url is specified</li>
	 *             <li>the resource is a bibtex: if no title is specified</li>
	 *             <li>no tags are specified or the tags have no names</li>
	 *             </ul>
	 */
	public ChangePostQuery(final String username, final String resourceHash, final Post<? extends Resource> post) throws IllegalArgumentException {
		/*
		 * TODO: extract validation
		 */
		if (!present(resourceHash)) {
			throw new IllegalArgumentException("no resourceHash given");
		}

		if (!present(post)) {
			throw new IllegalArgumentException("no post specified");
		}

		final Resource resource = post.getResource();
		if (!present(resource)) {
			throw new IllegalArgumentException("no resource specified");
		}

		final boolean isCommunityPost = resource instanceof GoldStandard<?>;
		if (!isCommunityPost && !present(username)) {
			throw new IllegalArgumentException("no username set");
		}

		if (resource instanceof Bookmark) {
			final Bookmark bookmark = (Bookmark) resource;
			if (!present(bookmark.getUrl())) throw new IllegalArgumentException("no url specified in bookmark");
		}

		if (resource instanceof BibTex) {
			final BibTex publication = (BibTex) resource;
			if (!present(publication.getIntraHash())) {
				throw new IllegalArgumentException("found an publication without intrahash assigned.");
			}
		}

		final Set<Tag> tags = post.getTags();
		if (!present(tags) && !isCommunityPost) {
			throw new IllegalArgumentException("no tags specified");
		}

		for (final Tag tag : tags) {
			if (!present(tag.getName())) throw new IllegalArgumentException("missing tag name");
		}

		this.username = username;
		this.resourceHash = resourceHash;
		this.post = post;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final StringWriter sw = new StringWriter(100);
		this.getRenderer().serializePost(sw, post, null);
		final String postUrl = this.getUrlRenderer().createHrefForResource(this.username, this.resourceHash);
		this.downloadedDocument = performRequest(HttpMethod.PUT, postUrl, sw.toString());
	}
	
	@Override
	protected String getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		if (this.isSuccess()) {
			return this.getRenderer().parseResourceHash(this.downloadedDocument);
		}
		return this.getError();
	}
}