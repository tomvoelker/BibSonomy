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

import org.bibsonomy.model.util.file.UploadedFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author dzo
  */
public class ServerUploadedFile implements UploadedFile {
	private MultipartFile file;
	
	/**
	 * @param file
	 */
	public ServerUploadedFile(MultipartFile file) {
		this.file = file;
	}

	@Override
	public String getFileName() {
		return this.file.getOriginalFilename();
	}
	
	@Override
	public byte[] getBytes() throws IOException {
		return this.file.getBytes();
	}
	
	@Override
	public void transferTo(File fileInFileSytem) throws Exception {
		this.file.transferTo(fileInFileSytem);
	}

}
