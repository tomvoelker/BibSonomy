package tags;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.bibsonomy.common.enums.SpamStatus;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.util.UrlUtils;

import resources.Resource;


/**
 * Some taglib functions
 * 
 * @author Dominik Benz
 * @version $Id$
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
	 * 
	 * @param s a String
	 * @return trimmed String
	 */	
	public static String trimWhiteSpace (String s) {
		/*
		 * remove empty lines
		 */
		return s.replaceAll("(?m)\n\\s*\n", "\n");
	}

	/** Removes all "non-trivial" characters from the file name.
	 * If the file name is empty "export" is returned
	 * @param file a file name
	 * @return cleaned file name
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

	/**
	 * wrapper for URLDecoder.decode(URI, "UTF-8");
	 * 
	 * @param URI a URI string
	 * @return the decoded URI string
	 */
	public static String decodeURI (String URI) {
		if (URI != null) {
			try {
				return URLDecoder.decode(URI, "UTF-8");
			} catch (UnsupportedEncodingException e) {
			}
		}
		return null;
	}
	
	/**
	 * wrapper for URLEncoder.encode(URI, "UTF-8");
	 * 
	 * @param URI a URI string
	 * @return the encoded URI string
	 */
	public static String encodeURI (String URI) {
		if (URI != null) {
			try {
				return URLEncoder.encode(URI, "UTF-8");
			} catch (UnsupportedEncodingException e) {
			}
		}
		return null;
	}	

	/**
	 * @param input
	 * @return credential
	 */
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
	 * converts a list of tags in a space-separated string of tags 
	 * 
	 * @param tags a list of tags
	 * @return a space-separated string of tags
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
	 * get the Path component of a URI string
	 * 
	 * @param uriString a URI string
	 * @return the path component of the given URI string
	 */
	public static String getPath (String uriString) {
		URI uri;
		try {
			uri = new URI(UrlUtils.encodeURLExceptReservedChars(uriString));
			return uri.getPath();
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}		
	}
	
	/**
	 * Cuts the last segment of the url string until last slash
	 * 
	 * @param uriString the url
	 * @return last segment of the url string until last slash
	 */
	public static String getLowerPath (String uriString) {
		URI uri; 
		uriString = uriString.substring(0, uriString.lastIndexOf("/"));
		try {
			uri = new URI(UrlUtils.encodeURLExceptReservedChars(uriString));
			return uri.getPath();
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}	
	}
	
	/**
	 * extract query part of given URI string, within a leading "?"
	 * 
	 * @param uriString a URI string
	 * @return query part of the given URI string, within a leading "?"
	 */
	public static String getQuery (String uriString) {
		URI uri;
		try {
			uri = new URI(UrlUtils.encodeURLExceptReservedChars(uriString));
			if (uri.getQuery() != null && ! uri.getQuery().equals("")) { 
				return "?" + uri.getQuery();
			}
			return "";
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}		
	}	
	
	/**
	 * checks if a given String is numeric
	 * 
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
	
	/**
	 * parses a String of misc field entries
	 * 
	 * @param misc miscfield of a bibtex entry
	 * @return array of key value entrys
	 */
	public static ArrayList<String> miscFieldToArray(String misc){
		ArrayList<String> formattedMiscFields = new ArrayList<String>();
		HashMap<String, String> miscFields = BibTexUtils.parseMiscField(misc);
		if (miscFields != null) {
			for (String fieldName : miscFields.keySet()) {
				formattedMiscFields.add(fieldName + " = {" + miscFields.get(fieldName) + "}");
			}
		}
		return formattedMiscFields;
	}
	
	/**
	 * Computes font size for given tag frequency and maximum tag frequency inside tag cloud
	 * 
	 * this is used as attribute font-size=X%, hence values between 100 and 300 are returned
	 * 
	 * @param tagFrequency
	 * @param tagMaxFrequency
	 * @param tagSizeMode 
	 * @return font size for the tag cloud with the given parameters
	 */
	public static Integer computeTagFontsize(Integer tagFrequency, Integer tagMaxFrequency, String tagSizeMode) {
		// round(log(if(tag_anzahl>100, 100, tag_anzahl+6)/6))*60+40
		if ("home".equals(tagSizeMode)) {
			Double t = (tagFrequency > 100 ? 100.0 : tagFrequency.doubleValue() + 6);
			t /= 5;
			t = Math.log(t) * 100 + 60;
			if (t.intValue() < 100) 
					return 100;
			return t.intValue();
		}		
		return 100 + (tagFrequency / tagMaxFrequency * 200);
	}
	
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
	

	/**
	 * wrapper for for org.bibsonomy.util.UrlUtils.setParam
	 * 
	 * @param url an url string
	 * @param paramName parameter name
	 * @param paramValue parameter value
	 * @return an url string with the requested parameter set
	 */
	public static String setParam(String url, String paramName, String paramValue) {
		return UrlUtils.setParam(url, paramName, paramValue); 
	}
	
	/**
	 * wrapper for org.bibsonomy.model.util.BibTexUtils.cleanBibtex
	 * 
	 * @see org.bibsonomy.model.util.BibTexUtils
	 * @param bibtex
	 */
	public static String cleanBibtex(String bibtex) {
		return BibTexUtils.cleanBibTex(bibtex);
	}
	
	/**
	 * returns the SpamStatus as string for admin pages
	 * @param id id of the spammer state
	 * @return string representation
	 */
	public static String getPredictionString(final Integer id) {
		return SpamStatus.getStatus(id).toString();
	}	
	
	/**
	 * Retrieves if given status is a spammer status
	 * @param id
	 * @return 
	 */
	public static Boolean isSpammer(final Integer id) {
		SpamStatus status = SpamStatus.getStatus(id);
		return SpamStatus.isSpammer(status);
	}
}