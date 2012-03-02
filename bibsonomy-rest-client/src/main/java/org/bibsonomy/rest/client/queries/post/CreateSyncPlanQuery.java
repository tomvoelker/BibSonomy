/**
 *
 *  BibSonomy-Rest-Client - The REST-client.
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
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

import java.io.Reader;
import java.io.StringWriter;
import java.util.List;

import org.bibsonomy.model.Resource;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SynchronizationDirection;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.rest.client.AbstractSyncQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.util.StringUtils;

/**
 * @author wla
 * @version $Id$
 */
public class CreateSyncPlanQuery extends AbstractSyncQuery<List<SynchronizationPost>> {

	private final List<SynchronizationPost> posts;
	
	/**
	 * creates a new sync plan query
	 * @param serviceURI the uri of the service
	 * @param posts the posts
	 * @param resourceType the resource to use
	 * @param strategy the sync strategy to use
	 * @param direction the syn direction to use
	 */
	public CreateSyncPlanQuery(final String serviceURI, final List<SynchronizationPost> posts, final Class<? extends Resource> resourceType, final ConflictResolutionStrategy strategy, final SynchronizationDirection direction) {
		super(serviceURI, resourceType, strategy, direction);
		this.posts = posts;
	}

	@Override
	protected List<SynchronizationPost> doExecute() throws ErrorPerformingRequestException {
		final StringWriter sw = new StringWriter();
		final Renderer renderer = this.getRenderer();
		renderer.serializeSynchronizationPosts(sw, posts);
		
		final String syncURL = getSyncURL();
		final Reader reader = performRequest(HttpMethod.POST, syncURL, StringUtils.toDefaultCharset(sw.toString()));
		return renderer.parseSynchronizationPostList(reader);
	}
}
