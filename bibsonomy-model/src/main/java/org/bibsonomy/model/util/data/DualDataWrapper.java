package org.bibsonomy.model.util.data;

import java.io.InputStream;
import java.io.Reader;

/**
 * @author jensi
 * @version $Id$
 */
public class DualDataWrapper implements DualData {

	private final Data first;
	private final Data second;

	/**
	 * construct
	 * @param first
	 * @param second
	 */
	public DualDataWrapper(final Data first, final Data second) {
		this.first = first;
		this.second = second;
	}
	
	@Override
	public String getMimeType() {
		return "" + first.getMimeType() + ":" + second.getMimeType();
	}

	@Override
	public InputStream getInputStream() {
		return this.first.getInputStream();
	}

	@Override
	public Reader getReader() {
		return this.first.getReader();
	}

	@Override
	public InputStream getInputStream2() {
		return this.second.getInputStream();
	}

	@Override
	public Reader getReader2() {
		return this.second.getReader();
	}

	@Override
	public Data getData2() {
		return second;
	}

}
