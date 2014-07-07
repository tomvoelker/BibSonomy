package org.bibsonomy.memento;

import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.bibsonomy.android.loggingservice.AndroidLoggingServiceServlet;
import org.bibsonomy.util.WebUtils;

import org.apache.commons.httpclient.Header;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Interaction with Memento TimeGates, cf. http://www.mementoweb.org/
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
	 */
	public URL getMementoUrl(final String url, final String datetime) {
		// encode the URL into the mement
		final String queryUrl = getQueryUrl(url);
		// encode the datetime as "Accept-Datetime" header
		final List<Header> headers = Collections.singletonList(new Header("Accept-Datetime", datetime));
		// get redirect from timegate
		log.debug("querying timegate " + timeGate + " for " + url + " at " + datetime);
		final URL redirectUrl = WebUtils.getRedirectUrl(queryUrl, headers);
		log.debug("result: " + redirectUrl);
		return redirectUrl;
	}
}
