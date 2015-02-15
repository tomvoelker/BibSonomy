/**
 * BibSonomy-Web-Common - Common things for web
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
package org.bibsonomy.services.memento;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.util.DateTimeUtils;
import org.bibsonomy.util.WebUtils;

/**
 * Interaction with Memento TimeGates, cf. http://www.mementoweb.org/ 
 * 
 * Given a URL and a timestamp, Memento tries to find a copy of that
 * URL in a web archive that is closest to the timestamp.
 * 
 * This service is taking a URL and a timestamp as input ({@link #getMementoUrl(String, String)}) and 
 * queries the configured timegate for an appropriate archived version.
 * 
 * @author rja
 *
 */
public class MementoService {
	private static final Log log = LogFactory.getLog(MementoService.class);
	
	private final URL timeGate;
	
	/**
	 * @param timeGate the timeGate of the MementoService
	 */
	public MementoService(final URL timeGate) {
		this.timeGate = timeGate;
	}
	
	/**
	 * @param url
	 * @return the query url to the memento service
	 */
	protected String getQueryUrl(final String url) {
		return this.timeGate.toString() + url;
	}
	
	/**
	 * @param url The URL for which we want to get the archived version.
	 * @param datetime A properly () formatted datetime.
	 * @return the redirect url to the mementoservice
	 * @throws MalformedURLException 
	 */
	public URL getMementoUrl(final String url, final Date datetime) throws MalformedURLException {
		// encode the URL into the mement
		final URL queryUrl = new URL(getQueryUrl(url));
		// encode the datetime as "Accept-Datetime" header
		final String formatDateRFC1123 = DateTimeUtils.formatDateRFC1123(datetime);
		final List<Header> headers = Collections.singletonList(new Header("Accept-Datetime", formatDateRFC1123));
		// get redirect from timegate
		log.debug("querying timegate " + timeGate + " for " + url + " at " + datetime + " (" + formatDateRFC1123 + ")");
		final URL redirectUrl = WebUtils.getRedirectUrl(queryUrl, headers);
		log.debug("result: " + redirectUrl);
		return redirectUrl;
	}
}
