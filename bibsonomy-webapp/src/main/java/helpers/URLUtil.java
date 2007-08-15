package helpers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class URLUtil {
	private Hashtable<String,String> query = null;
	private String pathName = ""; 

	/**
	 * NOTE: the initial code for this method was obtained from 
	 * http://archive.apache.org/dist/geronimo/v1.0-M3/geronimo-1.0-M3-src.tar.gz/geronimo-1.0-M3/specs/servlet/src/java/javax/servlet/http/
	 * and modified to fit our purposes. Modifications are:
	 * 
	 * 
	 * Parses a query string passed from the client to the server and builds a
	 * <code>HashTable</code> object with key-value pairs. The query string
	 * should be in the form of a string packaged by the GET or POST method,
	 * that is, it should have key-value pairs in the form <i>key=value</i>,
	 * with each pair separated from the next by a &amp; character.
	 *
	 * <p>A key can appear more than once in the query string with different
	 * values. However, the key appears only once in the hashtable, with its
	 * value being an array of strings containing the multiple values sent
	 * by the query string.
	 *
	 * <p>The keys and values in the hashtable are stored in their decoded
	 * form, so any + characters are converted to spaces, and characters
	 * sent in hexadecimal notation (like <i>%xx</i>) are converted to ASCII
	 * characters.
	 *
	 * @param s a string containing the query to be parsed
	 *
	 * @return a <code>HashTable</code> object built from the parsed key-value
	 * pairs
	 * @throws UnsupportedEncodingException 
	 *
	 * @exception IllegalArgumentException if the query string is invalid
	 */
	private Hashtable<String,String> parseQueryString(String s) throws UnsupportedEncodingException {
		Hashtable<String,String> ht = new Hashtable<String,String>();
		// only lookg at part after '?'
		StringTokenizer st = new StringTokenizer(s, "&");
		while (st.hasMoreTokens()) {
			String pair = (String) st.nextToken();
			int pos = pair.indexOf('=');
			if (pos == -1) {
				// broken parameter (without value) found --> return 
				return ht;
			}
			String key = URLDecoder.decode(pair.substring(0, pos), "UTF-8");
			String val = URLDecoder.decode(pair.substring(pos + 1, pair.length()), "UTF-8");
			ht.put(key, val);
		}
		return ht;
	}

	public Hashtable<String, String> getQuery() {
		return query;
	}
	public String getPathName() {
		return pathName;
	}

	public void setQueryString(String queryString) {
		if (queryString == null) {
			throw new IllegalArgumentException();
		}
		try {
			int parametersStartAt = queryString.indexOf("?");
			if (parametersStartAt > 0) {
				this.pathName = queryString.substring(0, parametersStartAt); 
			}
			this.query = parseQueryString(queryString.substring(parametersStartAt + 1));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
