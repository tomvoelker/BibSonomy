package org.bibsonomy.webapp.util.markdown;
import java.util.HashMap;

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
	
	/** Hashmap for the replacement of the variables. */
	HashMap<String, String> replacements;
	
	/**
	 * Instantiates a new Serializer.
	 * @param replacements a Hashmap which maps a variable to the value it should be replaced with
	 */
	public Serializer(HashMap<String, String> replacements) {
		super();
		this.replacements = replacements;
	}

	/* (non-Javadoc)
	 * @see org.pegdown.plugins.ToHtmlSerializerPlugin#visit(org.pegdown.ast.Node, org.pegdown.ast.Visitor, org.pegdown.Printer)
	 */
	@Override
	public boolean visit(Node node, Visitor visitor, Printer printer) {
		if (node instanceof VariableNode) {
			String var = ((VariableNode) node).getName();
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
		String[] fields = exp.split("\\s", 3);
		if (fields.length != 3)
			throw new RuntimeException("Invalid expression: " + exp);
		
		String lhs = fields[0];
		if (lhs.startsWith("\"") && lhs.endsWith("\""))
			lhs = lhs.substring(1, lhs.length()-1);
		else
			lhs = replaceVar(lhs);
		
		String rhs = fields[2];
		if (rhs.startsWith("\"") && rhs.endsWith("\""))
			rhs = rhs.substring(1, rhs.length()-1);
		else
			rhs = replaceVar(rhs);
		
		String cmp = fields[1];
		
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
	
	private String replaceVar(String var) {
		String r = replacements.get(var);
		if (r == null)
			throw new RuntimeException("Unknown variable: " + var);
		return r;
	}
	
	/**
	 * Compares two version Strings
	 * @param v1 The first version String
	 * @param v2 The second version String
	 * @return the value 0 if the versions are equal; -1 if v1 < v2; and 1 if v1 > v2
	 */
	private static int compareVersions(String v1, String v2) {		
		String[] fields1 = v1.split("\\.");
		String[] fields2 = v2.split("\\.");
		
		for (int i = 0; i < Math.max(fields1.length, fields2.length); i++) {
			int f1 = (i < fields1.length) ? Integer.parseInt(fields1[i]) : 0;
			int f2 = (i < fields2.length) ? Integer.parseInt(fields2[i]) : 0;
			
			if (f1 < f2)
				return -1;
			if (f2 > f1)
				return 1;
		}
		
		return 0;
	}

}
