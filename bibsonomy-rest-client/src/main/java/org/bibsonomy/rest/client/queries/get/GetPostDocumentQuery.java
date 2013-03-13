/**
 *
 *  BibSonomy-Rest-Client - The REST-client.
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.rest.client.queries.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;

import org.bibsonomy.model.Document;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.util.FileFactory;
import org.bibsonomy.rest.client.util.MultiDirectoryFileFactory;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * Downloads a document for a specific post.
 * 
 * @author Waldemar Biller <wbi@cs.uni-kassel.de>
 * @version $Id$
 */
public class GetPostDocumentQuery extends AbstractQuery<Document> {

	private final Document document;
	private final String resourceHash;
	private boolean fileExists;

	/**
	 * @param username the user name
	 * @param resourceHash the resource hash
	 * @param fileName the filename
	 * @param directory the dir
	 */
	public GetPostDocumentQuery(final String username, final String resourceHash, final String fileName, final String directory) {
		this(username, resourceHash, fileName, new MultiDirectoryFileFactory(directory, directory, directory));
	}
	
	/**
	 * @param username
	 * @param resourceHash the resource hash of a specific post
	 * @param fileName the file name of the document
	 * @param fileDirectory 
	 * @param pdfDirectory 
	 * @param psDirectory 
	 */
	public GetPostDocumentQuery(final String username, final String resourceHash, final String fileName, FileFactory fileFactory) {
		if (!present(username)) throw new IllegalArgumentException("no username given");
		if (!present(resourceHash)) throw new IllegalArgumentException("no resourceHash given");
		if (!present(fileName)) throw new IllegalArgumentException("no file name given");
		
		this.document = new Document();
		this.document.setFileName(fileName);
		this.document.setUserName(username);
		this.resourceHash = resourceHash;
		
		// create the file
		try {
			this.document.setFile(fileFactory.getFile(fileName));
			this.fileExists = !this.document.getFile().createNewFile();
		} catch (final IOException ex) {
			throw new IllegalArgumentException("could not create new file " + this.document.getFile().getAbsolutePath());
		}
	}

	@Override
	protected Document doExecute() throws ErrorPerformingRequestException {
		if (!this.fileExists) {
			this.performFileDownload(RESTConfig.USERS_URL + "/" + this.document.getUserName() + "/posts/" + this.resourceHash + "/documents/" + this.document.getFileName(), this.document.getFile());
		} else {
			// TODO: never overwrite? what if there is a new document?
			this.setExecuted(true);
			this.setStatusCode(200);
		}
		return this.document;
	}
}
