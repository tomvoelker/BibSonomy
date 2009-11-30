/**
 *  
 *  BibSonomy-Rest-Client - The REST-client.
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
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

import java.io.File;
import java.io.IOException;

import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;

/**
 * Downloads a document for a specific post.
 * @author Waldemar Biller <wbi@cs.uni-kassel.de>
 * @version $Id$
 */
public class GetPostDocumentQuery extends AbstractQuery<File> {

	private File document;
	private final String username;
	private final String resourceHash;
	private boolean fileExists;

	public GetPostDocumentQuery(final String username, final String resourceHash, final String fileName, final String directory) {
		this(username, resourceHash, fileName, directory, directory, directory);
	}
	/**
	 * @param username
	 * @param resourceHash the resource hash of a specific post
	 * @param fileName the file name of the document
	 */
	public GetPostDocumentQuery(final String username, final String resourceHash, final String fileName, final String fileDirectory, final String pdfDirectory, final String psDirectory) {

		if ((username == null) || (username.length() == 0)) throw new IllegalArgumentException("no username given");
		if ((resourceHash == null) || (resourceHash.length() == 0)) throw new IllegalArgumentException("no resourceHash given");
		if ((fileName == null) || (fileName.length() == 0)) throw new IllegalArgumentException("no file name given");

		this.username = username;
		this.resourceHash = resourceHash;
		
		// create the file
		try {
			if(getExtension(fileName).equals("pdf")) {
				this.document = new File(pdfDirectory + "/" + fileName);
			} else if(getExtension(fileName).equals("ps")){
				this.document = new File(psDirectory + "/" + fileName);
			} else {
				this.document = new File(fileDirectory + "/" + fileName);
			}
			
			this.fileExists = !this.document.createNewFile();
			
		} catch (final IOException ex) {
			throw new IllegalArgumentException("could not create new file " + this.document.getAbsolutePath());
		}
	}

	@Override
	protected File doExecute() throws ErrorPerformingRequestException {
		if(!this.fileExists)
			this.performFileDownload(URL_USERS + "/" + this.username + "/posts/" + this.resourceHash + "/documents/" + this.document.getName(), this.document);
		else {
			this.setExecuted(true);
			this.setStatusCode(200);
		}
		return this.document;
	}
	
	private String getExtension(String filename) {
        if(filename != null) {
            int i = filename.lastIndexOf('.');
            if(i>0 && i<filename.length()-1) {
                return filename.substring(i+1).toLowerCase();
            }
        }
        return null;
    }

}
