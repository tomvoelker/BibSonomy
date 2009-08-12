package org.bibsonomy.ftp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.User;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class BibSonomyFileSystemFactory implements FileSystemFactory {

	private static final Log log = LogFactory.getLog(BibSonomyFileSystemFactory.class);

	/** Special file that contains all publications */
	private static final String ALL_BIBTEX = "bibtex-all.bib";
	/** Special file that contains all bookmarks */
	private static final String ALL_BOOKMARK = "bookmarks.html";

	private final BibSonomyFileSystemData data;

	/**
	 * @param data
	 */
	public BibSonomyFileSystemFactory(final BibSonomyFileSystemData data) {
		this.data = data;
	}

	public FileSystemView createFileSystemView(final User user) throws FtpException {
		return new FileSystemView() {

			private String currDir = "/";

			public boolean changeWorkingDirectory(final String dir) throws FtpException {
				log.debug("Changing work dir: " + dir);

				// canonic version of the dir name
				final String cDir = dir.replaceFirst(".?/", "");
				if ("".equals(cDir)) {
					currDir = dir;
					return true;
				}

				return false;
			}

			public FtpFile getFile(final String file) throws FtpException {
				log.debug("Getting file: " + file);

				if ("/".equals(file) || "./".equals(file)) {
					final BibSonomyFtpFile root = new BibSonomyFtpFile("");
					for (final String child : new String[] { ALL_BIBTEX, ALL_BOOKMARK }) {
						root.addChild(getFile(child));
					}
					return root;
				}
				if (file.contains(ALL_BIBTEX)) {
					final BibSonomyFtpFile ftpFile = new BibSonomyFtpFile(file);
					ftpFile.setContent(data.getAllBibTex());
					return ftpFile;
				}
				if (file.contains(ALL_BOOKMARK)) {
					final BibSonomyFtpFile ftpFile = new BibSonomyFtpFile(file);
					ftpFile.setContent(data.getAllBookmark());
					return ftpFile;
				}

				return null;
			}

			public FtpFile getHomeDirectory() throws FtpException {
				log.debug("Getting home dir...");
				return new BibSonomyFtpFile("");
			}

			public FtpFile getWorkingDirectory() throws FtpException {
				log.debug("Getting work dir...");
				return new BibSonomyFtpFile(currDir);
			}

			public boolean isRandomAccessible() throws FtpException {
				return true;
			}

			public void dispose() {
			}
		};
	}
}