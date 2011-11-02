package org.bibsonomy.rest.client.queries.get;

import org.bibsonomy.model.Resource;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationDirection;
import org.bibsonomy.rest.client.AbstractSyncQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;

/**
 * @author wla
 * @version $Id$
 */
public class GetLastSyncDataQuery extends AbstractSyncQuery<SynchronizationData> {

	public GetLastSyncDataQuery(final String serviceURI, final Class<? extends Resource> resourceType, final ConflictResolutionStrategy strategy, final SynchronizationDirection direction) {
		super(serviceURI, resourceType, strategy, direction);
	}

	@Override
	protected SynchronizationData doExecute() throws ErrorPerformingRequestException {
		return this.getRenderer().parseSynchronizationData(performGetRequest(getSyncURL()));
	}

}
