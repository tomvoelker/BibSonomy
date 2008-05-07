package org.bibsonomy.webdav.resource;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.webdav.BibSonomyBackend;

import com.atlassian.confluence.extra.webdav.servlet.resource.Resource;

/**
 * Currently this holds just the logged in user.
 * 
 * @author Christian Schenk
 * @version $Id: BibTexCollectionResource.java,v 1.3 2008-04-29 06:19:47 cschenk
 *          Exp $
 */
public class UsersCollectionResource extends VirtualCollectionResource {

	private final String currentUserName;

	/**
	 * Constructs the new resource.
	 * 
	 * @param root
	 *            The resource's parent.
	 * @param backend
	 *            The backend
	 */
	public UsersCollectionResource(final RootCollectionResource root, final BibSonomyBackend backend) {
		super(root, backend, "user", "User");
		this.currentUserName = this.getBackend().getCurrentUser().getFullName();
	}

	public Resource getChild(final String name) {
		if (name.equals(this.currentUserName)) {
			return new UserCollectionResource(this, this.getBackend(), this.currentUserName, this.currentUserName);
		}
		return null;
	}

	public List<Resource> getChildren() {
		final List<Resource> children = new ArrayList<Resource>();
		children.add(new UserCollectionResource(this, this.getBackend(), this.currentUserName, this.currentUserName));
		return children;
	}
}