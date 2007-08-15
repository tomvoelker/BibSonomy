package webdav.tree;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import webdav.beans.TagNode;

/**
 * A WebDAV directory tree. It contains methods for manipulating the tree.
 * 
 * @author Christian Schenk
 */
public interface DirectoryTree {
	/**
	 * Rebuilds the tree.
	 */
	public void update();

	/**
	 * Adds a node to the tree.
	 */
	public void addNode(final String str);

	/**
	 * Adds a node with content to the tree.
	 */
	public void addNode(final String nodePath, final InputStream input) throws IOException;

	/**
	 * Removes a node.
	 */
	public void removeNode(final String str);

	/**
	 * Retrieves a node.
	 */
	public TagNode getNode(final String str);

	/**
	 * Retrieves the child-nodes of a given node.
	 */
	public List<TagNode> getChildren(final String str);

	/**
	 * Checks whether the given path is writeable. This should be checked when a file is about to be
	 * uploaded.<br>
	 * This could be managed with write-restrictions in the <i>Domain.xml</i>, but this way we would
	 * have to adjust the settings in the <i>Domain.xml</i> if we would change the implementation of
	 * {@link webdav.tree.DirectoryTree} which would be much more time-consuming than implementing
	 * this method.
	 */
	public boolean isPathWriteable(final String path);
}