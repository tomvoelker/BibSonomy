/**
 * BibSonomy-Rest-Client - The REST-client.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
package org.bibsonomy.rest.client.queries.post;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.StringWriter;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.bibsonomy.util.StringUtils;

/**
 * Use this Class to post a post. ;)
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public final class CreatePostQuery extends AbstractQuery<String> {
	private static final Log log = LogFactory.getLog(CreatePostQuery.class);
	
	private final Post<? extends Resource> post;
	private final String username;

	/**
	 * Creates a new post in bibsonomy.
	 * 
	 * @param username
	 *            the username under which the post is to be created
	 * @param post
	 *            the post to be created
	 * @throws IllegalArgumentException
	 *             if
	 *             <ul>
	 *             <li>the username is null or empty</li>
	 *             <li>no resource is connected with the post</li>
	 *             <li>the resource is a bookmark: if no url is specified</li>
	 *             <li>the resource is a bibtex: if no title is specified</li>
	 *             <li>no tags are specified or the tags have no names</li>
	 *             </ul>
	 */
	public CreatePostQuery(final String username, final Post<? extends Resource> post) throws IllegalArgumentException {
		if (!present(post)) {
			throw new IllegalArgumentException("no post specified");
		}

		final Resource resource = post.getResource();
		if (!present(resource)) {
			throw new IllegalArgumentException("no resource specified");
		}

		final boolean isCommunityPost = resource instanceof GoldStandard<?>;
		if (!isCommunityPost && !present(username)) {
			throw new IllegalArgumentException("no username given");
		}

		if (resource instanceof Bookmark) {
			final Bookmark bookmark = (Bookmark) resource;
			if (!present(bookmark.getUrl())) throw new IllegalArgumentException("no url specified in bookmark");
		}

		if (resource instanceof BibTex) {
			final BibTex publication = (BibTex) resource;
			if (!present(publication.getTitle())) throw new IllegalArgumentException("no title specified in bibtex");
		}

		final Set<Tag> tags = post.getTags();
		if (!present(tags) && !isCommunityPost) {
			throw new IllegalArgumentException("no tags specified");
		}

		for (final Tag tag : tags) {
			if (!present(tag.getName())) throw new IllegalArgumentException("missing tagname");
		}

		this.username = username;
		this.post = post;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final StringWriter sw = new StringWriter(100);
		this.getRenderer().serializePost(sw, this.post, null);
		final String postUrl = this.getUrlRenderer().createHrefForResource(this.username).asString();
		this.downloadedDocument = performRequest(HttpMethod.POST, postUrl, StringUtils.toDefaultCharset(sw.toString()));
	}
	
	@Override
	protected String getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		if (this.isSuccess()) {
			return this.getRenderer().parseResourceHash(this.downloadedDocument);
		}
		
		log.warn("failed to create post (" + this.getError() + ")");
		return null;
	}
}