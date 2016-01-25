/**
 * BibSonomy-Rest-Client - The REST-client.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.rest.client.queries.post;

import java.io.Reader;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SynchronizationDirection;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.rest.client.AbstractSyncQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.util.StringUtils;

/**
 * @author wla
 */
public class CreateSyncPlanQuery extends AbstractSyncQuery<List<SynchronizationPost>> {
	private static final Log log = LogFactory.getLog(CreateSyncPlanQuery.class);

	private final List<SynchronizationPost> posts;

	/**
	 * creates a new sync plan query
	 * 
	 * @param serviceURI the uri of the service
	 * @param posts the posts
	 * @param resourceType the resource to use
	 * @param strategy the sync strategy to use
	 * @param direction the sync direction to use
	 */
	public CreateSyncPlanQuery(final String serviceURI, final List<SynchronizationPost> posts, final Class<? extends Resource> resourceType, final ConflictResolutionStrategy strategy, final SynchronizationDirection direction) {
		super(serviceURI, resourceType, strategy, direction);
		this.posts = posts;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final StringWriter sw = new StringWriter();
		final Renderer renderer = this.getRenderer();
		renderer.serializeSynchronizationPosts(sw, posts);

		final String syncURL = this.getUrlRenderer().createHrefForSync(this.serviceURI, this.resourceType, this.strategy, this.direction, null, null);
		final Reader reader = performRequest(HttpMethod.POST, syncURL, StringUtils.toDefaultCharset(sw.toString()));
		this.downloadedDocument = reader;
	}

	@Override
	protected List<SynchronizationPost> getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		if (isSuccess()) {
			try {
				return this.getRenderer().parseSynchronizationPostList(this.downloadedDocument);
			} catch (final BadRequestOrResponseException ex) {
				log.error(ex.getMessage());
				throw ex;
			}
		}
		throw new BadRequestOrResponseException("HTTP STATUS: " + this.getHttpStatusCode());
	}
}
