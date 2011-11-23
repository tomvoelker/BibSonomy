package org.bibsonomy.rest.strategy.sync;

import java.io.ByteArrayOutputStream;
import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author wla
 * @version $Id$
 */
public class GetSyncDataStrategy extends Strategy {
	
	private static final Log log = LogFactory.getLog(GetSyncDataStrategy.class);
	
	private final URI serviceURI;
	private final Class<? extends Resource> resourceType;

	/**
	 * @param context
	 * @param serviceURI
	 */
	public GetSyncDataStrategy(final Context context, final URI serviceURI) {
		super(context);
		this.serviceURI = serviceURI;
		this.resourceType = ResourceFactory.getResourceClass(context.getStringAttribute(RESTConfig.RESOURCE_TYPE_PARAM, ResourceFactory.RESOURCE_CLASS_NAME));
	}

	@Override
	public void perform(final ByteArrayOutputStream outStream) throws InternServerException, NoSuchResourceException, ResourceMovedException, ResourceNotFoundException {
		final LogicInterface logic = this.getLogic();
		final String userName = logic.getAuthenticatedUser().getName();
		
		final SynchronizationData lastSyncData = logic.getLastSyncData(userName, serviceURI, this.resourceType);
		if (log.isDebugEnabled()) {
			log.debug("got last sync data '" + lastSyncData + "' for user " + userName + " and sync service " + serviceURI);
		}
		this.getRenderer().serializeSynchronizationData(writer, lastSyncData);
	}

}
