package org.bibsonomy.rest.fileupload;

import java.io.IOException;
import java.io.InputStream;

import org.bibsonomy.model.util.data.Data;
import org.bibsonomy.util.ValidationUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author jensi
 * @version $Id$
 */
public class FileUploadData implements Data {
	private final MultipartFile uploadedFile;
	
	/**
	 * construct
	 * @param uploadedFile
	 */
	public FileUploadData(final MultipartFile uploadedFile) {
		ValidationUtils.assertNotNull(uploadedFile);
		this.uploadedFile = uploadedFile;
	}
	
	@Override
	public String getMimeType() {
		return uploadedFile.getContentType();
	}

	@Override
	public InputStream getInputStream() {
		try {
			return uploadedFile.getInputStream();
		} catch (IOException ex) {
			throw new RuntimeException("cannot access uploaded file with name '" + uploadedFile.getName() + "'", ex);
		}
	}

}
