
package resources;

import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

@Deprecated
public abstract class SimHash {
	
	/* Input: a String of persons, divided by " and "
	 * Output: a Set of normalized persons, divided by ", " and enclosed in brackets "[ ]"
	 * 
	 */
	protected static Set<String> normalizePersonList (String s) {
		Scanner t = new Scanner (s).useDelimiter(" and ");
		SortedSet<String> persons = new TreeSet<String>(); 
		while (t.hasNext()) {
			persons.add(normalizePerson(t.next()));
		}
		return persons;
	}
	
	/* Input: a String of a person name 
	 * Output: normalized String of person name
	 * Example: 
	 * Donald E. Knuth --> d.knuth
	 * D.E.      Knuth --> d.knuth
	 * Donald    Knuth --> d.knuth
	 *           Knuth --> knuth
	 */
	protected static String normalizePerson (String s) {
		StringTokenizer t = new StringTokenizer(s);
		String          first = null;
		String          last  = null;
		// get first and last name
		while (t.hasMoreTokens()) {
			if (first == null) {
				first = t.nextToken().trim();
			} else {
				last = t.nextToken().trim();
			}
		}
		// only last name (=first) given
		if (first != null && last == null) {
			return first;
		}
		if (first != null && last != null) {
			return (first.substring(0,1) + "." + last);
		}
		return first;
	}
	
	protected static String getStringFromList (Collection<String> c) {
		if (c.isEmpty()) {
			return "[]";
		} else {
			// returns [item1,item2,item3,...]
			StringBuffer s = new StringBuffer("[");
			Iterator it = c.iterator();
			while (it.hasNext()) {
				s.append(it.next() + ",");
			}
			s.replace(s.length()-1,s.length(),"]");
			return s.toString();
		}
	}
	
	/*
	 * static methods for doing some String manipulation
	 */
	
	// removes everything which is not a Number
	protected static String removeNonNumbers (String s) {
		if (s != null) {
			return s.replaceAll("[^0-9]+","");
		}
		return "";
	}
	
	// removes everything which is neither a Number nor a Letter 
	protected static String removeNonNumbersOrLetters (String s) {
		if (s != null) {
			return s.replaceAll("[^0-9\\p{L}]+", "");
		}
		return "";
	}
	
	// removes everything which is neither a Number nor a Letter nor a Dot (.) nor Space
	protected static String removeNonNumbersOrLettersOrDotsOrSpace (String s) {
		if (s != null) {
			return normalizeWhitespace(s).replaceAll("[^0-9\\p{L}\\. ]+", "");
		}
		return "";
	}
	
	// removes all whitespace
	protected static String removeWhitespace (String s) {
		if (s != null) {
			return s.replaceAll("\\s+","");
		}
		return "";
	}
	
	// substitutes all whitespace with " "
	protected static String normalizeWhitespace (String s) {
		if (s != null) {
			return s.replaceAll("\\s+"," ");
		}
		return "";
	}

}