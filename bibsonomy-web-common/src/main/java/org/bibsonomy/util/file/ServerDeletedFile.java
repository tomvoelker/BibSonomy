/**
 *
 *  BibSonomy-Web-Common - Common things for web
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
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
