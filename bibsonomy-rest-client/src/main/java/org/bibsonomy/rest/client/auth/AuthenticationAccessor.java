/**
 * BibSonomy-Rest-Client - The REST-client.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
package org.bibsonomy.rest.client.auth;

import java.io.Reader;

import org.apache.http.client.methods.HttpRequestBase;
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
	<M extends HttpRequestBase> Reader perform(final String url, final String requestBody, final M method, final RenderingFormat renderingFormat) throws ErrorPerformingRequestException;
}
