package org.bibsonomy.rest.strategy.sync;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationStatus;
import org.bibsonomy.rest.strategy.AbstractUpdateStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author wla
 * @version $Id$
 */
public class PutSyncStatusStrategy extends AbstractUpdateStrategy {

	private final URI serviceURI;
	
	private static final Log log = LogFactory.getLog(PutSyncStatusStrategy.class);
	/**
	 * 
	 * @param context
	 * @param serviceURI
	 */
	public PutSyncStatusStrategy(final Context context, final URI serviceURI) {
		super(context);
		this.serviceURI = serviceURI;
	}

	@Override
	protected void render(final Writer writer, final String resourceID) {
		// TODO Auto-generated method stub
		return;
	}

	@Override
	protected String update() {
		final LogicInterface logic = this.getLogic();
		final String statusStr = context.getStringAttribute("status", "");
		if(!present(statusStr)) {
			log.error("no status received");
			throw new IllegalArgumentException();
		}
		final SynchronizationStatus status = SynchronizationStatus.valueOf(statusStr);
		
		String info = null;
		final BufferedReader reader = new BufferedReader(doc);
		try {
			info = reader.readLine();
		} catch (final IOException ex) {
			log.error("can't parse sync data info");
			ex.printStackTrace();
		}
		
		final Class<? extends Resource> resourceType = ResourceFactory.getResourceClass(context.getStringAttribute("resourceType", null));
		
		final String userName = logic.getAuthenticatedUser().getName();
		final SynchronizationData lastSyncData = logic.getLastSyncData(userName, serviceURI, resourceType);
		
		logic.updateSyncData(userName, serviceURI, resourceType, lastSyncData.getLastSyncDate(), status, info);
		return null;
	}


}
