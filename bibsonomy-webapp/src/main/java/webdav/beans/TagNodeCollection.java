package webdav.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import webdav.helper.PathHelper;
import webdav.tree.beans.TreeEntry;

/**
 * This class manages a tree of {@link webdav.beans.TagNode}s.
 * 
 * @author Christian Schenk
 */
public class TagNodeCollection {
	/** All nodes (for getNode) */
	private final Map<String, TagNode> nodes;
	/** The nodes with their respective children (for getChildren) */
	private final Map<String, List<TagNode>> tree;

	public TagNodeCollection() {
		this.nodes = new HashMap<String, TagNode>();
		this.tree = new HashMap<String, List<TagNode>>();
	}

	/**
	 * Adds a node to this collection.
	 * 
	 * @param path The path of the node (e.g. "/a/path/node-name")
	 */
	public void addNode(final String path) {
		this.addNode(path, null);
	}

	/**
	 * Adds a node with content ({@link webdav.tree.beans.TreeEntry}).
	 * 
	 * @param path The path of the node (e.g. "/a/path/node-name")
	 * @param content An instance of {@link webdav.tree.beans.FileEntry} or
	 *          {@link webdav.tree.beans.BibtexEntry}
	 */
	public void addNode(final String path, final TreeEntry content) {		
		final TagNode newNode = new TagNode(path);
		if (content != null) newNode.setContent(content);
		final String parentPath = PathHelper.getParent(path);
		this.nodes.put(path, newNode);

		if (!parentPath.equals(path)) {
			if (this.tree.containsKey(parentPath)) {
				this.tree.get(parentPath).add(newNode);
			} else {
				final List<TagNode> nodeList = new ArrayList<TagNode>();
				nodeList.add(newNode);
				this.tree.put(parentPath, nodeList);
				this.nodes.get(parentPath).setCollection();
			}
		}
	}

	/**
	 * Removes a node from this collection.
	 * 
	 * @param path The path of the node which should be removed
	 * @throws An {@link UnsupportedOperationException} if the node is a collection
	 */
	public void removeNode(final String path) {
		final TagNode node = this.nodes.get(path);
		if (node.isCollection()) throw new UnsupportedOperationException("Can't delete directory");
		this.nodes.remove(path);
		this.tree.get(PathHelper.getParent(path)).remove(node);
	}

	/**
	 * Gets a node from this collection.
	 * 
	 * @param path The path of the needed {@link webdav.beans.TagNode}
	 * @return The {@link webdav.beans.TagNode} or null if it doesn't exist
	 */
	public TagNode getNode(final String path) {
		return this.nodes.get(path);
	}

	/**
	 * Gets a {@link java.util.List} of {@link webdav.beans.TagNode}s, i.e. the childnodes of the
	 * given path.
	 * 
	 * @param path The path of the needed {@link webdav.beans.TagNode}
	 * @return A {@link java.util.List} of {@link webdav.beans.TagNode}s or null if there are no
	 *         children
	 */
	public List<TagNode> getChildren(final String path) {
		return this.tree.get(path);
	}

	/**
	 * Returns all available nodes in a {@link java.util.List}. The corresponding
	 * {@link webdav.beans.TagNode}s can be retrieved with
	 * {@link webdav.beans.TagNodeCollection#getNode(String)}.<br>
	 * This is a convenience method because you would have to implement a recursive method to iterate
	 * over all available nodes. This way it's much easier and probably faster.
	 * 
	 * @return A {@link java.util.List} of all available nodes (as strings of their
	 *         path-representation)
	 */
	public List<String> getAllNodes() {
		final List<String> rVal = new ArrayList<String>();
		for (final String str : this.nodes.keySet()) {
			rVal.add(str);
		}
		return rVal;
	}
}