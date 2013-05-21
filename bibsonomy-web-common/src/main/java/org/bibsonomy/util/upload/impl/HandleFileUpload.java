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

package org.bibsonomy.util.upload.impl;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.UnsupportedFileTypeException;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.User;
import org.bibsonomy.util.HashUtils;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.file.FileUtil;
import org.bibsonomy.util.upload.FileUploadInterface;
import org.springframework.web.multipart.MultipartFile;

/**
 * Handles the file upload
 * 
 * @author Christian Kramer
 * @version $Id$
 */
public class HandleFileUpload implements FileUploadInterface {
	private static final Log log = LogFactory.getLog(HandleFileUpload.class);
	
	/**
	 * Used to compute the file hash.
	 */
	private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	static {
		df.setTimeZone(TimeZone.getDefault());
	}

	private final Document document = new Document();
	private final MultipartFile upFile;

	private final String docPath;
	private final boolean isTempPath;

	/**
	 * default constructor
	 */
	protected HandleFileUpload(final MultipartFile file, final String[] allowedExt, final String docPath, final boolean isTempPath) throws IOException {
		
		if (!present(file)) {
			throw new IOException("no file given");
		}
		
		this.docPath = docPath;
		this.isTempPath = isTempPath;
		
		this.upFile = file;
		
		final String filename = this.upFile.getOriginalFilename();
		if (present(filename)) {
			this.document.setFileName(FilenameUtils.getName(filename));
		}
		
		// check file extensions which we accept
		if (!present(document.getFileName()) || !StringUtils.matchExtension(this.document.getFileName(), allowedExt)) {
			throw new UnsupportedFileTypeException(allowedExt);
		}

		// create hash over file content
		try {
			this.document.setMd5hash(HashUtils.getMD5Hash(this.upFile.getBytes()));
		} catch (IOException e) {
			log.error("Could not compute hash for file " + this.upFile.getOriginalFilename(), e);
		}

		// compute random file hash
		this.document.setFileHash(StringUtils.getMD5Hash(this.upFile.getOriginalFilename() + Math.random() + df.format(new Date())));
	}

	/**
	 * writes an uploaded file to the disk and returns the object
	 * 
	 * @return file
	 * @throws Exception
	 */
	@Override
	public Document writeUploadedFile() throws Exception {
		final String documentPath;
		if (isTempPath) {
			documentPath = docPath + "/" + document.getFileHash();
		} else {
			documentPath = FileUtil.getFilePath(docPath, document.getFileHash());
		}
		
		document.setFile(new File(documentPath));

		try {
			this.upFile.transferTo(this.document.getFile());
		} catch (final Exception ex) {
			log.error("Could not write uploaded file.", ex);
			throw ex;
		}

		return document;
	}

	@Override
	public Document writeUploadedFile(final String fileHash, final User loginUser) throws Exception {
		document.setFileHash(fileHash);
		document.setUserName(loginUser.getName());
		return this.writeUploadedFile();
	}

}