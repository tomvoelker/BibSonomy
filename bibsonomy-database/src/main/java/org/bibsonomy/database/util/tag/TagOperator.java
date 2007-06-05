package org.bibsonomy.database.util.tag;

import org.bibsonomy.model.Tag;

/**
 * @author Jens Illig
 * @version $Id$
 */
public interface TagOperator {
	public void operate(final Tag left, final Tag right);
}