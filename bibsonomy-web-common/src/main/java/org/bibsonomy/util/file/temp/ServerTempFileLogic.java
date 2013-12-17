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

package org.bibsonomy.util.file.temp;

import java.io.File;

import org.bibsonomy.model.util.file.UploadedFile;
import org.bibsonomy.services.filesystem.TempFileLogic;
import org.bibsonomy.services.filesystem.extension.ExtensionChecker;
import org.bibsonomy.util.file.AbstractServerFileLogic;

/**
 * @author dzo
  */
public class ServerTempFileLogic extends AbstractServerFileLogic implements TempFileLogic {
	
	/**
	 * @param path
	 */
	public ServerTempFileLogic(String path) {
		super(path);
	}
	
	@Override
	public File getTempFile(String name) {
		final File file = new File(this.getFilePath(name));
		file.setReadOnly();
		return file;
	}
	
	@Override
	public File writeTempFile(UploadedFile file, ExtensionChecker extensionChecker) throws Exception {
		return this.writeFile(file, extensionChecker);
	}

	@Override
	protected String getFilePath(String fileHash) {
		return this.path + "/" + fileHash;
	}

	@Override
	public void deleteTempFile(String name) {
		new File(getFilePath(name)).delete();
	}
}
