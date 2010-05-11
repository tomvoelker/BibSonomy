package tags;

import org.bibsonomy.util.UrlUtils;



/**
 * Some taglib functions - stolen from bibsonomy-webapp/tags/Functions.java
 * 
 * @author fei
 * @version $Id$
 */
public class Functions  {
	
	/**
	 * Wrapper for org.bibsonomy.util.UrlUtils.cleanUrl
	 * 
	 * @see org.bibsonomy.util.UrlUtils
	 * @param url
	 * @return the cleaned url
	 */
	public static String cleanUrl(String url) {
		return UrlUtils.cleanUrl(url);
	}
	
	/** Quotes a String such that it is usable for JSON.
	 * 
	 * @param value
	 * @return The quoted String.
	 */
	public static String quoteJSON(final String value) {
		if (value != null) {
			return value
			.replaceAll("\\\\", "\\\\\\\\") // back-slashes
			.replaceAll("\n", "\\\\n")   // linebreaks 
			.replaceAll("\t", "\\\\t")   // tabs
			.replaceAll("\r", "")        // windows linebreaks
			.replaceAll("\"", "\\\\\"")  // quotation marks
			;  
		}
		return value;
	}	

	/** First, replaces certain BibTex characters, 
	 * and then quotes JSON relevant characters. 
	 *  
	 * @param value
	 * @return The cleaned String.
	 */
	public static String quoteJSONcleanBibTeX(final String value) {
		return quoteJSON(BibcleanCSV.cleanBibtex(value));
	}

}
