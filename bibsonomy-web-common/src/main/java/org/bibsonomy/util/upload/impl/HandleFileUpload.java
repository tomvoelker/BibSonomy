package org.bibsonomy.util.upload.impl;

import static org.bibsonomy.util.ValidationUtils.present;

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
import org.bibsonomy.util.HashUtils;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.file.FileUtil;
import org.bibsonomy.util.upload.FileUploadInterface;

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
	private FileItem upFile;

	private final String docPath;
	private final boolean isTempPath;

	/**
	 * default constructor
	 */
	protected HandleFileUpload(final List<FileItem> items, final String[] allowedExt, final String docPath, final boolean isTempPath) {
		this.docPath = docPath;
		this.isTempPath = isTempPath;
		
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
		if (present(filename)) {
			this.document.setFileName(FilenameUtils.getName(filename));
		}
		
		// check file extensions which we accept
		if (!present(document.getFileName()) || !StringUtils.matchExtension(document.getFileName(), allowedExt)) {
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
		final String documentPath;
		if (isTempPath) {
			documentPath = docPath + "/" + document.getFileHash();
		} else {
			documentPath = FileUtil.getFilePath(docPath, document.getFileHash());
		}
		
		document.setFile(new File(documentPath));

		try {
			this.upFile.write(document.getFile());
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