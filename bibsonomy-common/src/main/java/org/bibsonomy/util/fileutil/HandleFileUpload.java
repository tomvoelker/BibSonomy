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

/**
 * Handles the file upload
 * 
 * @version $Id$
 * @author Christian Kramer
 *
 */
public class HandleFileUpload implements FileUploadInterface {
	
	private static Map<String, FileItem> fieldMap;
	
	private static String fileHash;
	private final String fileName;
	private static FileItem upFile;
	
	/**
	 * @param items
	 * @throws Exception
	 */
	public HandleFileUpload(final List<FileItem> items) throws Exception{
		FileUtils util = new FileUtils();
		
		fieldMap = new HashMap<String, FileItem>();
		
		for (FileItem temp : items) {
			if (("file").equals(temp.getFieldName())){
				fieldMap.put(temp.getFieldName(), temp);
			}			
		}
		
		
		upFile = fieldMap.get("file");
		String filename = upFile.getName();
		if (filename != null) {
			this.fileName = FilenameUtils.getName(filename);
		}
		else {
			this.fileName = "";
		}
		
		/*
		 * check file extensions which we accept
		 */
		if (fileName.equals("") || !FileUtils.matchExtension(fileName, "pdf", "ps", "djv", "djvu")) {
			throw new Exception ("Please check your file. Only PDF, PS or DJVU files are accepted.");
		}
		
		// format date
		Date currDate = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getDefault());
		String currDateFormatted = df.format(currDate);
		
		fileHash = util.getMD5Hash(upFile.getFieldName() + Math.random() + currDateFormatted);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.util.fileutil.FileUploadInterface#writeUploadedFiles(java.lang.String, java.lang.String)
	 */
	public void writeUploadedFiles(final String rootPath, final String docPath) throws Exception{
		upFile.write(new File((rootPath + docPath + fileHash.substring(0, 2).toLowerCase()), fileHash));
	}
	

	/* (non-Javadoc)
	 * @see org.bibsonomy.util.fileutil.FileUploadInterface#getFileHash()
	 */
	public String getFileHash() {
		return fileHash;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.util.fileutil.FileUploadInterface#getFileName()
	 */
	public String getFileName() {
		return this.fileName;
	}
	
}