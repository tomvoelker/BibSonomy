package tags;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.model.Tag;
import org.bibsonomy.util.UrlUtils;

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
	 * Cuts the last segment of the url string until last slash
	 * @param uriString the url
	 */
	public static String getLowerPath (String uriString) {
		URI uri; 
		uriString = uriString.substring(0, uriString.lastIndexOf("/"));
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
	
	/**
	 * 
	 * @param misc miscfield of a bibtex entry
	 * @return array of key value entrys
	 */
	public static ArrayList<String> miscFieldToArray(String misc){
		ArrayList<String> formattedMiscFields = new ArrayList<String>();
		if (misc != null) {
			Matcher m = Pattern.compile("([a-zA-Z]+)\\s*=\\s*\\{(.+?)\\}").matcher(misc);
			while (m.find()) {
				formattedMiscFields.add(m.group(1)+" = {"+m.group(2)+"}");	
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
	 * @param mode
	 * @return
	 */
	public static Integer computeTagFontsize(Integer tagFrequency, Integer tagMaxFrequency, String tagSizeMode) {
		// round(log(if(tag_anzahl>100, 100, tag_anzahl+6)/6))*60+40
		if ("home".equals(tagSizeMode)) {
			Double t = (tagFrequency > 100 ? 100.0 : tagFrequency.doubleValue() + 6);
			t /= 6;
			t = Math.log(t) * 60 + 40;
			if (t.intValue() < 100) 
					return 100;
			return t.intValue();
		}		
		return 100 + (tagFrequency / tagMaxFrequency * 200);
	}
	
	/**
	 * Wrapper for org.bibsonomy.util.UrlUtils.cleanUrl
	 * 
	 * @see org.bibsonomy.util.UrlUtils.cleanUrl
	 * @param url
	 * @return the cleaned url
	 */
	public static String cleanUrl(String url) {
		return UrlUtils.cleanUrl(url);
	}
	
}