package webdav.tree.sort.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import webdav.beans.TagNode;
import webdav.helper.PathHelper;
import webdav.tree.sort.AbstractNodeSorter;

/**
 * This class sorts the nodes in alphabetical order. Given the following set of
 * nodes:
 * <pre>
 *  cba
 *  bca
 *  abc
 *  Acb
 * </pre>
 * it creates three different top-level nodes (namely <i>a</i>, <i>b</i> and
 * <i>c</i>) with the respective nodes as child nodes.
 * 
 * @author Christian Schenk
 */
public class SimpleAlphabeticalSorter extends AbstractNodeSorter {

	/** The created top-level nodes */
	private final List<String> abcNodes;

	/**
	 * Creates the top-level nodes with their respective child nodes.
	 * 
	 * @param nodes A list with unique elements
	 */
	public SimpleAlphabeticalSorter(final List<String> nodes) {
		super(nodes);
		this.abcNodes = new ArrayList<String>();

		for (final String node : nodes) {
			final String firstLetter = node.substring(0, 1).toLowerCase();
			final String abcPath = PathHelper.buildPath("/", firstLetter);

			if (this.tagNodeCollection.getNode(abcPath) == null) {
				this.tagNodeCollection.addNode(abcPath);
				this.abcNodes.add(abcPath);
			}

			this.tagNodeCollection.addNode(PathHelper.buildPath(abcPath, node));
		}

		Collections.sort(this.abcNodes);
	}

	public List<String> getNodes() {
		return this.abcNodes;
	}

	public List<String> getChildren(final String node) {
		final List<String> rVal = new ArrayList<String>();
		final List<TagNode> nodes = this.tagNodeCollection.getChildren(node);
		for (final TagNode curNode : nodes) {
			rVal.add(curNode.getName());
		}
		return rVal;
	}
}