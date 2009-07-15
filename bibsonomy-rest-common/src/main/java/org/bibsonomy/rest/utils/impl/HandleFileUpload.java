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
import org.bibsonomy.common.exceptions.UnsupportedFileTypeException;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.utils.FileUploadInterface;
import org.bibsonomy.util.HashUtils;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.file.FileUtil;

/**
 * Handles the file upload
 * 
 * @author Christian Kramer
 * @version $Id: HandleFileUpload.java,v 1.5 2009-06-23 14:11:37 voigtmannc Exp
 *          $
 */
public class HandleFileUpload implements FileUploadInterface {

	private String fileHash;
	private String fileName;
	private String md5hash;
	private FileItem upFile;

	private String docpath = null;

	/**
	 * firefox extion
	 */
	public static final String[] firfoxImportExt = { "html" };

	/**
	 * pdf, ps, djv, djvu, txt extensions
	 */
	public static final String[] fileUploadExt = { "pdf", "ps", "djv", "djvu", "txt" };

	/**
	 * layout defintion extension
	 */
	public static final String[] fileLayoutExt = { "layout" };

	/**
	 * initializes the file upload handler
	 * 
	 * @param items
	 * @param allowedExt
	 * @throws UnsupportedFileTypeException
	 */
	public void setUp(final List<FileItem> items, String[] allowedExt) throws UnsupportedFileTypeException {
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
		if (filename != null) {
			this.fileName = FilenameUtils.getName(filename);
		} else {
			this.fileName = "";
		}

		// "pdf", "ps", "djv", "djvu", "txt"
		// check file extensions which we accept
		if (this.fileName.equals("") || !StringUtils.matchExtension(this.fileName, allowedExt)) {
			throw new UnsupportedFileTypeException(allowedExt);
		}

		// format date
		final Date currDate = new Date();
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getDefault());
		final String currDateFormatted = df.format(currDate);

		// create hash over file content
		this.md5hash = HashUtils.getMD5Hash(this.upFile.get());

		this.fileHash = StringUtils.getMD5Hash(this.upFile.getName() + Math.random() + currDateFormatted);
	}

	/**
	 * default constructor
	 */
	public HandleFileUpload() {

	}

	/**
	 * writes an uploaded file to the disk and returns the object
	 * 
	 * @return file
	 * @throws Exception
	 */
	public File writeUploadedFile() throws Exception {

		File file = new File(FileUtil.getDocumentPath(this.docpath, this.fileHash));

		try {
			this.upFile.write(new File((FileUtil.getDocumentPath(this.docpath, this.fileHash))));
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

		return file;
	}

	@Override
	public Document writeUploadedFile(String docpath, String userName) throws Exception {

		Document document = null;

		File file = new File(FileUtil.getDocumentPath(docpath, fileHash));

		try {
			this.upFile.write(file);

			document = new Document();

			document.setUserName(userName);
			document.setFileName(this.fileName);

			document.setFileHash(fileHash);
			document.setMd5hash(this.md5hash);

		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

		return document;
	}

	@Override
	public Document writeUploadedFile(String fileHash, User loginUser) throws Exception {

		Document document = null;

		File file = new File(FileUtil.getDocumentPath(this.docpath, fileHash));

		try {
			this.upFile.write(file);

			document = new Document();

			document.setUserName(loginUser.getName());
			document.setFileName(this.fileName);

			document.setFileHash(fileHash);
			document.setMd5hash(this.md5hash);

		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

		return document;
	}

	@Override
	public Document writeUploadedFile(String userName) throws Exception {
		
		Document document = null;

		File file = new File(FileUtil.getDocumentPath(this.docpath, fileHash));

		try {
			this.upFile.write(file);

			document = new Document();

			document.setUserName(userName);
			document.setFileName(this.fileName);

			document.setFileHash(fileHash);
			document.setMd5hash(this.md5hash);

		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

		return document;
	}

	@Override
	public String getDocpath() {
		return this.docpath;
	}

	public void setDocpath(String docpath) {
		this.docpath = docpath;
	}
}