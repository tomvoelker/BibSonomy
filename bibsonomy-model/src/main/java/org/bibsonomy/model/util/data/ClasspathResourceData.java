package org.bibsonomy.model.util.data;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author jensi
 * @version $Id$
 */
public class ClasspathResourceData implements Data {

	private final String mimeType;
	private final String resourcePath;

	/**
	 * construct
	 * @param resourcePath
	 * @param mimeType
	 */
	public ClasspathResourceData(String resourcePath, String mimeType) {
		this.resourcePath = resourcePath;
		this.mimeType = mimeType;
	}
	
	@Override
	public String getMimeType() {
		return mimeType;
	}

	@Override
	public InputStream getInputStream() {
		return getClass().getResourceAsStream(resourcePath);
	}

	@Override
	public Reader getReader() {
		return new InputStreamReader(getInputStream());
	}

}
