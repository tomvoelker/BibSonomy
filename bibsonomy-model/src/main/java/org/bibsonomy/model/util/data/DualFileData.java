package org.bibsonomy.model.util.data;

import java.io.File;
import java.io.InputStream;

import org.bibsonomy.model.enums.ImportFormat;

/**
 * @author jensi
 * @version $Id$
 */
public class DualFileData extends FileData implements DualData {

	private final File file2;

	/**
	 * @param file
	 * @param file2
	 * @param format
	 */
	public DualFileData(File file, File file2, ImportFormat format) {
		super(file, format.getMimeType());
		this.file2 = file2;
	}
	
	/**
	 * @param file
	 * @param file2
	 * @param mimeType
	 */
	public DualFileData(File file, File file2, String mimeType) {
		super(file, mimeType);
		this.file2 = file2;
	}

	@Override
	public InputStream getInputStream2() {
		return getInputStream(file2);
	}

}
