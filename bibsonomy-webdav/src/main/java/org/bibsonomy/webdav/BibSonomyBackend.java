package org.bibsonomy.webdav;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.DBLogicUserInterfaceFactory;
import org.bibsonomy.database.util.IbatisDBSessionFactory;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.webdav.resource.RootCollectionResource;

import com.atlassian.confluence.extra.webdav.impl.UserImpl;
import com.atlassian.confluence.extra.webdav.servlet.WebdavClient;
import com.atlassian.confluence.extra.webdav.servlet.WebdavRequest;
import com.atlassian.confluence.extra.webdav.servlet.WebdavResponse;
import com.atlassian.confluence.extra.webdav.servlet.resource.BackendException;
import com.atlassian.confluence.extra.webdav.servlet.resource.CollectionResource;
import com.atlassian.confluence.extra.webdav.servlet.resource.InsufficientAuthorizationException;
import com.atlassian.confluence.extra.webdav.servlet.resource.Resource;
import com.atlassian.confluence.extra.webdav.servlet.resource.ResourceBackend;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;

/**
 * An implementation of a {@link ResourceBackend} which allows navigation of
 * BibSonomy via WebDAV.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class BibSonomyBackend implements ResourceBackend {

	private static final Logger log = Logger.getLogger(BibSonomyBackend.class);
	private static final String DEFAULT_NAME = "BibSonomy";
	private RootCollectionResource rootCollectionResource;
	private final LogicInterfaceFactory logicInterfaceFactory;
	private LogicInterface logicInterface;

	/**
	 * Constructor
	 */
	public BibSonomyBackend() {
		// FIXME: don't wire this here...
		this.logicInterfaceFactory = new DBLogicUserInterfaceFactory();
		((DBLogicUserInterfaceFactory) this.logicInterfaceFactory).setDbSessionFactory(new IbatisDBSessionFactory());
		this.logicInterface = null;
	}

	public void init(final ServletContext servletContext) throws BackendException {
		// Do nothing.
	}

	public void destroy() {
		// Do nothing.
	}

	public void initRequest(final WebdavRequest req) {
		// Do nothing.
	}

	public String getName() {
		return DEFAULT_NAME;
	}

	public Resource getRootResource() {
		if (this.rootCollectionResource == null) {
			this.rootCollectionResource = new RootCollectionResource(this);
		}
		return this.rootCollectionResource;
	}

	/**
	 * Convenience method that retruns the current user.
	 * 
	 * @return user that's currently logged in, may be <code>null</code>
	 */
	public User getCurrentUser() {
		return AuthenticatedUserThreadLocal.getUser();
	}

	public boolean authenticateUser(final String username, final String password) {
		log.debug("Username/Password: " + username + "/" + password);
		if (present(username) == false || present(password) == false || User.ANONYMOUS.equals(username)) return false;

		try {
			this.logicInterface = this.logicInterfaceFactory.getLogicAccess(username, StringUtils.getMD5Hash(password));
		} catch (ValidationException ignore) {
			return false;
		}

		AuthenticatedUserThreadLocal.setUser(new UserImpl(username));

		return true;
	}

	public boolean isUserAuthenticated() {
		return AuthenticatedUserThreadLocal.getUser() != null;
	}

	public boolean clearUserAuthentication() {
		if (AuthenticatedUserThreadLocal.getUser() != null) {
			AuthenticatedUserThreadLocal.setUser(null);
			return true;
		}
		return false;
	}

	// TODO: implement write access
	public boolean isReadOnly() {
		return true;
	}

	public boolean isLockingSupported() {
		return false;
	}

	/**
	 * Returns the LogicInterface or throws an
	 * InsufficientAuthorizationException if the user hasn't logged in yet.
	 * 
	 * @return global instance of the LogicInterface
	 */
	public LogicInterface getLogicInterface() {
		if (this.logicInterface == null) {
			throw new InsufficientAuthorizationException();
		}
		return this.logicInterface;
	}

	/**
	 * Borrowed from
	 * com.atlassian.confluence.extra.webdav.impl.ConfluenceBackend
	 */
	public String getCollectionXHTML(final CollectionResource crc, final WebdavRequest request, final WebdavResponse response) {
		String name = crc.getDisplayName();
		if (name == null) name = crc.getName();

		String urlPrefix = "";
		final String requestUrl = request.getRequestURI();
		if (!requestUrl.endsWith("/")) urlPrefix = requestUrl.substring(requestUrl.lastIndexOf('/') + 1) + '/';

		final StringBuffer out = new StringBuffer();

		out.append("<html><head><title>" + name + "</title></head>\n");
		out.append("<body>\n");
		out.append("<h1>").append("BibSonomy WebDAV " + name).append("</h1>\n");
		out.append("<hr/>\b");

		if (crc.getParent() != null) out.append("<p><a href=\"").append(urlPrefix).append("..\">..</a></p>\n");

		final List<Resource> children = new ArrayList<Resource>(crc.getChildren());
		Collections.sort(children, new Comparator<Resource>() {
			public int compare(final Resource resource1, final Resource resource2) {
				return resource1.getName().compareTo(resource2.getName());
			}
		});

		final Iterator<Resource> i = children.iterator();
		while (i.hasNext()) {
			final Resource child = i.next();
			out.append("<p><a href=\"");
			out.append(urlPrefix).append(request.getClient().encodeFileName(child.getName(), WebdavClient.ISO_8859_1_ENCODING));
			out.append("\">").append(child.getName()).append("</a></p>");
		}

		out.append("</body></html>\n");

		return out.toString();
	}
}