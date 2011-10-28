package org.bibsonomy.rest.strategy.sync;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author wla
 * @version $Id$
 */
public class GetSyncDataStrategy extends Strategy {
	
	private final URI serviceURI;
	private final Class<? extends Resource> resourceType;

	/**
	 * @param context
	 * @param serviceURI
	 * @param resourceType 
	 */
	public GetSyncDataStrategy(final Context context, final URI serviceURI, final Class<? extends Resource> resourceType) {
		super(context);
		this.resourceType = resourceType;
		this.serviceURI = serviceURI;
	}

	@Override
	public void perform(final ByteArrayOutputStream outStream) throws InternServerException, NoSuchResourceException, ResourceMovedException, ResourceNotFoundException {
		final LogicInterface logic = this.getLogic();
		final String userName = logic.getAuthenticatedUser().getName();
		final Map<Class<? extends Resource>, SynchronizationData> syncDataMap = new LinkedHashMap<Class<? extends Resource>, SynchronizationData>();
		if (BibTex.class.equals(resourceType) || Resource.class.equals(resourceType)) {
			final SynchronizationData syncData = logic.getLastSyncData(userName, serviceURI, BibTex.class);
			if(present(syncData)) {
				syncDataMap.put(BibTex.class, syncData);
			}
		}
		if(Bookmark.class.equals(resourceType) || Resource.class.equals(resourceType)) {
			final SynchronizationData syncData = logic.getLastSyncData(userName, serviceURI, Bookmark.class);
			if(present(syncData)) {
				syncDataMap.put(Bookmark.class, syncData);
			}
		}
		this.getRenderer().serializeSynchronizationDataMap(writer, syncDataMap);
	}

}
