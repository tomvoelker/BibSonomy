package org.bibsonomy.database.managers.util.cris;

import org.bibsonomy.model.cris.Linkable;

/**
 * @dzo
 */
public interface LinkDirectionChecker {

	public boolean requiresSwap(final Linkable source, final Linkable target);
}
