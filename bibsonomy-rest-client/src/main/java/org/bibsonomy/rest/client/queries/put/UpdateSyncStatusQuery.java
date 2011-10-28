package org.bibsonomy.rest.client.queries.put;

import org.bibsonomy.model.Resource;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SynchronizationDirection;
import org.bibsonomy.model.sync.SynchronizationStatus;
import org.bibsonomy.rest.client.AbstractSyncQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.util.UrlUtils;

/**
 * @author wla
 * @version $Id$
 */
public class UpdateSyncStatusQuery extends AbstractSyncQuery<Boolean> {

	private final SynchronizationStatus status;
	private final String info;
	
	public UpdateSyncStatusQuery(final String serviceURI, final Class<? extends Resource> resourceType, final ConflictResolutionStrategy strategy, final SynchronizationDirection direction, final SynchronizationStatus status, final String info) {
		super(serviceURI, resourceType, strategy, direction);
		this.status = status;
		this.info = info;
	}

	@Override
	protected Boolean doExecute() throws ErrorPerformingRequestException {
		String url = generateURL("status");
		url = UrlUtils.setParam(url, "status", status.toString());
		performRequest(HttpMethod.PUT, url, info);
		return true;
	}
}
