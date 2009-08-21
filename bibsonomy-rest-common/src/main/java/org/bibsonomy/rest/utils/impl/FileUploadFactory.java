package org.bibsonomy.rest.utils.impl;

import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.bibsonomy.rest.utils.FileUploadInterface;

/**
 * @author cvo
 * @version $Id$
 */
public class FileUploadFactory {

	private String docpath = null;

    private boolean tempPath = false;
	
	public FileUploadInterface getFileUploadHandler(final List<FileItem> items, String[] allowedExt) {
		return new HandleFileUpload(items, allowedExt, this.docpath, this.tempPath);
	}
	
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
