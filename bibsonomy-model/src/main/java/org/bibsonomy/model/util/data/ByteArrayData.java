package org.bibsonomy.model.util.data;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author jensi
 * @version $Id$
 */
public class ByteArrayData implements Data {
	private final byte[] data;
	private final String type;
	
	
	public ByteArrayData(final byte[] data, final String type) {
		this.data = data;
		this.type = type;
	}
	
	@Override
	public String getMimeType() {
		return type;
	}

	@Override
	public InputStream getInputStream() {
		return new ByteArrayInputStream(data);
	}

	@Override
	public Reader getReader() {
		return new InputStreamReader(getInputStream());
	}

}
