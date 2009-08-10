/**
 *  
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
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

package org.bibsonomy.rest.utils.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.UnsupportedFileTypeException;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.utils.FileUploadInterface;
import org.bibsonomy.util.HashUtils;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.util.file.FileUtil;

/**
 * Handles the file upload
 * 
 * @author Christian Kramer
 * @version $Id: HandleFileUpload.java,v 1.5 2009-06-23 14:11:37 voigtmannc Exp
 *          $
 */
public class HandleFileUpload implements FileUploadInterface {
	
	private static final Log log = LogFactory.getLog(HandleFileUpload.class);

	private Document document = new Document();
	private FileItem upFile;

	private String docPath;
	
	/**
	 * firefox extion
	 */
	public static final String[] firefoxImportExt = { "html" };

	/**
	 * pdf, ps, djv, djvu, txt extensions
	 */
	public static final String[] fileUploadExt = { "pdf", "ps", "djv", "djvu", "txt" };

	/**
	 * layout defintion extension
	 */
	public static final String[] fileLayoutExt = { "layout" };

	/**
	 * Used to compute the file hash.
	 */
	private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static {
		df.setTimeZone(TimeZone.getDefault());
	}

	/**
	 * default constructor
	 */
	protected HandleFileUpload(final List<FileItem> items, final String[] allowedExt, final String docPath) {

		this.docPath = docPath;
		
		if (items.size() == 1) {
			this.upFile = items.get(0);
		} else {

			// copy items into global field map
			for (final FileItem temp : items) {
				if ("file".equals(temp.getFieldName())) {
					this.upFile = temp;
				}
			}
		}

		final String filename = this.upFile.getName();
		if (ValidationUtils.present(filename)) {
			this.document.setFileName(FilenameUtils.getName(filename));
		}

		// "pdf", "ps", "djv", "djvu", "txt"
		// check file extensions which we accept
		if (!ValidationUtils.present(document.getFileName()) || !StringUtils.matchExtension(document.getFileName(), allowedExt)) {
			throw new UnsupportedFileTypeException(allowedExt);
		}

		// create hash over file content
		this.document.setMd5hash(HashUtils.getMD5Hash(this.upFile.get()));

		// compute random file hash
		this.document.setFileHash(StringUtils.getMD5Hash(this.upFile.getName() + Math.random() + df.format(new Date())));
	}

	/**
	 * writes an uploaded file to the disk and returns the object
	 * 
	 * @return file
	 * @throws Exception
	 */
	@Override
	public Document writeUploadedFile() throws Exception {

		document.setFile(new File(FileUtil.getDocumentPath(docPath, document.getFileHash())));

		try {
			this.upFile.write(document.getFile());
		} catch (Exception ex) {
			log.error("Could not write uploaded file.", ex);
			throw ex;
		}

		return document;
	}

	@Override
	public Document writeUploadedFile(final String fileHash, final User loginUser) throws Exception {
		document.setFileHash(fileHash);
		document.setUserName(loginUser.getName());
		return writeUploadedFile();
	}

}