/**
 * BibSonomy-Web-Common - Common things for web
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.util;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author rja
 */
public class WebUtils {
	private static final Log log = LogFactory.getLog(WebUtils.class);

	/** maximal number of redirects to follow in {@link #getRedirectUrl(URL)} */
	private static final int MAX_REDIRECT_COUNT = 10;

	/** the connection timeout */
	private static final int CONNECTION_TIMEOUT = 5 * 1000;

	/** the read timeout */
	private static final int READ_TIMEOUT = 5 * 1000;

	/** The user agent used for all requests with {@link HttpURLConnection}. */
	private static final String USER_AGENT_PROPERTY_VALUE = "BibSonomy/2.0.32 (Linux x86_64; en) Gecko/20120714 Iceweasel/3.5.16 (like Firefox/3.5.16)";

	private static final String CHARSET = "charset=";
	private static final String EQUAL_SIGN = "=";
	private static final String AMP_SIGN = "&";
	private static final String NEWLINE = "\n";
	private static final String SEMICOLON = ";";
	private static final String USER_AGENT_HEADER_NAME = "User-Agent";
	private static final String COOKIE_HEADER_NAME = "Cookie";
	private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";

	/**
	 * The maximum number of characters (~bytes) to read from a HTTP connection.
	 * We fixed this to 1 MB to avoid that radio streams or huge files
	 * mess up our heap. If this is not enough, please increase the size
	 * carefully.
	 */
	private static final int MAX_CONTENT_LENGTH = 1 * 1024 * 1024;

	private static final PoolingHttpClientConnectionManager CONNECTION_MANAGER = new PoolingHttpClientConnectionManager();
	private static final HttpClient CLIENT = getHttpClient();

	/**
	 * default config for http client
	 */
	private static final RequestConfig DEFAULT_REQUEST_CONFIG = RequestConfig.custom()
					.setConnectTimeout(CONNECTION_TIMEOUT)
					.setSocketTimeout(READ_TIMEOUT)
					.setConnectionRequestTimeout(READ_TIMEOUT)
					.setMaxRedirects(MAX_REDIRECT_COUNT)
					.setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY).build();

	/**
	 * @return the default request config used for instances of http client
	 */
	public static Builder getDefaultRequestConfig() {
		return RequestConfig.copy(DEFAULT_REQUEST_CONFIG);
	}

	/**
	 * This method returns an instance of the HttpClient and should only be used
	 * if the other methods that deliver direct results can not be used. Each
	 * call to this method should be documented with an explanation why it is
	 * necessary.
	 * @param defaultRequestConfig
	 *
	 * @return the configured {@link HttpClient}
	 */
	public static HttpClient getHttpClient(final RequestConfig defaultRequestConfig) {
		/*
		 * configure client
		 */
		final HttpClientBuilder builder = HttpClientBuilder.create();
		builder.setDefaultRequestConfig(defaultRequestConfig);
		builder.setConnectionManager(CONNECTION_MANAGER);
		builder.setUserAgent(USER_AGENT_PROPERTY_VALUE);
		builder.setRedirectStrategy(new LaxRedirectStrategy()); // to enable following redirects for POST requests
		builder.setDefaultCookieStore(new BasicCookieStore());
		// build client
		return builder.build();
	}

	/**
	 * This method returns an instance of the HttpClient and should only be used
	 * if the other methods that deliver direct results can not be used. Each
	 * call to this method should be documented with an explanation why it is
	 * necessary.
	 *
	 * @return the configured {@link HttpClient}
	 */
	public static HttpClient getHttpClient() {
		return getHttpClient(DEFAULT_REQUEST_CONFIG);
	}

	/**
	 * Do a POST request to the given URL with the given content. Assume the charset of the result to be charset.
	 *
	 * @param url
	 * @param postContent
	 * @param charset - the assumed charset of the result. If <code>null</code>, the charset from the response header is used.
	 * @param cookie - the Cookie to be attached to the request. If <code>null</code>, the Cookie header is not set.
	 * @return The content of the result page.
	 *
	 * @throws IOException
	 *
	 * @Deprecated
	 */
	public static String getPostContentAsString(final URL url, final String postContent, final String charset, final String cookie) throws IOException {
		final HttpURLConnection urlConn = createConnection(url);
		urlConn.setAllowUserInteraction(false);
		urlConn.setDoInput(true);
		urlConn.setDoOutput(true);
		urlConn.setRequestMethod("POST");
		urlConn.setRequestProperty(CONTENT_TYPE_HEADER_NAME, "application/x-www-form-urlencoded");

		if (cookie != null) {
			urlConn.setRequestProperty(COOKIE_HEADER_NAME, cookie);
		}

		writeStringToStream(postContent, urlConn.getOutputStream());

		// connect
		urlConn.connect();

		/*
		 * extract character encoding from header
		 */
		final String activeCharset;
		if (charset == null) {
			activeCharset = getCharset(urlConn);
		} else {
			activeCharset = charset;
		}

		/*
		 * FIXME: check content type header to ensure that we only read textual
		 * content (and not a PDF, radio stream or DVD image ...)
		 */
		// write into string writer
		final StringBuilder out = inputStreamToStringBuilder(urlConn.getInputStream(), activeCharset);

		// disconnect
		urlConn.disconnect();

		return out.toString();
	}

	/**
	 * Do a POST request to the given URL with the given content. Assume the charset of the result to be charset.
	 *
	 * @param url
	 * @param postContent
	 * @param charset - the assumed charset of the result. If <code>null</code>, the charset from the response header is used.
	 * @return The content of the result page.
	 *
	 * @throws IOException
	 *
	 * @Deprecated
	 */
	public static String getPostContentAsString(final URL url, final String postContent, final String charset) throws IOException {
		return getPostContentAsString(url, postContent, charset, null);
	}

	/**
	 * Do a POST request to the given URL with the given content and cookie. Assume the charset of the result to be charset.
	 *
	 * @param url
	 * @param postContent
	 * @param cookie
	 * @return The content of the result page.
	 *
	 * @throws IOException
	 *
	 * @Deprecated
	 */
	public static String getPostContentAsString(final String cookie, final URL url, final String postContent) throws IOException {
		return getPostContentAsString(url, postContent, null, cookie);
	}

	/**
	 * Do a POST request to the given URL with the given content.
	 *
	 * @param url
	 * @param postContent
	 * @return The content of the result page.
	 *
	 * @throws IOException
	 *
	 * @Deprecated
	 */
	public static String getPostContentAsString(final URL url, final String postContent) throws IOException {
		return getPostContentAsString(url, postContent, null, null);
	}

	/**
	 * Reads from a URL and writes the content into a string.
	 *
	 * @param inputURL the URL of the content.
	 * @param cookie a cookie which should be included in the header of the request send to the server
	 * @return String which holds the page content.
	 * @throws IOException
	 *
	 * @Deprecated
	 */
	public static String getContentAsString(final URL inputURL, final String cookie) throws IOException {
		try {
			final HttpURLConnection urlConn = createConnection(inputURL);
			urlConn.setAllowUserInteraction(false);
			urlConn.setDoInput(true);
			urlConn.setDoOutput(false);

			if (cookie != null) {
				urlConn.setRequestProperty(COOKIE_HEADER_NAME, cookie);
			}
			urlConn.connect();

			/*
			 * extract character encoding from header
			 */
			final String charSet = getCharset(urlConn);

			/*
			 * FIXME: check content type header to ensure that we only read textual
			 * content (and not a PDF, radio stream or DVD image ...)
			 */

			/*
			 * write content into string buffer
			 */
			final StringBuilder out = inputStreamToStringBuilder(urlConn.getInputStream(), charSet);

			urlConn.disconnect();

			return out.toString();
		} catch (final ConnectException cex) {
			log.debug("Could not get content for URL " + inputURL.toString() + " : " + cex.getMessage());
			throw new IOException(cex);
		} catch (final IOException ioe) {
			log.debug("Could not get content for URL " + inputURL.toString() + " : " + ioe.getMessage());
			throw ioe;
		}
	}

	/**
	 * Reads from a URL and writes the content into a string.
	 *
	 * @param inputURL the URL of the content.
	 * @return String which holds the page content.
	 * @throws IOException
	 *
	 * @Deprecated
	 */
	public static String getContentAsString(final URL inputURL) throws IOException {
		return getContentAsString(inputURL, null);
	}

	/**
	 * Reads from a URL and writes the content into a string.
	 *
	 * @param url
	 * @param cookie
	 * @param postData
	 * @param visitBefore
	 *
	 * @return String which holds the page content.
	 *
	 * @throws IOException
	 */
	public static String getContentAsString(final String url, final String cookie, final String postData, final String visitBefore) throws IOException {
		return getContentAsString(CLIENT, url, cookie, postData, visitBefore);
	}

	/**
	 * Reads from a URL and writes the content into a string.
	 *
	 * @param url
	 * @param cookie
	 * @param postData
	 * @param visitBefore
	 *
	 * @return String which holds the page content.
	 *
	 * @throws IOException
	 */
	public static String getContentAsString(final HttpClient client, final String url, final String cookie, final String postData, final String visitBefore) throws IOException {
		if (present(visitBefore)) {
			/*
			 * visit URL to get cookies if needed
			 */
			final HttpGet get = new HttpGet(visitBefore);
			try {
				client.execute(get);
			} finally {
				// required, see http://hc.apache.org/httpclient-3.x/threading.html
				get.releaseConnection();
			}
		}

		final HttpRequestBase method;
		if (present(postData)) {
			/*
			 * do a POST request
			 */
			method = new HttpPost(url);
			/*
			 * prepare parameters
			 */
			final List<NameValuePair> params = new ArrayList<>();

			for (final String s : postData.split(AMP_SIGN)) {
				final String[] p = s.split(EQUAL_SIGN);

				if (p.length != 2) {
					continue;
				}

				params.add(new BasicNameValuePair(p[0], p[1]));
			}

			((HttpPost)method).setEntity(new UrlEncodedFormEntity(params));
		} else {
			/*
			 * do a GET request
			 */
			method = new HttpGet(url);
		}

		/*
		 * set cookie
		 */
		if (present(cookie)) {
			method.addHeader(COOKIE_HEADER_NAME, cookie);
		}
		try {
			/*
			 * do request
			 */
			final HttpResponse response = client.execute(method);
			final int status = response.getStatusLine().getStatusCode();
			if (status != HttpStatus.SC_OK) {
				throw new IOException(url + " returns: " + status);
			}

			/*
			 * FIXME: check content type header to ensure that we only read textual
			 * content (and not a PDF, radio stream or DVD image ...)
			 */
			/*
			 * collect response
			 */
			final String charset = extractCharset(response.getFirstHeader(CONTENT_TYPE_HEADER_NAME).getValue());
			final StringBuilder content = inputStreamToStringBuilder(response.getEntity().getContent(), charset);

			final String string = content.toString();
			if (string.length() > 0) {
				return string;
			}
		} finally {
			method.releaseConnection();
		}

		return null;
	}

	/**
	 * Reads from a URL and writes the content into a string.
	 *
	 * @param url
	 * @return String which holds the page content.
	 * @throws IOException
	 */
	public static String getContentAsString(final String url) throws IOException {
		return getContentAsString(url, null, null, null);
	}

	/**
	 * Reads from a URL and writes the content into a string.
	 *
	 * @param url
	 * @param cookie
	 * @return String which holds the page content.
	 * @throws IOException
	 */
	public static String getContentAsString(final String url, final String cookie) throws IOException {
		return getContentAsString(url, cookie, null, null);
	}

	/**
	 * Convenience method for getting the page content by passing the {@link HttpClient} and the
	 * {@link HttpGet}. If the HTTP status code is other than 200 HTTP OK null will be returned.
	 *
	 * @param client The client to execute.
	 * @param method The method to be executed.
	 * @return The response body as String if and only if the HTTP status code is 200 HTTP OK.
	 * @throws IOException
	 * @throws HttpException
	 */
	public static String getContentAsString(HttpClient client, HttpGet method) throws HttpException, IOException {
		try {
			final HttpResponse response = client.execute(method);
			switch (response.getStatusLine().getStatusCode()) {
				case HttpStatus.SC_OK:
					final String charset = extractCharset(response.getFirstHeader(CONTENT_TYPE_HEADER_NAME).getValue());
					return inputStreamToStringBuilder(response.getEntity().getContent(), charset).toString();
				default:
					return null;
			}
		} finally {
			// required, see http://hc.apache.org/httpclient-3.x/threading.html
			method.releaseConnection();
		}
	}

	/**
	 * Convenience method for getting the page content by passing the {@link HttpGet} executed by the exisitng client.
	 * It calls {@link WebUtils#getContentAsString(HttpClient, HttpGet)}
	 * @param method
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public static String getContentAsString(HttpGet method) throws HttpException, IOException {
		return getContentAsString(CLIENT, method);
	}

	/**
	 * Convenience method for getting the page content by passing the {@link HttpClient} and the
	 * {@link URI}. It calls {@link WebUtils#getContentAsString(HttpClient, HttpGet)}.
	 *
	 * @param client The client to execute.
	 * @param uri The URI to be requested.
	 * @return The response body as String if and only if the HTTP status code is 200 HTTP OK.
	 * @throws HttpException
	 * @throws IOException
	 */
	public static String getContentAsString(HttpClient client, URI uri) throws HttpException, IOException {
		final HttpGet method = new HttpGet();
		method.setURI(uri);
		return getContentAsString(client, method);
	}

	/**
	 * Convenience method for getting the page content by passing the {@link HttpClient} and the
	 * URI as String. It calls {@link WebUtils#getContentAsString(HttpClient, HttpGet)}.
	 *
	 * @param client The client to execute.
	 * @param uri The URI to be requested as String.
	 * @return The response body as String if and only if the HTTP status code is 200 HTTP OK.
	 * @throws HttpException
	 * @throws IOException
	 */
	public static String getContentAsString(HttpClient client, String uri) throws HttpException, IOException {
		return getContentAsString(client, new HttpGet(uri));
	}

	/**
	 * Shortcut for {@link #getRedirectUrl(URL, List)}.
	 *
	 * @param url The location to start.
	 * @return - The redirect URL if received HTTP Status Code 200, null otherwise.
	 */
	public static URL getRedirectUrl(final URL url) {
		return getRedirectUrl(url, null);
	}

	/**
	 * Executes a request for the given URL following up to {@value #MAX_REDIRECT_COUNT}
	 * redirects. If response is HTTP Status Code 200 returns the URL for that location,
	 * otherwise return null. 
	 *
	 * @param url The location to start.
	 * @param headers Additional headers to be added to the request
	 * @return - The redirect URL if received HTTP Status Code 200, null otherwise.
	 */
	public static URL getRedirectUrl(final URL url, final List<Header> headers) {
		final HttpGet method = new HttpGet(url.toExternalForm());
		if (present(headers)) {
			for (final Header header : headers) {
				method.addHeader(header);
			}
		}
		final HttpClient client = getHttpClient();

		try {
			final HttpClientContext context = HttpClientContext.create();
			final HttpResponse response = client.execute(method, context);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

				// get final redirect URL, cf. https://stackoverflow.com/questions/1456987/
				final List<URI> locations = context.getRedirectLocations();
				if (locations != null) {
					return locations.get(locations.size() - 1).toURL();
				}
			}
		} catch (IOException e) {
			// ignore
		} finally {
			// required, see http://hc.apache.org/httpclient-3.x/threading.html
			method.releaseConnection();
		}

		return null;
	}

	/**
	 * Returns the cookies returned by the server on accessing the URL. 
	 * The format of the returned cookies is as
	 *
	 * @param url
	 * @return The cookies as string, build by {@link #buildCookieString(List)}.
	 * @throws IOException
	 */
	public static String getCookies(final URL url) throws IOException {
		final HttpURLConnection urlConn = createConnection(url);
		urlConn.setAllowUserInteraction(false);
		urlConn.setDoInput(true);
		urlConn.setDoOutput(false);

		urlConn.connect();

		final List<String> cookies = urlConn.getHeaderFields().get("Set-Cookie");
		urlConn.disconnect();

		return buildCookieString(cookies);
	}

	/**
	 * @param url the url
	 * @return the proper configured http connection for the url
	 * @throws IOException
	 */
	public static HttpURLConnection createConnection(URL url) throws IOException {
		final HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

		// set the timeouts
		urlConn.setReadTimeout(READ_TIMEOUT);
		urlConn.setConnectTimeout(CONNECTION_TIMEOUT);
		urlConn.setUseCaches(false);

		/*
		 * set user agent (see http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html) since some
		 * pages require it to download content.
		 */
		urlConn.setRequestProperty(USER_AGENT_HEADER_NAME, USER_AGENT_PROPERTY_VALUE);
		return urlConn;
	}

	/**
	 * Builds a cookie string as used in the HTTP header.
	 *
	 * @param cookies - a list of key/value pairs
	 * @return The cookies folded into a string.
	 */
	public static String buildCookieString(final List<String> cookies) {
		final StringBuffer result = new StringBuffer();

		if (cookies != null) {
			for (final String cookie : cookies) {
				if (result.length() != 0) {
					result.append(";");
				}
				result.append(cookie);
			}
		}
		return result.toString();
	}


	/** Extracts the charset ID of a web page as returned by the server.
	 *
	 * @param urlConn
	 * @return
	 */
	private static String getCharset(final HttpURLConnection urlConn) {
		return extractCharset(urlConn.getContentType());
	}

	/**
	 * Extracts the charset from the given string. The string should resemble
	 * the content type header of an HTTP request. Valid examples are:
	 * <ul>
	 * <li>text/html; charset=utf-8; qs=1</li>
	 * <li>
	 * </ul>
	 *
	 * @param contentType
	 * @return - The charset.
	 */
	public static String extractCharset(final String contentType) {
		/*
		 * this typically looks like that:
		 * text/html; charset=utf-8; qs=1
		 */
		if (present(contentType)) {
			final int charsetPosition = contentType.indexOf(CHARSET);
			if (charsetPosition > -1) {
				/*
				 * cut this:
				 *                    |<--   -->|
				 * text/html; charset=utf-8; qs=1
				 */
				String charSet = contentType.substring(charsetPosition + CHARSET.length());

				// get only charset
				final int charsetEnding = charSet.indexOf(SEMICOLON);
				if (charsetEnding > -1) {
					/*
					 * cut this:
					 * |<->|
					 * utf-8; qs=1
					 */
					charSet = charSet.substring(0, charsetEnding);
				}
				/*
				 * reomove the "" from the charSet if it is contained
				 */

				if (charSet.startsWith("\"")) {
					charSet = charSet.replaceAll("\"", "");
				}
				return charSet.trim().toUpperCase();
			}
		}
		/*
		 * default charset
		 */
		return StringUtils.CHARSET_UTF_8;
	}

	/**
	 * Copies the stream into the string builder.
	 *
	 * @param inputStream
	 * @param charset
	 * @return stringbuilder with the contents of the inputstream
	 * @throws IOException
	 */
	public static StringBuilder inputStreamToStringBuilder(final InputStream inputStream, final String charset) throws IOException {
		final InputStreamReader in;
		/*
		 * set charset
		 */
		if (!present(charset)) {
			in = new InputStreamReader(inputStream);
		} else {
			in = new InputStreamReader(inputStream, charset);
		}
		/*
		 * use buffered reader (we always assume to have text)
		 */
		final BufferedReader buf = new BufferedReader(in);
		final StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = buf.readLine()) != null && sb.length() + line.length() < MAX_CONTENT_LENGTH) {
			sb.append(line).append(NEWLINE);
		}
		buf.close();

		return sb;
	}

	/** Writes the given string to the stream.
	 *
	 * @param s
	 * @param outputStream
	 * @throws IOException
	 */
	private static void writeStringToStream(final String s, final OutputStream outputStream) throws IOException {
		final StringReader reader = new StringReader(s);
		int b;
		while ((b = reader.read()) >= 0) {
			outputStream.write(b);
		}
		outputStream.flush();
	}
}
