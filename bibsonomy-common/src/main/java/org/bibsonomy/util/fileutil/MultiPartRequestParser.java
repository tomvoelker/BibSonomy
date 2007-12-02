package org.bibsonomy.util.fileutil;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Parses the request items
 * 
 * @author Christian Kramer
 * @version $Id$
 */
public class MultiPartRequestParser {

	// initialize the max size ~50MB
	final int MAX_REQUEST_SIZE = 1024 * 1024 * 51;
	private List<FileItem> items;

	/**
	 * @param request
	 * @throws FileUploadException
	 */
	@SuppressWarnings("static-access")
	public MultiPartRequestParser(final HttpServletRequest request) throws FileUploadException {
		// the factory to hold the file
		final FileItemFactory factory = new DiskFileItemFactory();
		final ServletFileUpload upload = new ServletFileUpload(factory);

		/*
		 * need to check if the request content-type isn't null because the
		 * apache.commons.fileupload doesn't to that so the junit tests will
		 * fail with a nullpointer exception
		 */
		boolean isMultipart = false;
		if (request.getContentType() != null) {
			isMultipart = upload.isMultipartContent(request);
		}

		// online parse the items if the content-type is multipart
		if (isMultipart) {
			upload.setSizeMax(MAX_REQUEST_SIZE);

			// parse the items
			this.items = upload.parseRequest(request);
		}
	}

	public List<FileItem> getList() {
		return this.items;
	}
}