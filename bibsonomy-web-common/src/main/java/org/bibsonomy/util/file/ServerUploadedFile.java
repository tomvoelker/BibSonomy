package org.bibsonomy.util.file;

import java.io.File;
import java.io.IOException;

import org.bibsonomy.model.util.file.UploadedFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author dzo
 * @version $Id$
 */
public class ServerUploadedFile implements UploadedFile {
	private MultipartFile file;
	
	/**
	 * @param file
	 */
	public ServerUploadedFile(MultipartFile file) {
		this.file = file;
	}

	@Override
	public String getFileName() {
		return this.file.getOriginalFilename();
	}
	
	@Override
	public byte[] getBytes() throws IOException {
		return this.file.getBytes();
	}
	
	@Override
	public void transferTo(File fileInFileSytem) throws Exception {
		this.file.transferTo(fileInFileSytem);
	}

}
