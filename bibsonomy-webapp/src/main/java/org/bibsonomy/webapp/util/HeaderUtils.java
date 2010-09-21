package org.bibsonomy.webapp.util;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.Vector;

/**
 * @author rja
 * @version $Id$
 */
public class HeaderUtils {

	/**
	 * Mapping of mime types to the supported export formats.
	 * Used for content negotiation using /uri/ 
	 * 
	 * Note: order is important: more specific mime-types must come before less specific.
	 * I.e., "text/plain" before "plain" and "rdf+xml" before "xml".
	 *  
	 */
	private static final String[][] FORMAT_URLS = new String[][] {
		/* mime-type,   	bookmark, 	publication  */
		{"html", 			"", 		""			},
		{"rss", 			"rss", 		"publrss"	},
		{"rdf+xml",			null,		"swrc"		},
		{"text/plain",  	null, 		"bib"		},
		{"plain", 			null, 		"bib"		},
		{"xml", 			"xml",		"layout/dblp"},
		{"rdf",				null,		"burst"		},
		{"bibtex",			"bookbib",	"bib"		}
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

}
