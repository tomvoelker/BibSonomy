package org.bibsonomy.webapp.util.markdown;
import java.util.Collections;
import java.util.List;

import org.pegdown.ast.AbstractNode;
import org.pegdown.ast.Node;
import org.pegdown.ast.Visitor;

/**
 * A Node for a variable.
 *
 * @author Johannes Blum
 */
public class VariableNode extends AbstractNode {
	
	private String name;

	/**
	 * @param name
	 */
	public VariableNode(String name) {
		super();
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see org.pegdown.ast.Node#accept(org.pegdown.ast.Visitor)
	 */
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	/* (non-Javadoc)
	 * @see org.parboiled.trees.GraphNode#getChildren()
	 */
	@Override
	public List<Node> getChildren() {
		return Collections.emptyList();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/* (non-Javadoc)
	 * @see org.pegdown.ast.AbstractNode#toString()
	 */
	@Override
	public String toString() {
		return "${"+ getName() + "}";
	}

}
