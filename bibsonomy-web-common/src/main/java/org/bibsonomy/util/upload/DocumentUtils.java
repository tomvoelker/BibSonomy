package org.bibsonomy.util.upload;

import java.io.File;
import java.io.IOException;

import org.bibsonomy.model.Document;
import org.bibsonomy.util.file.FileUtil;
import org.springframework.util.FileCopyUtils;

/**
 * @author rja
 * @version $Id$
 */
public class DocumentUtils {


	/**
	 * Copies the temporary file to the documents directory.
	 * 
	 * 
	 * @param tmpPath - path where the temporary file resides
	 * @param docPath - path where the file shall be stored
	 * @param userName
	 * @param compoundFileName - Contains the temporary and the real file name.
	 * Structure:
	 * <pre>
	 * 
	 * 		0 ...                  31 32 ...           63 64 ...
	 * 		MD5-Hash of file contents temporary file name original file name
	 * </pre>
	 * @return The document that represents the file.
	 */
	public static Document getPersistentDocument(final String tmpPath, final String docPath, final String userName, final String compoundFileName) {
		/*
		 * FIXME: Since the MD5 hash is coming from the outside, it can be 
		 * manipulated - security bug!
		 */
		final String md5Hash     = compoundFileName.substring(0, 31);  // MD5 hash of the file contents
		final String tmpFileName = compoundFileName.substring(32, 64); // temporary file name on disk
		final String fileName    = compoundFileName.substring(64);     // original file name (stored in DB only)
		/*
		 * The file is stored on disk with this file name. 
		 */
		final String fileNameHash = FileUtil.getRandomFileHash(fileName);
		/*
		 * get temporary file
		 */
		final File tmpFile = new File(tmpPath + tmpFileName);
		/*
		 * create new (final) file
		 */
		final File file = new File(FileUtil.getFileDir(docPath, fileNameHash) + fileNameHash);
		/*
		 * copy from tmp directory to documents directory
		 */
		try {
			FileCopyUtils.copy(tmpFile, file);
		} catch (IOException ex) {

		}
		/*
		 * delete temporary file
		 */
		tmpFile.delete();
		/*
		 * create document
		 */
		final Document document = new Document();
		document.setFileName(fileName);
		document.setFileHash(fileNameHash);
		document.setMd5hash(md5Hash);
		document.setUserName(userName);

		return document;
	}
}
