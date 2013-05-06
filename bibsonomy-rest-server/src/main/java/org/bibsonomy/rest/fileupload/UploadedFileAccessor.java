package org.bibsonomy.rest.fileupload;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.model.util.data.Data;
import org.bibsonomy.model.util.data.DataAccessor;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;


/**
 * Abstraction from the request allowing access to uploaded multipart files.
 * 
 * This expects that the extraction of the file has been done before - typically
 * by Spring's DispatcherServlet. If this is not the case, the document upload
 * fails!
 * 
 * @author Jens Illig
 * @version $Id$
 */
public class UploadedFileAccessor implements DataAccessor {
	private final HttpServletRequest request;

	/**
	 * Create the accessor.
	 * 
	 * @param request
	 *            the request to search for fileUploads - may be null which results in a 'no uploaded files available'-behavior
	 */
	public UploadedFileAccessor(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * @param multipartName
	 *            name of the HTTP-Multipart containing the file
	 * @return the Uploaded File or null if none was found with the given name
	 */
	public MultipartFile getUploadedFileByName(String multipartName) {
		if (request instanceof MultipartHttpServletRequest) {
			return ((MultipartHttpServletRequest) request).getFile(multipartName);
		}
		return null;
	}

	@Override
	public Data getData(String multipartName) {
		MultipartFile file = getUploadedFileByName(multipartName);
		if (file == null) {
			return null;
		}
		return new FileUploadData(file);
	}
}
