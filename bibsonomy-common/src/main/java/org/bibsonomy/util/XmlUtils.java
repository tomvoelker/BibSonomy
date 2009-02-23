package org.bibsonomy.util;

import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharArrays;
import it.unimi.dsi.fastutil.chars.CharOpenHashSet;

/**
 * Some utility functions for working with XML
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 * @version $Id$
 */
public class XmlUtils {

	/** the following is used by the removeControlCharacters-methods below */	
	private static final CharOpenHashSet illegalChars;
	/** replacement character for illegal characters */
	private static final char ILLEGAL_CHAR_SUBSTITUTE = '\uFFFD'; 	
	
	/** 
	 * define disallowed characters in XML 1.0
	 * see http://www.w3.org/International/questions/qa-controls.en.php for details 
	 */
    static {
        final String escapeString = "\u0000\u0001\u0002\u0003\u0004\u0005" +
            "\u0006\u0007\u0008\u000B\u000C\u000E\u000F\u0010\u0011\u0012" +
            "\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001A\u001B\u001C" +
            "\u001D\u001E\u001F\uFFFE\uFFFF";

        illegalChars = new CharOpenHashSet();
        for (int i = 0; i < escapeString.length(); i++) {
            illegalChars.add(escapeString.charAt(i));
        }
    }
    
    /**
     * Substitutes all illegal characters in the given string by the value of
     * {@link XmlUtils#ILLEGAL_CHAR_SUBSTITUTE}. 
     *
     * @param string
     * @return a string with control characters removed
     */
    public static String removeXmlControlCharacters(String string, final Boolean substitute) {
    	if (string == null) return string;
    	char[] ch = string.toCharArray();
    	StringBuilder sb = new StringBuilder(ch.length);    	
        for (int i = 0; i < ch.length; i++) {
            if (XmlUtils.illegalChars.contains(ch[i])) {
            	sb.append(substitute ? XmlUtils.ILLEGAL_CHAR_SUBSTITUTE : "");
            }
            else {
            	sb.append(ch[i]);
            }
        }
        return sb.toString();    	    	
    }
    
    /**
     * wrapper method for {@link XmlUtils.removeXmlControlCharacters(String string, final Boolean substitute)} 
     * 
     * @param string a string
     * @return a string with control characters removed
     */
    public static String removeXmlControlCharacters(String string) {
    	return XmlUtils.removeXmlControlCharacters(string, false);
    }
    
    /**
     * Substitutes all illegal characters in the given char array by the value of
     * {@link XmlUtils#ILLEGAL_CHAR_SUBSTITUTE}. If no illegal characters
     * were found, no copy is made and the given char array
     * 
     * @param ch a char array
     * @return a char array with control characters removed
     */
    public static char[] removeXmlControlCharacters(char[] ch, final Boolean substitute) {
    	StringBuilder sb = new StringBuilder(ch.length);
        for (int i = 0; i < ch.length; i++) {
            if (XmlUtils.illegalChars.contains(ch[i])) {
            	sb.append(substitute ? XmlUtils.ILLEGAL_CHAR_SUBSTITUTE : "");
            }
            else {
            	sb.append(ch[i]);
            }
        }
        return sb.toString().toCharArray();    
    }
    
    /**
     * wrapper method for {@link XmlUtils.removeXmlControlCharacters(char[] ch, boolean substitute)}
     * 
     * @param ch a char array
     * @return a char array with control characters removed
     */
    public static char[] removeXmlControlCharacters(char[] ch) {
    	return XmlUtils.removeXmlControlCharacters(ch, false);
    }
    
    /**
     * Checks if a given char c is a control character; if yes, a replacement
     * is returned, if no, the char c is returned
     * 
     * @param c a char
     * @return a char with control characters removed
     */
    public static char removeXmlControlCharacter(char c, final Boolean substitute) {
    	if (XmlUtils.illegalChars.contains(c)) {
    		return substitute ? XmlUtils.ILLEGAL_CHAR_SUBSTITUTE : ' ' ;
    	}
    	return c;
    }
    
    /**
     * Wrapper for {@link XmlUtils.removeXmlControlCharacter(char c, final Boolean substitute)}
     * 
     * @param c a char
     * @return a char with control charaters removed
     */
    public static char removeXmlControlCharacters(char c) {
    	return XmlUtils.removeXmlControlCharacter(c, false);
    }       
	
}
