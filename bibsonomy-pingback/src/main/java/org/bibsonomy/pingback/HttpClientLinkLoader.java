package org.bibsonomy.pingback;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	private static final String HTTP_HEADER_XPINGBACK = "X-Pingback";
	private static final Pattern pingbackUrlPattern = Pattern.compile("<link rel=\"pingback\" href=\"([^\"]+)\" ?/?>", Pattern.CASE_INSENSITIVE);

	final HttpClient httpClient;

	public HttpClientLinkLoader() {
		final SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		this.httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(schemeRegistry));
	}



	@Override
	public Link loadLink(final String linkUrl) {
		try {
			final HttpGet httpGet = new HttpGet(linkUrl);
			final HttpResponse response = this.httpClient.execute(httpGet);
			/*
			 * probe for pingback header
			 */
			final Header header = response.getFirstHeader(HTTP_HEADER_XPINGBACK);
			if (present(header)) {
				return new Link(null, linkUrl, header.getValue(), true);
			}
			/*
			 * TODO: probe for trackback header
			 */
			/*
			 * probe content
			 */
			final HttpEntity entity = response.getEntity();
			if (present(entity)) {
			    final BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
			    try {
					final String headContent = readHeadSectionOfPage(reader);
			    	/*
			    	 * probe pingback content
			    	 */
					final String pingbackUrl = getPingbackUrlFromHtml(headContent);
					if (present(pingbackUrl)) {
						return new Link(null, linkUrl, pingbackUrl, true);
					}
					/*
					 * TODO: probe trackback content
					 */
			    } finally {
			        reader.close();
			    }
			}
		} catch (final Exception e) {
			// ignore
		}
		return new Link(null, linkUrl, null, false);
	}

	@Override
	public String loadPageContents(String linkUrl) {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean containsLink(String htmlText, String link) {
		throw new UnsupportedOperationException();
	}
	@Override
	public List<String> findLinkAddresses(String textileText) {
		throw new UnsupportedOperationException();
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
		return line.matches("<body[^>]+>|</head>");
	}

	protected String getPingbackUrlFromHtml(String html) {
		final Matcher matcher = pingbackUrlPattern.matcher(html);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

}
