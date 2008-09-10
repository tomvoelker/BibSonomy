package org.bibsonomy.util.fileutil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
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
	private final FileItem upFile;

	/**
	 * @param items
	 * @throws Exception
	 */
	public HandleFileUpload(final List<FileItem> items) throws Exception {
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

		// check file extensions which we accept
		if (this.fileName.equals("") || !StringUtils.matchExtension(this.fileName, "pdf", "ps", "djv", "djvu", "txt")) {
			throw new Exception("Please check your file. Only PDF, PS, TXT or DJVU files are accepted.");
		}

		// format date
		final Date currDate = new Date();
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getDefault());
		final String currDateFormatted = df.format(currDate);

		this.fileHash = StringUtils.getMD5Hash(this.upFile.getFieldName() + Math.random() + currDateFormatted);
	}

	/*
	 * @see org.bibsonomy.util.fileutil.FileUploadInterface#writeUploadedFiles(java.lang.String,
	 *      java.lang.String)
	 */
	public void writeUploadedFiles(final String docPath) throws Exception {
		this.upFile.write(new File((docPath + this.fileHash.substring(0, 2).toLowerCase()), this.fileHash));
	}

	/*
	 * @see org.bibsonomy.util.fileutil.FileUploadInterface#getFileHash()
	 */
	public String getFileHash() {
		return this.fileHash;
	}

	/*
	 * @see org.bibsonomy.util.fileutil.FileUploadInterface#getFileName()
	 */
	public String getFileName() {
		return this.fileName;
	}
}