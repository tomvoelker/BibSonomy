package org.bibsonomy.ftp;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class BibSonomyFtpServer {

	/**
	 * @throws FtpException
	 */
	public BibSonomyFtpServer() throws FtpException {
		final FtpServerFactory serverFactory = new FtpServerFactory();
		final BibSonomyUserManager userManager = new BibSonomyUserManager();
		final BibSonomyFileSystemFactory fileSystemFactory = new BibSonomyFileSystemFactory(new BibSonomyFileSystemData(userManager));
		serverFactory.setUserManager(userManager);
		serverFactory.setFileSystem(fileSystemFactory);

		// change port
		final ListenerFactory factory = new ListenerFactory();
		factory.setPort(2221);
		serverFactory.addListener("default", factory.createListener());

		final FtpServer server = serverFactory.createServer();
		server.start();
	}

	/**
	 * @param args
	 * @throws FtpException
	 */
	public static void main(final String[] args) throws FtpException {
		new BibSonomyFtpServer();
	}
}