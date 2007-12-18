package tags;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Properties;

import org.bibsonomy.model.Tag;

import resources.Resource;


/**
 * Some taglib functions
 * 
 * @author dbenz
 */
public class Functions  {

	// contains special characters, symbols, etc...
	private static Properties chars = new Properties(); 
		
	// load special characters
	static {
	    try {
	        chars.load(Functions.class.getClassLoader().getResourceAsStream("chars.properties"));
	    } catch (IOException e) {
	    	throw new RuntimeException(e.getMessage());
	    }	    	    		
	}
	
	/**
	 * lookup a special character
	 * 
	 * @param key
	 * @return String 
	 */
	public static String ch (String key) {
		if (chars.getProperty(key) != null) {
			return chars.getProperty(key);
		}
		return "???" + key + "???";
	}
	
	/**
	 * replaces occurrences of whitespace in the by only one occurrence of the 
	 * respective whitespace character  
	 */	
	public static String trimWhiteSpace (String s) {
		/*
		 * remove empty lines
		 */
		return s.replaceAll("(?m)\n\\s*\n", "\n");
	}

	/** Removes all "non-trivial" characters from the file name.
	 * If the file name is empty "export" is returned
	 * @param file
	 * @return
	 */
	public static String makeCleanFileName (String file) {
		if (file == null || file.trim().equals("")) {
			return "export";
		} 
		try {
			return URLDecoder.decode(file, "UTF-8").replaceAll("[^a-zA-Z0-9-_]", "_");
		} catch (UnsupportedEncodingException e) {
			return file.replaceAll("[^a-zA-Z0-9-_]", "_");
		}
	}

	public static String decodeURI (String URI) {
		if (URI != null) {
			try {
				return URLDecoder.decode(URI, "UTF-8");
			} catch (UnsupportedEncodingException e) {
			}
		}
		return null;
	}
	
	public static String encodeURI (String URI) {
		if (URI != null) {
			try {
				return URLEncoder.encode(URI, "UTF-8");
			} catch (UnsupportedEncodingException e) {
			}
		}
		return null;
	}	

	public static String makeCredential (String input) {
		if (input != null) {
			/*
			 * TODO: in the future this must be dynamic!
			 */
			return Resource.hash(input + "security_by_obscurity");
		}
		return null;
	}
	
	/**
	 * @param tags
	 * @return
	 */
	public static String toTagString (List<Tag> tags) {		
		StringBuffer sb = new StringBuffer();
		for (Tag tag : tags) {
			sb.append(tag.getName());
			sb.append(" ");
		}
		return sb.toString().substring(0, sb.length() - 1);
	}
	
	/**
	 * @param tags
	 * @return
	 */
	public static String getPath (String uriString) {
		URI uri;
		try {
			uri = new URI(uriString);
			return uri.getPath();
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}		
	}
	
	/**
	 * @param tags
	 * @return
	 */
	public static String getQuery (String uriString) {
		URI uri;
		try {
			uri = new URI(uriString);
			if (uri.getQuery() != null && ! uri.getQuery().equals("")) { 
				return "?" + uri.getQuery();
			}
			return "";
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}		
	}	
	
	/**
	 * @param input - string that will be tested of numbers
	 * @return true in case of parameter input is a number otherwise return false
	 */
	public static Boolean isNumeric(String input){	
		try{
			Integer.parseInt(input);
		}catch(NumberFormatException nfe){
			return false;
		}
		return true;
	}
	
}