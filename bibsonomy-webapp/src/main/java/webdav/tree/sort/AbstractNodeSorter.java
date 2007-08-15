package webdav.tree.sort;

import java.util.List;

import webdav.beans.TagNodeCollection;
import webdav.tree.sort.NodeSorter;

/**
 * This is the superclass for implementations of the NodeSorter-interface. It
 * sets up some variables, which are common to all implementations.
 * 
 * @see webdav.tree.sort.NodeSorter
 * @author Christian Schenk
 */
public abstract class AbstractNodeSorter implements NodeSorter {

	/** Will contain the sorted nodes */
	protected final TagNodeCollection tagNodeCollection;
	/** The nodes we want to sort */
	protected final List<String> nodes;

	/**
	 * Adds a '/'-node to the corresponding collection to avoid this in
	 * implementations.
	 */
	public AbstractNodeSorter(final List<String> nodes) {
		this.tagNodeCollection = new TagNodeCollection();
		this.tagNodeCollection.addNode("/");
		this.nodes = nodes;
	}
}