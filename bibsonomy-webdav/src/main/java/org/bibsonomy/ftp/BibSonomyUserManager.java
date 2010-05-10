package org.bibsonomy.ftp;

import java.util.ArrayList;
import java.util.List;

import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.AnonymousAuthentication;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.database.DBLogicUserInterfaceFactory;
import org.bibsonomy.database.util.IbatisDBSessionFactory;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;
import org.bibsonomy.util.StringUtils;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class BibSonomyUserManager implements UserManager {

	/* The UserManager handles the login, thus it holds the LogicInterface */
	private final LogicInterfaceFactory logicInterfaceFactory;
	private LogicInterface logicInterface;

	/**
	 * Constructor
	 */
	public BibSonomyUserManager() {
		// FIXME: don't wire this here...
		this.logicInterfaceFactory = new DBLogicUserInterfaceFactory();
		((DBLogicUserInterfaceFactory) this.logicInterfaceFactory).setDbSessionFactory(new IbatisDBSessionFactory());
		this.logicInterface = null;
	}

	/**
	 * Returns the LogicInterface or throws an
	 * InsufficientAuthorizationException if the user hasn't logged in yet.
	 * 
	 * @return global instance of the LogicInterface
	 */
	public LogicInterface getLogicInterface() {
		if (this.logicInterface == null) {
			throw new RuntimeException(new AuthenticationFailedException("Must authenticate first"));
		}
		return this.logicInterface;
	}

	/**
	 * Tries to get a LogicInterface instance for the given credentials. Doesn't
	 * support anonymous login.
	 */
	public User authenticate(final Authentication authentication) throws AuthenticationFailedException {
		if (authentication instanceof UsernamePasswordAuthentication) {
			final UsernamePasswordAuthentication upauth = (UsernamePasswordAuthentication) authentication;

			final String username = upauth.getUsername();
			final String password = upauth.getPassword();

			if (username == null || password == null) {
				throw new AuthenticationFailedException("Authentication failed (credentials missing)");
			}

			try {
				this.getLogicAccess(username, password);
				return getUserByName(username);
			} catch (final AccessDeniedException ex) {
				throw new AuthenticationFailedException(ex);
			} catch (final FtpException ex) {
				throw new AuthenticationFailedException("User doesn't exist", ex);
			}
		} else if (authentication instanceof AnonymousAuthentication) {
			throw new AuthenticationFailedException("Anonymous authentication isn't allowed");
		} else {
			throw new IllegalArgumentException("Authentication not supported");
		}
	}

	/**
	 * Tries to access the LogicInterface either with a hashed password or a
	 * plain text one.
	 * 
	 * @param username
	 * @param password
	 * @throws AuthenticationFailedException
	 */
	private void getLogicAccess(final String username, final String password) throws AuthenticationFailedException {
		try {
			this.logicInterface = this.logicInterfaceFactory.getLogicAccess(username, StringUtils.getMD5Hash(password));
		} catch (final AccessDeniedException ignored) {
			try {
				this.logicInterface = this.logicInterfaceFactory.getLogicAccess(username, password);
			} catch (final AccessDeniedException ex) {
				throw new AuthenticationFailedException(ex);
			}
		}
	}

	public User getUserByName(final String username) throws FtpException {
		if (username == null || "anonymous".equals(username)) return null;
		if (this.logicInterface == null) return null;

		final BaseUser user = new BaseUser();
		user.setName(username);
		user.setPassword(this.logicInterface.getAuthenticatedUser().getPassword());
		// user.setHomeDirectory("");
		// user.setEnabled(true);

		// TODO: set these values wisely
		final List<Authority> authorities = new ArrayList<Authority>();
		authorities.add(new ConcurrentLoginPermission(10, 10));
		// authorities.add(new TransferRatePermission(200, 200));
		user.setAuthorities(authorities);

		return user;
	}

	public String[] getAllUserNames() throws FtpException {
		return new String[] { "admin" };
	}

	public boolean doesExist(final String username) throws FtpException {
		// Although we don't really know whether the user exists or not we'll
		// return true here either way
		return true;
	}

	public String getAdminName() throws FtpException {
		return "admin";
	}

	public boolean isAdmin(final String username) throws FtpException {
		if ("admin".equals(username)) return true;
		return false;
	}

	public void delete(String username) throws FtpException {
	}

	public void save(User username) throws FtpException {
	}
}