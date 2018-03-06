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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
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

	/**
	 * according to http://hc.apache.org/httpclient-3.x/threading.html
	 * HttpClient is thread safe and we can use one instance for several requests.
	 */
	private static final MultiThreadedHttpConnectionManager CONNECTION_MANAGER = new MultiThreadedHttpConnectionManager();
	static {
		final HttpConnectionManagerParams params = new HttpConnectionManagerParams();
		params.setConnectionTimeout(CONNECTION_TIMEOUT);
		params.setSoTimeout(READ_TIMEOUT);
		CONNECTION_MANAGER.setParams(params);
	}
	private static final HttpClient CLIENT = getHttpClient();

	
	/**
	 * This method returns an instance of the HttpClient and should only be used
	 * if the other methods that deliver direct results can not be used. Each 
	 * call to this method should be documented with an explanation why it is 
	 * necessary.
	 * 
	 * @return the configured {@link HttpClient}
	 */
	public static HttpClient getHttpClient() {
		final HttpClient client = new HttpClient(CONNECTION_MANAGER);
		final HttpClientParams params = client.getParams();
		/*
		 * configure client
		 */
		params.setParameter(HttpMethodParams.USER_AGENT, USER_AGENT_PROPERTY_VALUE);
		params.setParameter(HttpClientParams.SO_TIMEOUT, Integer.valueOf(READ_TIMEOUT));
		params.setConnectionManagerTimeout(READ_TIMEOUT);
		params.setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		params.setBooleanParameter(HttpMethodParams.SINGLE_COOKIE_HEADER, true);
		params.setIntParameter(HttpClientParams.MAX_REDIRECTS, MAX_REDIRECT_COUNT);
		return client;
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
		final HttpURLConnection urlConn = createConnnection(url);
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
	 * Convenience method for receiving page content for the given {@link PostMethod}. It calls
	 * {@link WebUtils#getContentAsString(HttpClient, HttpMethod)} and returns on status code 200 HTTP OK.
	 * On status code 303 See Other it calls {@link WebUtils#getContentAsString(HttpClient, String)} on the
	 * received "Location"-parameter.
	 * 
	 * @param client The client to execute.
	 * @param method The {@link PostMethod} to be executed.
	 * @return The content of the result page.
	 * @throws HttpException
	 * @throws IOException
	 */
	public static String getPostContentAsString(HttpClient client, PostMethod method) throws HttpException, IOException {
		final String postContent = getContentAsString(client, method);
		//if the postContent successfully received, return
		if (present(postContent)) return postContent;
		//check if status is 303 See Other
		if (method.getStatusCode() == HttpStatus.SC_SEE_OTHER) {
			Header location = method.getResponseHeader("Location");
			if (present(location) && present(location.getValue())) {
				HttpURL uri = new HttpURL(new HttpURL(method.getURI().getURI()), location.getValue());
				return getContentAsString(client, uri);
			}
		}
		return null;
	}
	/**
	 * Reads from a URL and writes the content into a string.
	 * 
	 * @param inputURL the URL of the content.
	 * @param cookie a cookie which should be included in the header of the request send to the server
	 * @return String which holds the page content.
	 * @throws IOException 
	 * 
	 * 
	 */
	public static String getContentAsString(final URL inputURL, final String cookie) throws IOException {
		return getContentAsString(inputURL.toString(), cookie, null, null);
	}
	
	@Deprecated
	public static String getContentAsStringOld(final URL inputURL, final String cookie) throws IOException {
		
		try {
			final HttpURLConnection urlConn = createConnnection(inputURL);
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
		if (present(visitBefore)) {
			/*
			 * visit URL to get cookies if needed
			 */
			CLIENT.executeMethod(new GetMethod(visitBefore));
		}
		
		final HttpMethod method;
		if (present(postData)) {
			/*
			 * do a POST request
			 */
			final List<NameValuePair> data = new ArrayList<NameValuePair>();
			
			for (final String s : postData.split(AMP_SIGN)) {
				final String[] p = s.split(EQUAL_SIGN);
				
				if (p.length != 2) {
					continue;
				}
				
				data.add(new NameValuePair(p[0], p[1]));
			}
			
			method = new PostMethod(url);
			((PostMethod)method).setRequestBody(data.toArray(new NameValuePair[data.size()]));
		} else {
			/*
			 * do a GET request
			 */
			method = new GetMethod(url);
			method.setFollowRedirects(true);
		}
		
		/*
		 * set cookie
		 */
		if (present(cookie)) {
			method.addRequestHeader(COOKIE_HEADER_NAME, cookie);
		}
		
		/*
		 * do request
		 */
		final int status = CLIENT.executeMethod(method);
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
		final String charset = extractCharset(method.getResponseHeader(CONTENT_TYPE_HEADER_NAME).getValue()); 
		final StringBuilder content = inputStreamToStringBuilder(method.getResponseBodyAsStream(), charset);
		method.releaseConnection();
		
		final String string = content.toString();
		if (string.length() > 0) {
			return string;
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
	 * {@link HttpMethod}. If the HTTP status code is other than 200 HTTP OK null will be returned.
	 * 
	 * @param client The client to execute.
	 * @param method The method to be executed.
	 * @return The response body as String if and only if the HTTP status code is 200 HTTP OK.
	 * @throws IOException 
	 * @throws HttpException 
	 */
	public static String getContentAsString(HttpClient client, HttpMethod method) throws HttpException, IOException {
		try {
			switch (client.executeMethod(method)) {
			case HttpStatus.SC_OK:
				final String charset = extractCharset(method.getResponseHeader(CONTENT_TYPE_HEADER_NAME).getValue());
				return inputStreamToStringBuilder(method.getResponseBodyAsStream(), charset).toString();
			default:
				return null;
			}
		} finally {
			method.releaseConnection();
		}
	}
	/**
	 * Convenience method for getting the page content by passing the {@link HttpMethod} executed by the exisitng client.
	 * It calls {@link WebUtils#getContentAsString(HttpClient, HttpMethod)}
	 * @param method
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public static String getContentAsString(HttpMethod method) throws HttpException, IOException {
		return getContentAsString(CLIENT, method);
	}
	/**
	 * Convenience method for getting the page content by passing the {@link HttpClient} and the
	 * {@link URI}. It calls {@link WebUtils#getContentAsString(HttpClient, HttpMethod)}.
	 * 
	 * @param client The client to execute.
	 * @param uri The URI to be requested.
	 * @return The response body as String if and only if the HTTP status code is 200 HTTP OK.
	 * @throws HttpException
	 * @throws IOException
	 */
	public static String getContentAsString(HttpClient client, URI uri) throws HttpException, IOException {
		final HttpMethod method = new GetMethod();
		method.setURI(uri);
		return getContentAsString(client, method);
	}
	/**
	 * Convenience method for getting the page content by passing the {@link HttpClient} and the
	 * URI as String. It calls {@link WebUtils#getContentAsString(HttpClient, HttpMethod)}.
	 * 
	 * @param client The client to execute.
	 * @param uri The URI to be requested as String.
	 * @return The response body as String if and only if the HTTP status code is 200 HTTP OK.
	 * @throws HttpException
	 * @throws IOException
	 */
	public static String getContentAsString(HttpClient client, String uri) throws HttpException, IOException {
		return getContentAsString(client, new GetMethod(uri));
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
		final HttpMethod method = new GetMethod(url.toExternalForm());
		if (present(headers)) {
			for (final Header header : headers) {
				method.addRequestHeader(header);
			}
		}
		final HttpClient client = getHttpClient();
		
		try {
			client.executeMethod(method);
		} catch (IOException e) {
			// ignore
		} finally {
			method.releaseConnection();
		}
		
		if (method.getStatusCode() != HttpStatus.SC_OK) return null;
		
		try {
			return new URL(method.getURI().getURI());
		} catch (URIException | MalformedURLException e) {
			// ignore, just return null
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
		final HttpURLConnection urlConn = createConnnection(url);
		urlConn.setAllowUserInteraction(false);
		urlConn.setDoInput(true);
		urlConn.setDoOutput(false);

		urlConn.connect();

		final List<String> cookies = urlConn.getHeaderFields().get("Set-Cookie");
		urlConn.disconnect();
		
		return buildCookieString(cookies);
	}
	
	/**
	 * 
	 * @param url
	 * @return the cookies
	 * @throws IOException
	 */
	public static String getLongCookies(final URL url) throws IOException {
		final List<String> cookies = new ArrayList<String>();
		final GetMethod getMethod = new GetMethod(url.toString());
		CLIENT.executeMethod(getMethod);
		final Header[] responseHeaders = getMethod.getResponseHeaders("Set-Cookie");
		for (int i = 0; i < responseHeaders.length; i++) {
			cookies.add(responseHeaders[i].getValue().toString());
		}
		return buildCookieString(cookies);
	}
	/**
	 * @param url the url
	 * @return the proper configured http connection for the url
	 * @throws IOException
	 */
	public static HttpURLConnection createConnnection(URL url) throws IOException {
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
				if (result.length() != 0)
					result.append(";");
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
