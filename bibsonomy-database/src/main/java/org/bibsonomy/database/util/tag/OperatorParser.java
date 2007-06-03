/*
 * Created on 03.06.2007
 */
package org.bibsonomy.database.util.tag;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bibsonomy.model.Tag;
import org.bibsonomy.util.ExceptionUtils;

public class OperatorParser {
	private Pattern pattern;
	private static final Logger log = Logger.getLogger(OperatorParser.class);
	private Map<String, TagOperator> operators;
	
	public Tag parse(String complexTag) {
		Tag first = null;
		Tag previous = null;
		TagOperator tagOp = null;
		String tagOpStr = null;
		final Matcher m = pattern.matcher(complexTag);
		int lastEnd = 0;
		while (m.find() == true) {
			final Tag found = new Tag();
			final String op = m.group(1);
			found.setName(complexTag.substring(lastEnd, m.start()).trim());
			lastEnd = m.end();
			
			if (previous == null) {
				first = found;
			} else if ((found != null) && (tagOp != null)) {
				log.debug(previous.getName() + tagOpStr + found.getName());
				tagOp.operate(previous, found);
			}
			if (op.length() > 0) {
				tagOpStr = op;
				tagOp = findTagOperator(op);
			}
			previous = found;
			if (op == null) {
				break;
			}
		}
		return first;
	}

	private TagOperator findTagOperator(final String op) {
		final TagOperator rVal = operators.get(op);
		if (rVal == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "could not find " + TagOperator.class.getSimpleName() + " for operator '" + op + "'");
		}
		return rVal;
	}

	public void setOperators(Map<String, TagOperator> operators) {
		this.operators = operators;
		final StringBuilder sb = new StringBuilder("(");
		for (String op : operators.keySet()) {
			sb.append(op).append('|');
		}
		sb.setCharAt(sb.length()-1, '|');
		sb.append("$)");
		log.debug("compiling pattern '" + sb.toString() + "'");
		this.pattern = Pattern.compile(sb.toString()); // TODO: operatoren dynamisch einfügen
	}
}
