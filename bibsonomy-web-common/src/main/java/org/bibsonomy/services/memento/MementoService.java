package org.bibsonomy.services.memento;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.bibsonomy.util.WebUtils;

import org.apache.commons.httpclient.Header;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
	
	public MementoService(final URL timeGate) {
		this.timeGate = timeGate;
	}
	
	protected String getQueryUrl(final String url) {
		return this.timeGate.toString() + url;
	}
	
	/**
	 * @param url The URL for which we want to get the archived version.
	 * @param datetime A properly () formatted datetime.
	 * @return
	 * @throws MalformedURLException 
	 */
	public URL getMementoUrl(final String url, final String datetime) throws MalformedURLException {
		// encode the URL into the mement
		final URL queryUrl = new URL(getQueryUrl(url));
		// encode the datetime as "Accept-Datetime" header
		final List<Header> headers = Collections.singletonList(new Header("Accept-Datetime", datetime));
		// get redirect from timegate
		log.debug("querying timegate " + timeGate + " for " + url + " at " + datetime);
		final URL redirectUrl = WebUtils.getRedirectUrl(queryUrl, headers);
		log.debug("result: " + redirectUrl);
		return redirectUrl;
	}
}
