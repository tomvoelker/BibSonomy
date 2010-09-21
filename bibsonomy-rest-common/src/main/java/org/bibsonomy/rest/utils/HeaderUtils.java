package org.bibsonomy.rest.utils;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Comparator;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

/**
 * @author dzo
 * @version $Id$
 */
public class HeaderUtils {
	private HeaderUtils() {}

	/**
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
	 * @return a sorted map with the precedences
	 */
	public static SortedMap<Double, Vector<String>> getPreferredTypes(final String acceptHeader) {
		// maps the q-value to output format (reverse order)
		final SortedMap<Double,Vector<String>> preferredTypes = new TreeMap<Double,Vector<String>>(new Comparator<Double>() {
			@Override
			public int compare(Double o1, Double o2) {
				if (o1.doubleValue() > o2.doubleValue())
					return -1;
				else if (o1.doubleValue() < o2.doubleValue())
					return 1;
				else
					return o1.hashCode() - o2.hashCode();
			}				
		});		
		
		if (!present(acceptHeader)) {
			return preferredTypes;
		}
	
		// fill map with q-values and formats
		final Scanner scanner = new Scanner(acceptHeader.toLowerCase());
		scanner.useDelimiter(",");
	
		while(scanner.hasNext()) {
			final String[] types = scanner.next().split(";");
			final String type = types[0];
			double qValue = 1;
	
			if (types.length != 1) 
				qValue = Double.parseDouble(types[1].split("=")[1]);
			
			Vector<String> v = preferredTypes.get(qValue);
			if (!preferredTypes.containsKey(qValue)) {
				v = new Vector<String>();			
				preferredTypes.put(qValue, v);			
			}
			v.add(type);
		}
		return preferredTypes;
	}
	
	
}
