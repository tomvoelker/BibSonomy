package org.bibsonomy.rest.client.util;

import java.io.File;

import org.bibsonomy.util.file.FileUtil;

/**
 * @author Jens Illig
 * @version $Id$
 */
public class MultiDirectoryFileFactory implements FileFactory {
	
	private final String fileDirectory;
	private final String pdfDirectory;
	private final String psDirectory;

	public MultiDirectoryFileFactory(final String fileDirectory, final String pdfDirectory, final String psDirectory) {
		this.fileDirectory = fileDirectory;
		this.pdfDirectory = pdfDirectory;
		this.psDirectory = psDirectory;
	}

	@Override
	public File getFile(String fileName) {
		final String extension = FileUtil.getFileExtension(fileName);
		if ("pdf".equals(extension)) {
			return new File(getPdfDirectory(), fileName);
		} else if ("ps".equals(extension)) {
			return new File(getPsDirectory(), fileName);
		} else {
			return new File(getFileDirectory(), fileName);
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
