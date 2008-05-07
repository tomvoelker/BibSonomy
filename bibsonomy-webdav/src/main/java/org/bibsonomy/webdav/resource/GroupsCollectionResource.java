package org.bibsonomy.webdav.resource;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.model.Group;
import org.bibsonomy.webdav.BibSonomyBackend;

import com.atlassian.confluence.extra.webdav.servlet.resource.Resource;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class GroupsCollectionResource extends VirtualCollectionResource {

	private final String currentUserName;
	private final List<Group> groups;

	/**
	 * Constructs the new resource.
	 * 
	 * @param root
	 *            The resource's parent.
	 * @param backend
	 *            The backend
	 */
	public GroupsCollectionResource(final RootCollectionResource root, final BibSonomyBackend backend) {
		super(root, backend, "group", "Group");
		this.currentUserName = this.getBackend().getCurrentUser().getFullName();
		this.groups = this.getBackend().getLogicInterface().getUserDetails(this.currentUserName).getGroups();
	}

	public Resource getChild(final String name) {
		for (final Group group : this.groups) {
			if (name.equals(group.getName()) == false) continue;
			return new GroupCollectionResource(this, this.getBackend(), group.getName(), group.getName());
		}
		return null;
	}

	public List<Resource> getChildren() {
		final List<Resource> children = new ArrayList<Resource>();
		for (final Group group : this.groups) {
			children.add(new GroupCollectionResource(this, this.getBackend(), group.getName(), group.getName()));
		}
		return children;
	}
}