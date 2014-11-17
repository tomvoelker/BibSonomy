package org.bibsonomy.rest.fileupload;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.bibsonomy.model.util.data.Data;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.ValidationUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author jensi
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

	@Override
	public Reader getReader() {
		return new InputStreamReader(getInputStream(), Charset.forName(StringUtils.CHARSET_UTF_8));
	}
}
