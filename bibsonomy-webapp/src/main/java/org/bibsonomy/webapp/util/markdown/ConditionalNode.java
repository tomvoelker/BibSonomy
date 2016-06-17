package org.bibsonomy.webapp.util.markdown;
import java.util.ArrayList;
import java.util.List;

import org.pegdown.ast.AbstractNode;
import org.pegdown.ast.Node;
import org.pegdown.ast.Visitor;

/**
 * A Node for a conditional expression. 
 *
 * @author Johannes Blum
 */
public class ConditionalNode extends AbstractNode {
	
	/** The condition */
	private String condition;
	
	private String source;
	
	/** The child nodes representing the content */
	private final List<Node> children = new ArrayList<Node>();
	

	/**
	 * @param condition The condition
	 * @param children The children
	 * @param source The source of the children
	 */
	public ConditionalNode(String condition, List<Node> children, String source) {
		this.children.addAll(children);
		this.condition = condition;
		this.source = source;
	}
	
	/**
	 * @return the condition
	 */
	public String getCondition() {
		return this.condition;
	}
	
	/**
	 * @return the source
	 */
	public String getSource() {
		return this.source;
	}

	/* (non-Javadoc)
	 * @see org.parboiled.trees.GraphNode#getChildren()
	 */
	@Override
	public List<Node> getChildren() {
		return children;
	}

	
	/* (non-Javadoc)
	 * @see org.pegdown.ast.Node#accept(org.pegdown.ast.Visitor)
	 */
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}
