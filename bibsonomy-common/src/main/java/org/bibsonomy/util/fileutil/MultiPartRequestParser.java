package org.bibsonomy.util.fileutil;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Parses the request items
 * 
 * @version $Id$
 * @author Christian Kramer
 *
 */
public class MultiPartRequestParser {

	private Map<String, FileItem> fieldMap;
	private boolean isMultipart = false;
	private List<FileItem> items;

	/**
	 * @param request
	 * @throws FileUploadException
	 */
	public MultiPartRequestParser(final HttpServletRequest request) throws FileUploadException{
		
		// the factory to hold the file
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		
		/*
		 * need to check if the request content-type isn't null
		 * because the apache.commons.fileupload doesn't to that
		 * so the junit tests will fail with a nullpointer exception
		 */
		if(request.getContentType() != null) {
			isMultipart = upload.isMultipartContent(request);
		}
		
		// online parse the items if the content-type is multipart
		if (isMultipart)
		{	
			// initialize the max size ~50MB
			int maxRequestSize = 1024 * 1024 * 51;
			upload.setSizeMax(maxRequestSize);
			
			// parse the items
			items = upload.parseRequest(request);
		}
	}
	
	public List<FileItem> getList(){
		return this.items;
	}
}