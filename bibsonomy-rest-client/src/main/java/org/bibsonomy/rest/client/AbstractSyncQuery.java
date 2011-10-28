package org.bibsonomy.rest.client;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SynchronizationDirection;
import org.bibsonomy.util.UrlUtils;


/**
 * @author wla
 * @version $Id$
 */
public abstract class AbstractSyncQuery<T> extends AbstractQuery<T> {

	private final String serviceURI;
	private final ConflictResolutionStrategy strategy;
	private final SynchronizationDirection direction;
	private final Class<? extends Resource> resourceType;
	
	public AbstractSyncQuery(final String serviceURI, final Class<? extends Resource> resourceType, final ConflictResolutionStrategy strategy, final SynchronizationDirection direction) {
		this.serviceURI = serviceURI;
		this.resourceType = resourceType;
		this.strategy = strategy;
		this.direction = direction;
	}
	
	/**
	 * 
	 * @param action
	 * @return
	 */
	protected String generateURL(final String action) {
		String result = URL_SYNC + "/" + UrlUtils.safeURIEncode(serviceURI) + "/" + action; 
		if(!Resource.class.equals(resourceType) && present(resourceType)) {
			result = UrlUtils.setParam(result, "resourceType", ResourceFactory.getResourceName(resourceType));
		}
		if(present(strategy)) {
			result = UrlUtils.setParam(result, "strategy", strategy.getConflictResolutionStrategy());
		}
		if (present(direction)) {
			result = UrlUtils.setParam(result, "direction", direction.getSynchronizationDirection());
		}
		return result;
	}
}
