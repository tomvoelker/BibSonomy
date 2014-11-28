/**
 * BibSonomy-Web-Common - Common things for web
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.util.file;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.File;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.UnsupportedFileTypeException;
import org.bibsonomy.model.util.file.UploadedFile;
import org.bibsonomy.services.filesystem.extension.ExtensionChecker;
import org.bibsonomy.util.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author dzo
 */
public abstract class AbstractServerFileLogic {
	private static final Log log = LogFactory.getLog(AbstractServerFileLogic.class);
	
	/**
	 * Used to compute the file hash.
	 */
	private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	
	/** the base path of the files */
	protected final String path;
	
	/**
	 * default constructor
	 * @param path
	 */
	public AbstractServerFileLogic(String path) {
		this.path = path;
	}
	
	/**
	 * writes a file to the filesystem
	 * @param file
	 * @param extensionChecker
	 * @return the file written to the file system read only
	 * @throws Exception
	 */
	protected File writeFile(final UploadedFile file, ExtensionChecker extensionChecker) throws Exception {
		final String filename = file.getFileName();
		
		this.checkFile(extensionChecker, filename);
		
		final String fileHash = getFileHash(filename);
		final String documentPath = this.getFilePath(fileHash);
		return writeFile(file, documentPath);
	}
	/**
	 * check file extensions which we accept
	 * 
	 * @param extensionChecker
	 * @param filename
	 */
	protected void checkFile(ExtensionChecker extensionChecker, final String filename) {
		if (!present(filename) || !extensionChecker.checkExtension(filename)) {
			throw new UnsupportedFileTypeException(extensionChecker.getAllowedExtensions());
		}
	}
	
	/**
	 * writes file to the specified path
	 * @param file
	 * @param documentPath
	 * @return the written file
	 * @throws Exception
	 */
	protected File writeFile(final UploadedFile file, final String documentPath) throws Exception {
		final File fileInFileSytem = new File(documentPath);

		try {
			file.transferTo(fileInFileSytem);
		} catch (final Exception ex) {
			log.error("Could not write uploaded file.", ex);
			throw ex;
		}
		fileInFileSytem.setReadOnly();
		return fileInFileSytem;
	}

	/**
	 * @param filename
	 * @return the filehash based on the filename
	 */
	protected String getFileHash(String filename) {
		return StringUtils.getMD5Hash(filename + Math.random() + fmt.print(new Date().getTime()));
	}
	
	/**
	 * @param fileHash
	 * @return the file path with the filehash
	 */
	protected abstract String getFilePath(String fileHash);
}
