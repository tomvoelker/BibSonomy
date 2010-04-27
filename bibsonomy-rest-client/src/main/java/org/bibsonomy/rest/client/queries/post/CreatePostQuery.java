/**
 *  
 *  BibSonomy-Rest-Client - The REST-client.
 *   
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.rest.client.queries.post;

import java.io.StringWriter;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.RendererFactory;

/**
 * Use this Class to post a post. ;)
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class CreatePostQuery extends AbstractQuery<String> {
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
		if (username == null || username.length() == 0) throw new IllegalArgumentException("no username given");
		if (post == null) throw new IllegalArgumentException("no post specified");
		if (post.getResource() == null) throw new IllegalArgumentException("no resource specified");

		if (post.getResource() instanceof Bookmark) {
			final Bookmark bookmark = (Bookmark) post.getResource();
			if (bookmark.getUrl() == null || bookmark.getUrl().length() == 0) throw new IllegalArgumentException("no url specified in bookmark");
		}

		if (post.getResource() instanceof BibTex) {
			final BibTex bibtex = (BibTex) post.getResource();
			if (bibtex.getTitle() == null || bibtex.getTitle().length() == 0) throw new IllegalArgumentException("no title specified in bibtex");
		}

		if (post.getTags() == null || post.getTags().size() == 0) throw new IllegalArgumentException("no tags specified");
		for (final Tag tag : post.getTags()) {
			if (tag.getName().length() == 0) throw new IllegalArgumentException("missing tagname");
		}

		this.username = username;
		this.post = post;
	}

	@Override
	protected String doExecute() throws ErrorPerformingRequestException {
		final StringWriter sw = new StringWriter(100);
		RendererFactory.getRenderer(getRenderingFormat()).serializePost(sw, this.post, null);
		this.downloadedDocument = performRequest(HttpMethod.POST, URL_USERS + "/" + this.username + "/" + URL_POSTS + "?format=" + getRenderingFormat().toString().toLowerCase(), sw.toString());
		return null;
	}
	
	@Override
	public String getResult() throws BadRequestOrResponseException, IllegalStateException {
		if (this.isSuccess()) {
			return RendererFactory.getRenderer(getRenderingFormat()).parseResourceHash(this.downloadedDocument);
		}
		return this.getError();
	}	
}