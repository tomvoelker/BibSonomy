package org.bibsonomy.util.upload.impl;

import java.io.IOException;

import org.bibsonomy.util.upload.FileUploadInterface;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author cvo
 * @version $Id$
 */
public class FileUploadFactory {

	private String docpath = null;

    private boolean tempPath = false;
	
    /**
     * TODO: improve documentation
     * 
     * @param items
     * @param allowedExt
     * @return TODO
     */
	public FileUploadInterface getFileUploadHandler(final MultipartFile file, final String[] allowedExt) throws IOException {
		return new HandleFileUpload(file, allowedExt, this.docpath, this.tempPath);
	}
	
	/**
	 * @return the docpath
	 */
	public String getDocpath() {
		return this.docpath;
	}

	/**
	 * Sets the path where the documents from fileupload should be stored.
	 *  
	 * @param docpath - path where documents shall be stored.
	 */
	public void setDocpath(String docpath) {
		this.docpath = docpath;
	}
	
	/**
	 * @return the tempPath
	 */
	public boolean getTempPath() {
		return this.tempPath;
	}

	/**
	 * 
	 * 
	 * @param tempPath - if <code>true</code>, the files will be stored 
	 * temporarily - thus another directory naming scheme is used
	 */
	public void setTempPath(boolean tempPath) {
		this.tempPath = tempPath;
	}
	
}
