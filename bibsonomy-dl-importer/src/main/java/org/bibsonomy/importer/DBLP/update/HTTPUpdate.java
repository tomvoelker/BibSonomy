package org.bibsonomy.importer.DBLP.update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.importer.DBLP.DBLPException;


public class HTTPUpdate {
	private static final Log log = LogFactory.getLog(HTTPUpdate.class);

	private static final Pattern SESSION_ID_PATTERN = Pattern.compile("JSESSIONID=([0-9A-Fa-f]{32});.*");
	private static final Pattern CKEY_PATTERN = Pattern.compile(".*ckey=([0-9A-Fa-f]{32}).*");

	protected String baseURL = null;
	protected String cKey = null;
	private String userCookie = null;
	private String sessionCookie = null;
	
	
	
	public HTTPUpdate (String baseURL, String user, String passHash) throws MalformedURLException, IOException {
		this.baseURL = baseURL;
		this.userCookie = "_currUser=" + user + "%20" + passHash + "; ";
		openSession();
	}
	

	/** Calls the specified URL and throws an exception if the return code signals an error.
	 *  
	 * @param url
	 * @throws IOException
	 * @throws DBLPException
	 */
	protected void callURL(final URL url) throws IOException, DBLPException {
		
		log.debug("calling " + url);
		
		final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		setCookies(conn);
		conn.setInstanceFollowRedirects(false); // do not follow redirects!

		conn.getContentLength();

		final int response = conn.getResponseCode();
		conn.disconnect();
		if (!(response > 0 && response < 400)) throw new DBLPException("got " + response + " when calling " + url);
	}
	
	protected void setCookies(HttpURLConnection conn) {
		conn.setRequestProperty("Cookie",  userCookie + sessionCookie);
	}
	
	/**
	 * @param user
	 * @param passHash
	 * @return
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	protected void openSession() throws MalformedURLException, IOException {
		/*
		 * configure connection
		 */
		final URL url = new URL("http://www.bibsonomy.org/myBibSonomy");
		HttpURLConnection urlConn = null;
		urlConn = (HttpURLConnection) url.openConnection();
		urlConn.setAllowUserInteraction(false);
		urlConn.setDoInput(true);
		urlConn.setRequestProperty("Cookie", userCookie);
		urlConn.setDoOutput(false);
		urlConn.setUseCaches(false);
		urlConn.setInstanceFollowRedirects(true);
		urlConn.connect();
		/*
		 * get session from header
		 */
		final Map<String,List<String>> headers = urlConn.getHeaderFields();
		String sessionID = null;
		for (final String key: headers.keySet()) {
			if ("Set-Cookie".equals(key)) {
				for (String cookie: headers.get(key)) {
					Matcher matcher = SESSION_ID_PATTERN.matcher(cookie);
					if (matcher.matches()) {
						sessionID = matcher.group(1); 
					}
				}
				
			}
		}
		sessionCookie = "JSESSIONID=" + sessionID + "; ";
		/*
		 * read result to extract ckey
		 */
		final BufferedReader buf = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));
		String line = null;
		
		while ((line = buf.readLine()) != null) {
			if (line.contains("ckey=")) {
				final Matcher matcher = CKEY_PATTERN.matcher(line);
				if (matcher.matches()) {
					cKey = matcher.group(1);
					break;
				}
			}
		}
		urlConn.disconnect();
		buf.close();
	}
}
