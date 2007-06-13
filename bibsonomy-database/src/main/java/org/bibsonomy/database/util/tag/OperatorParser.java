package org.bibsonomy.database.util.tag;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bibsonomy.model.Tag;
import org.bibsonomy.util.ExceptionUtils;

/**
 * @author Jens Illig
 * @version $Id$
 */
public class OperatorParser {

	private static final Logger log = Logger.getLogger(OperatorParser.class);
	private Map<String, TagOperator> operators;
	private Pattern pattern;

	public Tag parse(final String complexTag) {
		Tag first = null;
		Tag previous = null;
		TagOperator tagOp = null;
		String tagOpStr = null;
		final Matcher matcher = this.pattern.matcher(complexTag);
		int lastEnd = 0;
		while (matcher.find() == true) {
			final Tag found = new Tag();
			final String operator = matcher.group(1);
			found.setName(complexTag.substring(lastEnd, matcher.start()).trim());
			lastEnd = matcher.end();

			if (previous == null) {
				first = found;
			} else if ((found != null) && (tagOp != null)) {
				log.debug(previous.getName() + tagOpStr + found.getName());
				tagOp.operate(previous, found);
			}
			if (operator.length() > 0) {
				tagOpStr = operator;
				tagOp = findTagOperator(operator);
			}
			previous = found;
			if (operator == null) {
				break;
			}
		}
		return first;
	}

	private TagOperator findTagOperator(final String operator) {
		final TagOperator rVal = this.operators.get(operator);
		if (rVal == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "could not find " + TagOperator.class.getSimpleName() + " for operator '" + operator + "'");
		}
		return rVal;
	}

	public void setOperators(final Map<String, TagOperator> operators) {
		this.operators = operators;
		final StringBuilder sb = new StringBuilder("(");
		for (final String operator : operators.keySet()) {
			sb.append(operator).append('|');
		}
		sb.setCharAt(sb.length() - 1, '|');
		sb.append("$)");
		log.debug("compiling pattern '" + sb.toString() + "'");
		// TODO: insert operator dynamically
		this.pattern = Pattern.compile(sb.toString());
	}
}