/*
 * Created on 03.06.2007
 */
package org.bibsonomy.database.util.tag;

import org.bibsonomy.model.Tag;

public interface TagOperator {
	public void operate(final Tag left, final Tag right);
}
