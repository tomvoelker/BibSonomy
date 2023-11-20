/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
