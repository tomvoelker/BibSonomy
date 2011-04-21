package org.bibsonomy.webapp.util;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.Vector;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.webapp.view.Views;

/**
 * @author rja
 * @version $Id$
 */
public class HeaderUtils {

	/**
	 * Mapping of MIME types to the supported export formats.
	 * Used for content negotiation using /uri/ 
	 * 
	 * Note: order is important: more specific MIME-types must come before less specific.
	 * I.e., "text/plain" before "plain" and "rdf+xml" before "xml".
	 *  
	 */
	private static final String[][] FORMAT_URLS = new String[][] {
		/* mime-type,   	bookmark, 						publication  */
		{"html", 			"", 		""},
		{"rdf+xml",			null,							Views.FORMAT_STRING_SWRC},
		{"text/plain",  	null, 							Views.FORMAT_STRING_BIB},
		{"plain", 			null, 							Views.FORMAT_STRING_BIB},
		{"rdf",				null,							Views.FORMAT_STRING_BURST},
		{"xml", 			Views.FORMAT_STRING_XML,		Views.FORMAT_STRING_LAYOUT + "/dblp"},
		{"csv", 			Views.FORMAT_STRING_CSV, 		Views.FORMAT_STRING_CSV	},
		{"json", 			Views.FORMAT_STRING_JSON, 		Views.FORMAT_STRING_JSON},
		{"rss", 			Views.FORMAT_STRING_RSS, 		Views.FORMAT_STRING_PUBLRSS},
		{"bibtex",			Views.FORMAT_STRING_BOOKBIB,	Views.FORMAT_STRING_BIB}
	};


	
	/**
	 * Gets the preferred response format which is supported in 
	 * dependence of the 'q-Value' (similar to a priority)
	 *
	 * @param acceptHeader 
	 * 			the HTML ACCEPT Header
	 * 			(example: 
	 * 				<code>ACCEPT: text/xml,text/html;q=0.9,text/plain;q=0.8,image/png</code>
	 * 				would be interpreted in the following precedence:
	 * 				1) text/xml
	 * 				2) image/png
	 * 				3) text/html
	 * 				4) text/plain)
	 * 			) 	
	 * @param contentType
	 * 			the contentType of the requested resource 
	 * 			<code>0</code> for bookmarks
	 * 			<code>1</code> for BibTeX
	 * @return 
	 * 			an index for access to the FORMAT_URLS array with the 
	 * 			url for redirect
	 */
	public static String getResponseFormat(final String acceptHeader, final int contentType) {		

		// if no acceptHeader is set, return default (= 0);
		if (!present(acceptHeader)) return FORMAT_URLS[0][contentType];

		final SortedMap<Double, Vector<String>> preferredTypes = org.bibsonomy.rest.utils.HeaderUtils.getPreferredTypes(acceptHeader);

		final List<String> formatOrder = new ArrayList<String>();			
		for (final Entry<Double, Vector<String>> entry: preferredTypes.entrySet()) {								
			for (final String type: entry.getValue()) {					
				formatOrder.add(type);					
			}
		}

		// check for supported formats
		for (final String type: formatOrder) {
			for (int j=0; j<FORMAT_URLS.length; j++) {					
				final String checkType = FORMAT_URLS[j][0];			
				if (type.indexOf(checkType) != -1) {						
					if (FORMAT_URLS[j][contentType] != null) {
						return FORMAT_URLS[j][contentType];
					}
				}
			}
		}		
		/*
		 * default: HTML
		 */
		return FORMAT_URLS[0][contentType];
		/*
		 * TODO: throw exception
		 */
//		throw new NotAcceptableException("", );
	}
	
	/**
	 * 
	 * http://hostname.com/mywebapp/servlet/MyServlet/a/b;c=123?d=789
	 * 
	 * @param req
	 * @return The URL that was used to produce the request
	 */
	public static String getUrl(final HttpServletRequest req) {
	    final StringBuffer reqUrl = req.getRequestURL();
	    final String queryString = req.getQueryString();   // d=789
	    if (present(queryString)) {
	        reqUrl.append("?").append(queryString);
	    }
	    return reqUrl.toString();
	}
	
	/**
	 * 
	 * /mywebapp/servlet/MyServlet/a/b;c=123?d=789
	 * 
	 * @param req
	 * @return The path and Query part of the URL that was used to produce the request
	 */
	public static String getPathAndQuery(final HttpServletRequest req) {
	    final StringBuffer reqUrl = new StringBuffer(req.getRequestURI());
	    final String queryString = req.getQueryString();   // d=789
	    if (present(queryString)) {
	        reqUrl.append("?").append(queryString);
	    }
	    return reqUrl.toString();
	}

}
