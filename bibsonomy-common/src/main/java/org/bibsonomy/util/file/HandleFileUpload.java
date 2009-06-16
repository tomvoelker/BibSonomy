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

package org.bibsonomy.util.file;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.bibsonomy.util.HashUtils;
import org.bibsonomy.util.StringUtils;

/**
 * Handles the file upload
 * 
 * @author Christian Kramer
 * @version $Id$
 */
public class HandleFileUpload implements FileUploadInterface {

	private final Map<String, FileItem> fieldMap;
	private final String fileHash;
	private final String fileName;
	private final String md5hash;
	private final FileItem upFile;
	
	public static final String[] firfoxImportExt = {"html"}; 
	public static final String[] fileUploadExt   = {"pdf", "ps", "djv", "djvu", "txt"};

	/**
	 * @param items
	 * @param allowedExt 
	 * @throws Exception
	 */
	public HandleFileUpload(final List<FileItem> items, String[] allowedExt) throws Exception {
		this.fieldMap = new HashMap<String, FileItem>();

		// copy items into global field map
		for (final FileItem temp : items) {
			if ("file".equals(temp.getFieldName())) {
				this.fieldMap.put(temp.getFieldName(), temp);
			}
		}

		this.upFile = this.fieldMap.get("file");
		final String filename = this.upFile.getName();
		if (filename != null) {
			this.fileName = FilenameUtils.getName(filename);
		} else {
			this.fileName = "";
		}

		// "pdf", "ps", "djv", "djvu", "txt"
		// check file extensions which we accept
		if (this.fileName.equals("") || !StringUtils.matchExtension(this.fileName, allowedExt)) {
			throw new Exception("Please check your file. Only " + getExceptionExtensions(allowedExt) + " files are accepted.");
		}

		// format date
		final Date currDate = new Date();
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getDefault());
		final String currDateFormatted = df.format(currDate);
		
		// create hash over file content
		this.md5hash = HashUtils.getMD5Hash(this.upFile.get());

		this.fileHash = StringUtils.getMD5Hash(this.upFile.getFieldName() + Math.random() + currDateFormatted);
	}
	
	/**
	 * Converts the given files extensions to upper cases and connects them with "," and "or", e.g.:
	 * input:
	 *   "pdf", "ps", "djv", "djvu", "txt"
	 * output:
	 *   "PDF, PS, TXT or DJVU"
	 * @param allowedExt
	 * @return
	 */
	private static String getExceptionExtensions(final String[] allowedExt) {
		final StringBuffer buf = new StringBuffer();
		for (int i = 0; i < allowedExt.length - 1; i++) {
			buf.append(allowedExt[i].toUpperCase() + ", ");
		}
		if (allowedExt.length > 1) {
			buf.append(" or ");
		}
		buf.append(allowedExt[allowedExt.length - 1]);
		return buf.toString();
	}

	/*
	 * @see org.bibsonomy.util.fileutil.FileUploadInterface#writeUploadedFiles(java.lang.String,
	 *      java.lang.String)
	 */
	public void writeUploadedFiles(final String docPath) throws Exception {
		this.upFile.write(new File((FileUtil.getDocumentPath(docPath, this.fileHash))));
	}
	
	/*
	 * @see org.bibsonomy.util.fileutil.FileUploadInterface#writeUploadedFilesAndReturnFile(java.lang.String,
	 *      java.lang.String)
	 */
	public File writeUploadedFilesAndReturnFile(final String docPath) throws Exception {
		
		File file = new File(FileUtil.getDocumentPath(docPath, this.fileHash));
		
		this.upFile.write(file);
		
		return file;
	}

	/*
	 * @see org.bibsonomy.util.fileutil.FileUploadInterface#getFileHash()
	 */
	public String getFileHash() {
		return this.fileHash;
	}
	
	/*
	 * @see org.bibsonomy.util.fileutil.FileUploadInterface#getMd5Hash()
	 */
	public String getMd5Hash() {
		return this.md5hash;
	}

	/*
	 * @see org.bibsonomy.util.fileutil.FileUploadInterface#getFileName()
	 */
	public String getFileName() {
		return this.fileName;
	}
}