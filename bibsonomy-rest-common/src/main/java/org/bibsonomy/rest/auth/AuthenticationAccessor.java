/**
 * BibSonomy-Rest-Common - Common things for the REST-client and server.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
