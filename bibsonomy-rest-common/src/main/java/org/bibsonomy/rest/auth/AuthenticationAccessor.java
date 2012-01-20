package org.bibsonomy.rest.auth;

import java.io.Reader;
import org.apache.commons.httpclient.HttpMethod;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.rest.renderer.RenderingFormat;

/**
 * encapsulates properties and methods needed to implement additional
 * remote authentication protocols, e.g., OAuth
 * 
 * @author fei
 * @version $Id$
 */
public interface AuthenticationAccessor {
	
	/**
	 * perform authenticated api call 
	 * 
	 * @param url
	 * @param requestBody
	 * @param method
	 * @param renderingFormat
	 * @return
	 * @throws ErrorPerformingRequestException
	 */
	public abstract <M extends HttpMethod> Reader perform(final String url, final String requestBody, final M method, final RenderingFormat renderingFormat) throws ErrorPerformingRequestException;
}
