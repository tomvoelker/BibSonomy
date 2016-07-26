/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.util.markdown;

import java.util.Collections;
import java.util.List;

import org.pegdown.ast.AbstractNode;
import org.pegdown.ast.Node;
import org.pegdown.ast.Visitor;

/**
 * A {@link Node} for a variable.
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
