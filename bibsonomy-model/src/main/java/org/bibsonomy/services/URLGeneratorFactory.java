package org.bibsonomy.services;

/**
 * a factory for {@link URLGenerator}
 * @author dzo
 */
public class URLGeneratorFactory {
	
	/**
	 * creates a new url generator for the specified systemUrl
	 * @param systemUrl
	 * @return the {@link URLGenerator} for the provided systemUrl
	 */
	public URLGenerator createURLGeneratorForSystem(final String systemUrl) {
		return new URLGenerator(systemUrl);
	}
}
