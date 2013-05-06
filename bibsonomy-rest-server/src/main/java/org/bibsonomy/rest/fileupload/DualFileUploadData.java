package org.bibsonomy.rest.fileupload;

import java.io.IOException;
import java.io.InputStream;

import org.bibsonomy.model.util.data.DualData;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author jensi
 * @version $Id$
 */
public class DualFileUploadData extends FileUploadData implements DualData {

	private final String mimeType;
	private final MultipartFile uploadedFile2;

	/**
	 * construct it
	 * @param mimeType mimetype for the combination
	 * @param uploadedFile
	 * @param uploadedFile2
	 */
	public DualFileUploadData(String mimeType, MultipartFile uploadedFile, MultipartFile uploadedFile2) {
		super(uploadedFile);
		this.mimeType = mimeType;
		this.uploadedFile2 = uploadedFile2;
	}
	
	@Override
	public InputStream getInputStream2() {
		try {
			return uploadedFile2.getInputStream();
		} catch (IOException ex) {
			throw new RuntimeException("cannot access second uploaded file with name '" + uploadedFile2.getName() + "'", ex);
		}
	}

	@Override
	public String getMimeType() {
		return this.mimeType;
	}

}
