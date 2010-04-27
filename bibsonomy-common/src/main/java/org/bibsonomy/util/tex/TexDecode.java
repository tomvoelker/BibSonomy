/**
 *  
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *   
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.util.tex;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Framework to encode TeX Macros to unicode.
 * 
 * @author Christian Claus
 * @version $Id$
 */
public class TexDecode {

	private static HashMap<String, String> texMap = new HashMap<String, String>();
	private static Pattern texRegexpPattern;
	private static final String CURLS = "[()]*[{}]*[\\[\\]]*";

	// macro with the highest count of curly brackets have to lead this array
	private static final String[] TEX = { 
		"{\\c{C}}",		"{\\c{c}}", 	"{{\\\"\\i}}", 
		"{{\\^\\i}}",	"{{\\`\\i}}", 	"{{\\'\\i}}", 
		"{{\\aa}}", 	"{{\\AA}}", 	"{{\\ae}}", 
		"{{\\AE}}", 	"{{\\ss}}",		"{\\\"{A}}", 
		"{\\\"{O}}", 	"{\\\"{U}}", 	"{\\\"{a}}", 
		"{\\\"{o}}", 	"{\\\"{u}}", 	"{\\\"A}", 
		"{\\\"O}", 		"{\\\"U}", 		"{\\\"a}", 
		"{\\\"e}", 		"{\\\"u}", 		"{\\\"o}", 
		"{\\`a}", 		"{\\`e}", 		"{\\`o}", 
		"{\\`u}", 		"{\\^e}", 		"{\\^o}", 
		"{\\^a}", 		"{\\^u}", 		"{\\'a}", 
		"{\\'e}", 		"{\\'o}", 		"{\\'u}", 
		"{\\'E}", 		"{\\~n}", 		"{\"A}", 
		"{\"O}", 		"{\"U}", 		"{\"a}", 
		"{\"o}", 		"{\"u}", 		"{\\~N}", 
		"\\\"{A}", 		"\\\"{O}", 		"\\\"{U}", 		
		"\\\"{a}", 		"\\\"{o}", 		"\\\"{u}",
		"\\'{\\i}",		"{\\'i}",		"\\`{e}",
		"\\c{C}",		"\\c{c}",		"{\\~A}",
		"{\\~a}",		"\\\"E",		"\\\"e",
		"\\\"A", 		"\\\"O", 		"\\\"U",
		"\"A", 			"\"O", 			"\"U",
		"\\\"a", 		"\\\"o", 		"\\\"u",
		"\"a", 			"\"o", 			"\"u"
	};

	private static final String[] UNICODE = { 
		"\u00C7", 		"\u00E7", 		"\u00EF", 
		"\u00EE", 		"\u00EC", 		"\u00ED", 
		"\u00E5", 		"\u00C5", 		"\u00E6", 
		"\u00C6", 		"\u00DF",		"\u00C4", 
		"\u00D6", 		"\u00DC", 		"\u00E4", 		
		"\u00F6", 		"\u00FC",		"\u00C4", 
		"\u00D6", 		"\u00DC", 		"\u00E4", 
		"\u00EB", 		"\u00FC", 		"\u00F6", 
		"\u00E0", 		"\u00E8", 		"\u00F2", 
		"\u00F9", 		"\u00EA", 		"\u00F4", 
		"\u00E2", 		"\u00FB", 		"\u00E1", 
		"\u00E9", 		"\u00F3", 		"\u00FA", 
		"\u00C9", 		"\u00F1", 		"\u00C4", 	
		"\u00D6", 		"\u00DC", 		"\u00E4", 
		"\u00F6", 		"\u00FC", 		"\u00D1",
		"\u00C4", 		"\u00D6", 		"\u00DC", 		
		"\u00E4", 		"\u00F6", 		"\u00FC",
		"\u00ED",		"\u00ED",		"\u00E8",
		"\u00C7", 		"\u00E7",		"\u00C3",
		"\u00E3",		"\u00CB",		"\u00EB",
		"\u00C4", 		"\u00D6", 		"\u00DC",
		"\u00C4", 		"\u00D6", 		"\u00DC", 		
		"\u00E4", 		"\u00F6", 		"\u00FC",
		"\u00E4", 		"\u00F6", 		"\u00FC"
	};


	/**
	 * initializes the HashMap 'texMap' with TeX macros as key and a
	 * referenced Unicode value as value. Also builds the regex 
	 * for matching the tex macros.
	 */
	static {
		if(TEX.length == UNICODE.length) {
			final StringBuffer texRegexp = new StringBuffer();
			texRegexp.append("(");
			for(int i = 0; i < TEX.length; ++i) {
				// build tex -> unicode map
				texMap.put(TEX[i], UNICODE[i]);
				// build regex
				texRegexp.append(Pattern.quote(TEX[i]));
				texRegexp.append("|");
			}
			// delete last "|", add closing bracket
			texRegexp.deleteCharAt(texRegexp.length() - 1);
			texRegexp.append(")");
			// compile pattern
			texRegexpPattern = Pattern.compile(texRegexp.toString());
		}		
	}


	/**
	 * Decodes a String which contains TeX macros into it's Unicode representation.
	 * 
	 * @param s
	 * @return Unicode representation of the String
	 */
	public static String decode(String s) {
		if (s != null) {			
			 final Matcher texRegexpMatcher = texRegexpPattern.matcher(s);
			 final StringBuffer sb = new StringBuffer();
			 while (texRegexpMatcher.find()) {
				 texRegexpMatcher.appendReplacement(sb, texMap.get(texRegexpMatcher.group()));
			 }
			 texRegexpMatcher.appendTail(sb);
			return sb.toString().trim().replaceAll(CURLS, "");
		}
		return "";
	}


	/**
	 * old version of encode(..) with counting brackets - works also with an
	 * unsorted tex array
	 * encodes a String, containing TeX macros to it's Unicode representation
	 * 
	 * @param s
	 * @return Unicode representation of the String
	 *//*
	public String encode(String s) {
		s += " ";

		String[] splitted = s.split("");
		String result = s;
		int bracketCounter = 0;
		int preCount = 0;
		boolean changed = true;

		for (int i = 0; i < splitted.length; ++i) {
			if (bracketCounter == 0 && preCount <= (i - 1)) {
				if (changed) {
					String sub = s.substring(preCount, i - 1);
					if (sub != null && texMap.get(sub) != null) {
						result = result.replace(sub, texMap.get(sub));
						changed = false;
					}
				}
				preCount = i - 1;
			}

			if (splitted[i].equals("{")) {
				changed = true;
				bracketCounter++;
			} else if (splitted[i].equals("}")) {
				changed = true;
				bracketCounter--;
			} 
		}

		result = result.trim();
		return result;
	}*/


	/**
	 * Getter for the texMap
	 * 
	 * @return HashMap of TeX->Unicode representation
	 */
	protected static HashMap<String, String> getTexMap() {
		return texMap;
	}


	/**
	 * Getter for the TeX macros
	 * 
	 * @return String array of TeX macros
	 */
	protected static String[] getTEX() {
		return TEX;
	}


	/**
	 * Getter for Unicode signs
	 * 
	 * @return String array of Unicode signs
	 */
	protected static String[] getUNICODE() {
		return UNICODE;
	}	
	

}