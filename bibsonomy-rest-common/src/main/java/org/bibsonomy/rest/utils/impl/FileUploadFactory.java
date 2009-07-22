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

	
	public FileUploadFactory(){};
	
	public FileUploadInterface getFileUploadHandler(final List<FileItem> items, String[] allowedExt) {
		return new HandleFileUpload(items, allowedExt, this.docpath);
	}
	
	public String getDocpath() {
		return this.docpath;
	}

	public void setDocpath(String docpath) {
		this.docpath = docpath;
	}
}
