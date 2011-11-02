package org.bibsonomy.rest.client.queries.post;

import java.io.Reader;
import java.io.StringWriter;
import java.util.List;

import org.bibsonomy.model.Resource;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SynchronizationDirection;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.rest.client.AbstractSyncQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.util.StringUtils;

/**
 * @author wla
 * @version $Id$
 */
public class CreateSyncPlanQuery extends AbstractSyncQuery<List<SynchronizationPost>> {

	private final List<SynchronizationPost> posts;
	
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
		System.out.println("createSyncPlanQuery() calling URL " + syncURL);
		final Reader reader = performRequest(HttpMethod.POST, syncURL, StringUtils.toDefaultCharset(sw.toString()));
		return renderer.parseSynchronizationPostList(reader);
	}



}
