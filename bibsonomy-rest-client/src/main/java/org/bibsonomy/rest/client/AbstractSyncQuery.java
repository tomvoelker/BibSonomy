package org.bibsonomy.rest.client;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SynchronizationDirection;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.util.UrlUtils;


/**
 * @author wla
 * @version $Id$
 * @param <T> 
 */
public abstract class AbstractSyncQuery<T> extends AbstractQuery<T> {

	private final String serviceURI;
	private final ConflictResolutionStrategy strategy;
	private final SynchronizationDirection direction;
	private final Class<? extends Resource> resourceType;
	
	/**
	 * 
	 * @param serviceURI
	 * @param resourceType
	 * @param strategy
	 * @param direction
	 */
	public AbstractSyncQuery(final String serviceURI, final Class<? extends Resource> resourceType, final ConflictResolutionStrategy strategy, final SynchronizationDirection direction) {
		this.serviceURI = serviceURI;
		this.resourceType = resourceType;
		this.strategy = strategy;
		this.direction = direction;
	}
	
	/**
	 * @return the sync url
	 */
	protected String getSyncURL() {
		final String result = URL_SYNC + "/" + UrlUtils.safeURIEncode(serviceURI);
		/*
		 * FIXME: resourceType=all not supported - where to block?
		 */
		if (present(resourceType)) {
			return UrlUtils.setParam(result, RESTConfig.RESOURCE_TYPE_PARAM, ResourceFactory.getResourceName(resourceType));
		}
		if (present(strategy)) {
			return UrlUtils.setParam(result, RESTConfig.SYNC_STRATEGY_PARAM, strategy.getConflictResolutionStrategy());
		}
		if (present(direction)) {
			return UrlUtils.setParam(result, RESTConfig.SYNC_DIRECTION_PARAM, direction.getSynchronizationDirection());
		}
		return result;
	}
}
