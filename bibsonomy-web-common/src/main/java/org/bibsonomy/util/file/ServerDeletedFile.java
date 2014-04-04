package org.bibsonomy.util.file;

import java.io.File;
import java.io.IOException;

import org.bibsonomy.model.util.file.FilePurpose;
import org.bibsonomy.model.util.file.UploadedFile;

/**
 * A file deleted on server.
 * 
 * @author cunis
 */
public class ServerDeletedFile implements UploadedFile {

	@Override
	public String getFileName() {
		return "";
	}

	@Override
	public String getAbsolutePath() {
		return "";
	}

	@Override
	public byte[] getBytes() throws IOException {
		throw new IOException("Requested file has been deleted!");
	}

	@Override
	public void transferTo(File fileInFileSytem) throws Exception {
		//nothing to do
		throw new IOException("Requested file has been deleted!");
	}

	@Override
	public FilePurpose getPurpose() {
		return FilePurpose.DELETE;
	}

}
