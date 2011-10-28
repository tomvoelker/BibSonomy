package org.bibsonomy.rest.strategy;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.exceptions.UnsupportedHttpMethodException;
import org.bibsonomy.rest.strategy.sync.DeleteSyncDataStrategy;
import org.bibsonomy.rest.strategy.sync.GetSyncDataStrategy;
import org.bibsonomy.rest.strategy.sync.PostSyncPlanStrategy;
import org.bibsonomy.rest.strategy.sync.PutSyncStatusStrategy;

/**
 * @author wla
 * @version $Id$
 */
public class SynchronizationHandler implements ContextHandler {

	private static final Log log = LogFactory.getLog(SynchronizationHandler.class);
	
	private enum SyncMethod {
		PLAN,
		DATA,
		STATUS;
	}
	
	@Override
	public Strategy createStrategy(final Context context, final StringTokenizer urlTokens, final HttpMethod httpMethod) {
		int numTokensLeft = urlTokens.countTokens();
		if(numTokensLeft != 2) {
			log.error("wrong url tokens count");
			while (numTokensLeft != 0) {
				System.err.println(urlTokens.nextToken());
				numTokensLeft = urlTokens.countTokens();
			}
			throw new NoSuchResourceException("cannot process url (no strategy available) - please check url syntax ");
		}
		URI serviceURI = null;
		try {
			serviceURI = new URI(urlTokens.nextToken());
		} catch (final URISyntaxException ex) {
			
			ex.printStackTrace();
			throw new NoSuchResourceException("cannot process url (no strategy available) - please check url syntax ");
		}
		final SyncMethod actionToken = Enum.valueOf(SyncMethod.class, urlTokens.nextToken().toUpperCase());
		switch (actionToken) {
		case PLAN:
			return new PostSyncPlanStrategy(context, serviceURI);
		case STATUS:
			return new PutSyncStatusStrategy(context, serviceURI);
		case DATA:
			return createSyncDataStrategy(context, httpMethod, serviceURI);
		default:
			break;
		}
		
		throw new NoSuchResourceException("cannot process url (no strategy available) - please check url syntax ");
	}

	private Strategy createSyncDataStrategy(final Context context, final HttpMethod httpMethod, final URI serviceURI) {
		switch (httpMethod) {
		case GET:
			return new GetSyncDataStrategy(context, serviceURI, ResourceFactory.getResourceClass(context.getStringAttribute("resourceType", "all")));
		case DELETE:
			return new DeleteSyncDataStrategy(context, serviceURI);
		default:
			throw new UnsupportedHttpMethodException(httpMethod, "SynchronizationStatus");
		}
	}

}
