package org.bibsonomy.model.util.data;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.bibsonomy.model.enums.ImportFormat;

/**
 * @author jensi
 * @version $Id$
 */
public class FileData implements Data {
	private final File file;
	private final String mimeType;
	
	/**
	 * @param file
	 * @param format
	 */
	public FileData(File file, ImportFormat format) {
		this(file, format.getMimeType());
	}
	
	/**
	 * @param file
	 * @param mimeType
	 */
	public FileData(File file, String mimeType) {
		this.file = file;
		this.mimeType = mimeType;
	}
	
	@Override
	public String getMimeType() {
		return mimeType;
	}

	@Override
	public InputStream getInputStream() {
		return getInputStream(file);
	}
	
	protected InputStream getInputStream(File file) {
		try {
			return new BufferedInputStream(new FileInputStream(file));
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

}
