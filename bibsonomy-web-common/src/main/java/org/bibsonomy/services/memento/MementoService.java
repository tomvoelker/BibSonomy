package org.bibsonomy.services.memento;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.util.WebUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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
	
	/** used to get RFC 1123 formatted date */
	protected static final DateTimeFormatter RFC1123_DATE_TIME_FORMATTER = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'").withZoneUTC();
	
	/**
	 * Formats the date to RFC 1123, e.g.,  Wed, 30 May 2007 18:47:52 GMT
	 * 
	 * Currently Java's formatter doesn't support this standard therefore we can
	 * not use the fmt:formatDate tag with a pattern
	 * 
	 * @param date
	 * @return the formatted date
	 */
	public static String formatDateRFC1123(final Date date) {
		if (present(date)) {
			try {
				return RFC1123_DATE_TIME_FORMATTER.print(new DateTime(date));
			} catch (final Exception e) {
				log.error("error while formating date to RFC 1123", e);
				return "";
			}
		}
		return "";
	}
	
	
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
		final List<Header> headers = Collections.singletonList(new Header("Accept-Datetime", formatDateRFC1123(datetime)));
		// get redirect from timegate
		log.debug("querying timegate " + timeGate + " for " + url + " at " + datetime);
		final URL redirectUrl = WebUtils.getRedirectUrl(queryUrl, headers);
		log.debug("result: " + redirectUrl);
		return redirectUrl;
	}
}
