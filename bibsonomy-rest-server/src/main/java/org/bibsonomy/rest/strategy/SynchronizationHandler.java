package org.bibsonomy.rest.strategy;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.StringTokenizer;

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
		
	@Override
	public Strategy createStrategy(final Context context, final StringTokenizer urlTokens, final HttpMethod httpMethod) {
		final int numTokensLeft = urlTokens.countTokens();
		if (numTokensLeft != 1) {
			throw new NoSuchResourceException("cannot process url (no strategy available) - please check url syntax ");
		}
		try {
			final URI serviceURI = new URI(urlTokens.nextToken());
			
			switch (httpMethod) {
			case GET:
				return new GetSyncDataStrategy(context, serviceURI);
			case DELETE:
				return new DeleteSyncDataStrategy(context, serviceURI);
			case PUT:
				return new PutSyncStatusStrategy(context, serviceURI);
			case POST:
				return new PostSyncPlanStrategy(context, serviceURI);
			default:
				throw new UnsupportedHttpMethodException(httpMethod, "SynchronizationStatus");
			}
		} catch (final URISyntaxException ex) {
			throw new NoSuchResourceException("cannot process url (no strategy available) - please check url syntax ");
		}
	}

}
