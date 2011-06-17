package org.bibsonomy.pingback;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import com.malethan.pingback.Link;
import com.malethan.pingback.LinkLoader;

/**
 * @author rja
 * @version $Id$
 */
public class HttpClientLinkLoader implements LinkLoader {

	private static final Log log = LogFactory.getLog(HttpClientLinkLoader.class);

	private static final String HTTP_HEADER_XPINGBACK = "X-Pingback";
	private static final Pattern PINGBACK_URL_PATTERN = Pattern.compile("<link rel=\"pingback\" href=\"([^\"]+)\" ?/?>", Pattern.CASE_INSENSITIVE);
	private static final Pattern TRACKBACK_RDF_PATTERN = Pattern.compile("(<rdf:RDF.*?</rdf:RDF>)", Pattern.DOTALL);
	private static final Pattern TRACKBACK_LINK_URL_PATTERN = Pattern.compile("dc:identifier=\"([^\"]+)\"");
	private static final Pattern TRACKBACK_PING_URL_PATTERN = Pattern.compile("trackback:ping=\"([^\"]+)\"");
	private static final Pattern END_OF_HTML_HEAD_PATTERN = Pattern.compile("<body[^>]+>|</head>", Pattern.CASE_INSENSITIVE);

	/**
	 * The maximal number of characters to read from the body of a HTML page.
	 * Approx. 500kb (The BibSonomy blog page is approx. 120kb big).
	 */
	private static final int MAX_HTML_BODY_CHARS = 500000;


	final HttpClient httpClient;

	public HttpClientLinkLoader() {
		/*
		 * HTTP client 4.1.1
		 */
		final SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		final ThreadSafeClientConnManager conman = new ThreadSafeClientConnManager(schemeRegistry);
		//		conman.setDefaultMaxPerRoute(10); // allow more than 10 connections to the same host
		this.httpClient = new DefaultHttpClient(conman);
		/*
		 * HTTP client 4.0.1
		 */
		/*
		final SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		final HttpParams httpParams = new BasicHttpParams(); // FIXME: not thread-safe :-(
		//        httpParams.setIntParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 25);
		//        httpParams.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, 5);
		final ThreadSafeClientConnManager conman = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
		//		conman.setDefaultMaxPerRoute(10); // allow more than 10 connections to the same host
		final HttpParams httpParams2 = new BasicHttpParams(); // FIXME: probably not thread-safe :-(
		this.httpClient = new DefaultHttpClient(conman, httpParams2);
		*/
	}


	@Override
	public Link loadLink(final String linkUrl) {
		log.debug("loading link " + linkUrl);
		try {
			final HttpGet httpGet = new HttpGet(linkUrl);
			try  {
				final HttpResponse response = this.httpClient.execute(httpGet);
				/*
				 * probe for pingback header
				 */
				final Header header = response.getFirstHeader(HTTP_HEADER_XPINGBACK);
				if (present(header)) {
					log.debug("found pingback header");
					return new Link(null, linkUrl, header.getValue(), true);
				}
				/*
				 * probe content
				 */
				final HttpEntity entity = response.getEntity();
				final Header contentType = entity.getContentType();
				if (present(entity) && present(contentType) && contentType.getValue().contains("html")) {
					final BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
					try {
						final String headContent = readHeadSectionOfPage(reader);
						/*
						 * probe pingback content
						 */
						final String pingbackUrl = getPingbackUrlFromHtml(headContent);
						if (present(pingbackUrl)) {
							log.debug("found pingback meta tag");
							return new Link(null, linkUrl, pingbackUrl, true);
						}
						/*
						 * probe trackback content
						 */
						final String trackbackUrl = getTrackbackUrl(linkUrl, headContent, reader);
						if (present(trackbackUrl)) {
							log.debug("found trackback URL");
							return new TrackbackLink(null, linkUrl, trackbackUrl, true);
						}
					} finally {
						reader.close();
					}
				}
			} finally {
				// ensure that the connection is released to the pool
				httpGet.abort();
			}
		} catch (final Exception e) {
			log.debug("got exception: ", e);
			// ignore
		}
		log.debug("no link found");
		return new Link(null, linkUrl, null, false);
	}


	/**
	 * Checks first the header for a trackback URL and if no could be found, 
	 * the body is checked.
	 * 
	 * @param linkUrl
	 * @param headContent
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	protected String getTrackbackUrl(final String linkUrl, final String headContent, final BufferedReader reader) throws IOException {
		/*
		 * first: check head section
		 */
		final String trackbackUrl = getTrackbackUrlFromHtml(linkUrl, headContent);
		if (present(trackbackUrl)) {
			return trackbackUrl;
		}
		/*
		 * check HTML body
		 */
		return getTrackbackUrlFromHtml(linkUrl, readRemainingPage(reader));
	}

	@Override
	public String loadPageContents(final String linkUrl) {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean containsLink(final String htmlText, final String link) {
		throw new UnsupportedOperationException();
	}
	@Override
	public List<String> findLinkAddresses(final String textileText) {
		throw new UnsupportedOperationException();
	}

	protected String readRemainingPage(final BufferedReader reader) throws IOException {
		final StringBuilder content = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null && content.length() < MAX_HTML_BODY_CHARS) {
			content.append(line);
		}
		return content.toString();
	}

	protected String readHeadSectionOfPage(final BufferedReader reader) throws IOException {
		final StringBuilder content = new StringBuilder();
		String line = reader.readLine();
		while (line != null && !reachedEndOfHeadSection(line)) {
			content.append(line);
			line = reader.readLine();
		}
		return content.toString();
	}

	protected boolean reachedEndOfHeadSection(final String line) {
		return END_OF_HTML_HEAD_PATTERN.matcher(line).matches();
	}

	protected String getPingbackUrlFromHtml(final String html) {
		final Matcher matcher = PINGBACK_URL_PATTERN.matcher(html);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	/**
	 * Tries to find the trackback ping URL for <code>linkUrl</code> in the 
	 * provided HTML string. 
	 * 
	 * @param linkUrl
	 * @param html
	 * @return The trackback ping URL for <code>linkUrl</code> if found, otherwise <code>null</code>.
	 */
	protected String getTrackbackUrlFromHtml(final String linkUrl, final String html) {
		/*
		 * find all RDF snippets in the page
		 */
		final Matcher matcher = TRACKBACK_RDF_PATTERN.matcher(html);
		while (matcher.find()) {
			/*
			 * check if the RDF block is the correct one for our linkUrl 
			 */
			final String match = matcher.group(1);
			final Matcher matcher2 = TRACKBACK_LINK_URL_PATTERN.matcher(match);
			if (matcher2.find()) {
				final String linkUrl2 = matcher2.group(1);
				/*
				 * check for linkUrl (also with local part # removed)
				 */
				if (linkUrl.equals(linkUrl2) || linkUrl.replaceAll("\\#.*$", "").equals(linkUrl2)) {
					/*
					 * correct RDF block for linKUrl found 
					 */
					final Matcher matcher3 = TRACKBACK_PING_URL_PATTERN.matcher(html);
					if (matcher3.find()) {
						return matcher3.group(1);
					}
				}
			}
		}
		/*
		 * nothing found
		 */
		return null;
	}

}
