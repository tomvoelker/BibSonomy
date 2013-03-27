/**
 *
 *  BibSonomy-Rest-Client - The REST-client.
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

package org.bibsonomy.rest.client.util;

import java.io.File;

import org.bibsonomy.util.file.FileUtil;

/**
 * @author Jens Illig
 * @version $Id$
 */
public class MultiDirectoryFileFactory implements FileFactory {
	
	private final String fileDirectory;
	private final String pdfDirectory;
	private final String psDirectory;

	public MultiDirectoryFileFactory(final String fileDirectory, final String pdfDirectory, final String psDirectory) {
		this.fileDirectory = fileDirectory;
		this.pdfDirectory = pdfDirectory;
		this.psDirectory = psDirectory;
	}

	@Override
	public File getFile(String fileName) {
		final String extension = FileUtil.getFileExtension(fileName);
		if ("pdf".equals(extension)) {
			return new File(getPdfDirectory(), fileName);
		} else if ("ps".equals(extension)) {
			return new File(getPsDirectory(), fileName);
		} else {
			return new File(getFileDirectory(), fileName);
		}
	}

	/**
	 * @return the fileDirectory
	 */
	public String getFileDirectory() {
		return this.fileDirectory;
	}

	/**
	 * @return the pdfDirectory
	 */
	public String getPdfDirectory() {
		return this.pdfDirectory;
	}

	/**
	 * @return the psDirectory
	 */
	public String getPsDirectory() {
		return this.psDirectory;
	}

}
