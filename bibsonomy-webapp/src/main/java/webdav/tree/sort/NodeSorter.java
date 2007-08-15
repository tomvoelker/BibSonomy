package webdav.tree.sort;

import java.util.List;

/**
 * An interface to sort nodes. Implementing classes should derive from
 * {@link webdav.tree.sort.AbstractNodeSorter}, which takes care of setting
 * some variables.
 * 
 * @see webdav.tree.sort.AbstractNodeSorter
 * @author Christian Schenk
 */
public interface NodeSorter {

	/**
	 * Gets the sorted list of nodes.
	 * 
	 * @return A sorted list of nodes
	 */
	public List<String> getNodes();

	/**
	 * Gets the child nodes of a node.
	 * 
	 * @param node A node
	 * @return All child nodes of the given node
	 */
	public List<String> getChildren(final String node);
}