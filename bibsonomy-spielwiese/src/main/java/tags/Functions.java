package tags;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * replaces occurrences of whitespace in the by only one occurrence of the
 * respective whitespace character
 */
public class Functions {

	public static String trimWhiteSpace(String s) {
		/*
		 * remove empty lines
		 */
		return s.replaceAll("(?m)\n\\s*\n", "\n");
	}

	/**
	 * Removes all "non-trivial" characters from the file name. If the file name
	 * is empty "export" is returned
	 * 
	 * @param file
	 * @return
	 */
	public static String makeCleanFileName(String file) {
		if (file == null || file.trim().equals("")) {
			return "export";
		}
		try {
			return URLDecoder.decode(file, "UTF-8").replaceAll("[^a-zA-Z0-9-_]", "_");
		} catch (UnsupportedEncodingException e) {
			return file.replaceAll("[^a-zA-Z0-9-_]", "_");
		}
	}

	public static String decodeURI(String URI) {
		if (URI != null) {
			try {
				return URLDecoder.decode(URI, "UTF-8");
			} catch (UnsupportedEncodingException e) {
			}
		}
		return null;
	}

	public static String makeCredential(String input) {
		// if (input != null) {
		// /*
		// * TODO: in the future this must be dynamic!
		// */
		// return Resource.hash(input + "security_by_obscurity");
		// }
		return null;
	}

}