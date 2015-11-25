/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
