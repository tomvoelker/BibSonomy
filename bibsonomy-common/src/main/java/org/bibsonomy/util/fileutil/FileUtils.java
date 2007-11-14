package org.bibsonomy.util.fileutil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

/**
 * This class provides some necessary file utils like create a md5 hash or
 * check the file extension. It's a copy from the DocumentUpload- and 
 * DocumentDownloadHandler from bibsonomy-webapp.
 *
 * @version $Id$
 * @author Christian Kramer
 *
 */
public class FileUtils {
	private static final Logger log = Logger.getLogger(FileUtils.class);
	
    /**
     * Calculates the MD5-Hash of a String s and returns it encoded as a hex string of 32 characters length.
     * 
     * @param s the string to be hashed
     * @return the MD5 hash of s as a 32 character string 
     * 
     * taken from Resource.java
     */
    public String getMD5Hash (final String s) {
    	if (s == null) {
    		return null;
    	} else {
    		final String charset = "UTF-8";
    		try {
    			MessageDigest md = MessageDigest.getInstance("MD5");
    			return toHexString(md.digest(s.getBytes(charset)));
    		} catch (java.io.UnsupportedEncodingException e) {
    			return null;
    		} catch (NoSuchAlgorithmException e) {
    			return null;
    		}
    	}
    }

    /**
     * Converts a buffer of bytes into a string of hex values.
     * 
     * @param buffer array of bytes which should be converted
     * @return hex string representation of buffer
     * 
     * taken from Resource.java
     */
    public String toHexString (byte[] buffer) {
    	StringBuffer result = new StringBuffer();
    	int i;
    	for (i = 0; i < buffer.length; i++) {
    		String hex = Integer.toHexString ((int) buffer[i]);
    		if (hex.length() == 1) {
    			hex = "0" + hex;
    		}
    		result.append(hex.substring(hex.length() - 2));
    	}
    	return result.toString();
    }
    
	/**
	 * Checks if a given string has one of the chosen extensions.
	 * @param s String to test
	 * @param extensions Extensions to match.
	 * @return true if String matches with extension
	 * 
	 *  taken from DocumentUploadHandler
	 */
	public static boolean matchExtension(String s, String... extensions){
		for (String ext : extensions) {
			if (s.length() >= ext.length() && s.substring(s.length() - ext.length(), s.length()).equalsIgnoreCase(ext))
				return true;
		}  
		return false;
	}
}
