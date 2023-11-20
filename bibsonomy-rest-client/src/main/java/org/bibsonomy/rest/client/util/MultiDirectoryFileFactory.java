/**
 * BibSonomy-Rest-Client - The REST-client.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.client.util;

import java.io.File;

import org.bibsonomy.util.file.FileUtil;

/**
 * @author Jens Illig
 */
public class MultiDirectoryFileFactory implements FileFactory {
	private static final String FILE_NAME_DELIMITER = "_";
	
	private final String fileDirectory;
	private final String pdfDirectory;
	private final String psDirectory;

	public MultiDirectoryFileFactory(final String fileDirectory, final String pdfDirectory, final String psDirectory) {
		this.fileDirectory = fileDirectory;
		this.pdfDirectory = pdfDirectory;
		this.psDirectory = psDirectory;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.client.util.FileFactory#getFileForResourceDocument(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public File getFileForResourceDocument(String username, String hash, String filename) {
		final String fullFileName = username + FILE_NAME_DELIMITER + hash + FILE_NAME_DELIMITER + filename;
		final String extension = FileUtil.getFileExtension(fullFileName);
		if ("pdf".equals(extension)) {
			return new File(getPdfDirectory(), fullFileName);
		} else if ("ps".equals(extension)) {
			return new File(getPsDirectory(), fullFileName);
		} else {
			return new File(getFileDirectory(), fullFileName);
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
