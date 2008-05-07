package org.bibsonomy.webdav.resource;

import java.util.List;

import org.bibsonomy.webdav.BibSonomyBackend;

import com.atlassian.confluence.extra.webdav.servlet.resource.Resource;

/**
 * This class is the logical root of the resource hierarchy.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class RootCollectionResource extends StandardFilesCollectionResource {

	private final BibSonomyCollectionResource userFolder;
	private final BibSonomyCollectionResource groupFolder;

	/**
	 * Constructor.
	 * 
	 * @param backend
	 *            The backend
	 */
	public RootCollectionResource(final BibSonomyBackend backend) {
		super(null, backend, "", "RootCollectionResource");
		this.userFolder = new UsersCollectionResource(this, backend);
		this.groupFolder = new GroupsCollectionResource(this, backend);
	}

	@Override
	public List<Resource> getChildren() {
		final List<Resource> children = super.getChildren();
		children.add(this.userFolder);
		children.add(this.groupFolder);
		return children;
	}

	@Override
	public Resource getChild(final String name) {
		final Resource standardResource = super.getChild(name);
		if (standardResource != null) return standardResource;

		if (this.userFolder.getName().equals(name)) {
			return this.userFolder;
		} else if (this.groupFolder.getName().equals(name)) {
			return this.groupFolder;
		}

		return null;
	}
}