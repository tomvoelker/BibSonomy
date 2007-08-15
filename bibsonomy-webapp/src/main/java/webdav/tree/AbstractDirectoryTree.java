package webdav.tree;

import java.util.List;

import webdav.beans.TagNode;
import webdav.beans.TagNodeCollection;

/**
 * This should be the superclass of all implementations of {@link webdav.tree.DirectoryTree}. It
 * sets some variables and implements some methods, which are likely to be the same in all
 * implementations.
 * 
 * @author Christian Schenk
 */
public abstract class AbstractDirectoryTree implements DirectoryTree {
	/** The {@link webdav.beans.TagNodeCollection} which holds the actual directory tree */
	protected TagNodeCollection tagNodeCollection;
	/** The rootpath of the directory where files can be stored */
	protected final String storeRootPath;

	/**
	 * The constructor just sets some variables.
	 * 
	 * @param storeRootPath A path to the directory where files should be saved on harddisk
	 */
	public AbstractDirectoryTree(final String storeRootPath) {
		this.tagNodeCollection = null;
		this.storeRootPath = storeRootPath;
	}

	/**
	 * Needs to be called if a derived class overloads this method to make sure that a fresh
	 * {@link webdav.beans.TagNodeCollection} is created.
	 */
	public void update() {
		this.tagNodeCollection = new TagNodeCollection();
		this.tagNodeCollection.addNode("/");
		this.tagNodeCollection.addNode("/files");
	}

	public List<TagNode> getChildren(final String str) {
		return this.tagNodeCollection.getChildren(str);
	}

	public TagNode getNode(final String str) {
		return this.tagNodeCollection.getNode(str);
	}

	public void addNode(final String str) {
		this.tagNodeCollection.addNode(str);
	}
}