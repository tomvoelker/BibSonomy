/**
 * BibSonomy Pingback - Pingback/Trackback for BibSonomy.
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

import com.malethan.pingback.Link;
import com.malethan.pingback.LinkLoader;

/**
 * @author rja
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

	private final HttpClient httpClient;

	/**
	 * default constructor
	 */
	public HttpClientLinkLoader() {
		this.httpClient = HttpClientHolder.getInstance().getHttpClient();
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
				if (present(entity)) { 
					final Header contentType = entity.getContentType();
					if (present(contentType) && contentType.getValue().contains("html")) {
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
	 * @return the trackback url
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
		return getTrackbackUrlFromHtml(linkUrl, readPortionOfPage(reader));
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

	/**
	 * reads only {@link #MAX_HTML_BODY_CHARS} from the reader
	 * @param reader
	 * @return the {@link #MAX_HTML_BODY_CHARS} from the reader
	 * @throws IOException
	 */
	protected String readPortionOfPage(final BufferedReader reader) throws IOException {
		final StringBuilder content = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null && content.length() < MAX_HTML_BODY_CHARS) {
			content.append(line);
		}
		return content.toString();
	}

	/**
	 *  TODO: move to WebUtils!
	 * reads only the head section of the page
	 * @param reader
	 * @return content from start till end of head
	 * @throws IOException
	 */
	protected String readHeadSectionOfPage(final BufferedReader reader) throws IOException {
		final StringBuilder content = new StringBuilder();
		String line = reader.readLine();
		while (line != null && !reachedEndOfHeadSection(line)) {
			content.append(line);
			line = reader.readLine();
		}
		return content.toString();
	}
	
	/**
	 * TODO: move to WebUtils!
	 * checks if line contains head end element 
	 * @param line
	 * @return <code>true</code> iff line contains head end element
	 */
	protected boolean reachedEndOfHeadSection(final String line) {
		return END_OF_HTML_HEAD_PATTERN.matcher(line).matches();
	}

	/**
	 * extracts the pingback url from html
	 * @param html
	 * @return the pingback url; if not found null
	 */
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
