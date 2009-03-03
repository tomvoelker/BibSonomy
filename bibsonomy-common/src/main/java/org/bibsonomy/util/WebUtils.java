package org.bibsonomy.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;

/**
 * @author rja
 * @version $Id$
 */
public class WebUtils {

	private static final Logger log = Logger.getLogger(WebUtils.class);
	
	private static final String CHARSET = "charset=";
	
	/**
	 * Do a POST request to the given URL with the given content. Assume the charset of the result to be charset.
	 * 
	 * @param url
	 * @param postContent
	 * @param charset - the assumed charset of the result. If <code>null</code>, the charset from the response header is used.
	 * @return The content of the result page.
	 * 
	 * @throws IOException
	 */
	public static String getPostContentAsString(final URL url, final String postContent, final String charset) throws IOException {
		final HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		urlConn.setAllowUserInteraction(false);
		urlConn.setDoInput(true);
		urlConn.setDoOutput(true);
		urlConn.setUseCaches(false);
		urlConn.setRequestMethod("POST");
		urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		
		
		/*
		 * set user agent (see http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html) since some 
		 * pages require it to download content.
		 */
		urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)");

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
		
		// write into string writer
		final StringWriter out = inputStreamToStringWriter(urlConn.getInputStream(), activeCharset);
		
		// disconnect
		urlConn.disconnect();
		
		return out.toString();
	}
	
	/**
	 * Do a POST request to the given URL with the given content.
	 * 
	 * @param url
	 * @param postContent
	 * @return The content of the result page.
	 * 
	 * @throws IOException
	 */
	public static String getPostContentAsString(final URL url, final String postContent) throws IOException {
		return getPostContentAsString(url, postContent, null);
	}
	
	/**
	 * Reads from a URL and writes the content into a string.
	 * @param inputURL the url to scrape
	 * @return String which holds the page content.
	 * @throws IOException 
	 */
	public static String getContentAsString(final URL inputURL) throws IOException {
		try {
			final HttpURLConnection urlConn = (HttpURLConnection) inputURL.openConnection();
			urlConn.setAllowUserInteraction(false);
			urlConn.setDoInput(true);
			urlConn.setDoOutput(false);
			urlConn.setUseCaches(false);
			/*
			 * set user agent (see http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html) since some 
			 * pages require it to download content.
			 */
			urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)");
			urlConn.connect();
			
			/*
			 * extract character encoding from header
			 */
			final String charSet = getCharset(urlConn);
			
			/*
			 * write content into string buffer
			 */
			StringWriter out = inputStreamToStringWriter(urlConn.getInputStream(), charSet);

			urlConn.disconnect();
			
			out.flush();
			out.close();
			return out.toString();
		} catch (final ConnectException cex) {
			log.fatal("Could not get content for URL " + inputURL.toString() + " : " + cex.getMessage());
			throw new IOException(cex);
		} catch (final IOException ioe) {
			log.fatal("Could not get content for URL " + inputURL.toString() + " : " + ioe.getMessage());
			throw ioe;
		}
	}
	
	/**
	 * Parse html file from given URL into DOM tree.
	 * 
	 * @param inputURL file's url
	 * @return parsed DOM tree
	 * @throws IOException if html file could not be parsed. 
	 */
	public static Document parseHTMLFromURL(final URL inputURL) throws IOException {
			final Tidy tidy = new Tidy();
			tidy.setQuiet(true);
			tidy.setShowWarnings(false);

			final String encodingName = getCharset((HttpURLConnection)inputURL.openConnection());
			tidy.setInputEncoding(encodingName);
			return tidy.parseDOM(inputURL.openConnection().getInputStream(), null);
	}
	
	/**
	 * Parse html file from given string into DOM tree.
	 * 
	 * @param content - content of the web page
	 * @return parsed DOM tree
	 */
	public static Document parseHTMLFromString(final String content) {
			final Tidy tidy = new Tidy();
			tidy.setQuiet(true);
			tidy.setShowWarnings(false);

			// we don't know the encoding now ... so we assume utf8
			tidy.setInputEncoding("UTF-8");
			return tidy.parseDOM(new ByteArrayInputStream(content.getBytes()), null);
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
		if (contentType != null) {
			final int charsetPosition = contentType.indexOf(CHARSET);
			if (charsetPosition > -1) {
				/*
				 * cut this:
				 *                    |<--   -->|             
				 * text/html; charset=utf-8; qs=1
				 */
				String charSet = contentType.substring(charsetPosition + CHARSET.length());
				
				// get only charset
				final int charsetEnding = charSet.indexOf(";");
				if (charsetEnding > -1) {
					/*
					 * cut this:
					 * |<->|             
					 * utf-8; qs=1
					 */
					charSet = charSet.substring(0, charsetEnding);
				}
				return charSet.trim().toUpperCase();
			} 
		} 
		/*
		 * default charset
		 */
		return "UTF-8";
	}

	/** Copies the stream into the string writer.
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	private static StringWriter inputStreamToStringWriter(final InputStream inputStream, final String charset) throws IOException {
		final InputStreamReader in;
		if (charset == null || charset.trim().equals(""))
			in = new InputStreamReader(inputStream);
		else 
			in = new InputStreamReader(inputStream, charset);
		
		final StringWriter out = new StringWriter();
		int b;
		while ((b = in.read()) >= 0) {
			out.write(b);
		}
		return out;
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
