package org.bibsonomy.model.util.data;

import java.io.InputStream;
import java.io.Reader;

/**
 * @author jensi
 * @version $Id$
 */
public class Data2Wrapper implements Data {

	private final DualData dualFileData;

	/**
	 * @param dualFileData
	 */
	public Data2Wrapper(DualData dualFileData) {
		this.dualFileData = dualFileData;
	}

	@Override
	public String getMimeType() {
		return dualFileData.getMimeType();
	}

	@Override
	public InputStream getInputStream() {
		return this.dualFileData.getInputStream2();
	}

	@Override
	public Reader getReader() {
		return this.dualFileData.getReader2();
	}

}
