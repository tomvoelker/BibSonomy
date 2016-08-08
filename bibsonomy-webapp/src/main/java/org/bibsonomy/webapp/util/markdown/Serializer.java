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

import java.util.Map;

import org.pegdown.Printer;
import org.pegdown.ast.Node;
import org.pegdown.ast.Visitor;
import org.pegdown.plugins.ToHtmlSerializerPlugin;

/**
 * Serializes the AST to HTML
 *
 * @author Johannes Blum
 */
public class Serializer implements ToHtmlSerializerPlugin {
	
	/** map for the replacement of the variables. */
	private Map<String, String> replacements;
	
	/**
	 * Instantiates a new Serializer.
	 * @param replacements a map which maps a variable to the value it should be replaced with
	 */
	public Serializer(final Map<String, String> replacements) {
		this.replacements = replacements;
	}

	/* (non-Javadoc)
	 * @see org.pegdown.plugins.ToHtmlSerializerPlugin#visit(org.pegdown.ast.Node, org.pegdown.ast.Visitor, org.pegdown.Printer)
	 */
	@Override
	public boolean visit(Node node, Visitor visitor, Printer printer) {
		if (node instanceof VariableNode) {
			final String var = ((VariableNode) node).getName();
			printer.print(replaceVar(var));
			return true;
		}
		
		if (node instanceof ConditionalNode) {
			if (evaluateExpression(((ConditionalNode) node).getCondition())) {
				for (Node child : node.getChildren()) {
					child.accept(visitor);
				}
			}
			return true;
		}
		
		return false;
	}
	
	private boolean evaluateExpression(String exp) {
		final String[] fields = exp.split("\\s", 3);
		if (fields.length != 3)
			throw new RuntimeException("Invalid expression: " + exp);
		
		final String lhs = norm(fields[0]);
		final String rhs = norm(fields[2]);
		final String cmp = fields[1];
		
		switch(cmp) {
		case "==":
			return lhs.equals(rhs);
		case "<=":
			return compareVersions(lhs, rhs) <= 0;
		case "<":
			return compareVersions(lhs, rhs) < 0;
		case ">=":
			return compareVersions(lhs, rhs) >= 0;
		case ">":
			return compareVersions(lhs, rhs) > 0;
		default:
			throw new RuntimeException("Unknown comparator: " + cmp);
		}
	}

	/**
	 * @param lhs
	 * @return
	 */
	private String norm(String lhs) {
		if (lhs.startsWith("\"") && lhs.endsWith("\"")) {
			// remove ""
			return lhs.substring(1, lhs.length() - 1);
		}
		return this.replaceVar(lhs);
	}
	
	private String replaceVar(final String var) {
		if (!this.replacements.containsKey(var)) {
			throw new RuntimeException("Unknown variable: " + var);
		}
		return this.replacements.get(var);
	}
	
	/**
	 * Compares two version Strings
	 * @param v1 The first version String
	 * @param v2 The second version String
	 * @return the value 0 if the versions are equal; -1 if v1 < v2; and 1 if v1 > v2
	 */
	private static int compareVersions(String v1, String v2) {
		final String[] fields1 = v1.split("\\.");
		final String[] fields2 = v2.split("\\.");
		
		for (int i = 0; i < Math.max(fields1.length, fields2.length); i++) {
			final int f1 = (i < fields1.length) ? Integer.parseInt(fields1[i]) : 0;
			final int f2 = (i < fields2.length) ? Integer.parseInt(fields2[i]) : 0;
			
			if (f1 < f2) {
				return -1;
			}
			if (f2 > f1) {
				return 1;
			}
		}
		
		return 0;
	}

}
