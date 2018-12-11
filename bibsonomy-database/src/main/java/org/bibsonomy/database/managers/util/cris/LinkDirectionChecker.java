package org.bibsonomy.database.managers.util.cris;

import org.bibsonomy.model.cris.Linkable;

/**
 * interface to check if the link direction of a {@link org.bibsonomy.model.cris.CRISLink} is correct
 * @author dzo
 */
public interface LinkDirectionChecker {

	/**
	 * @param source the source of the crislink
	 * @param target the target of the crislink
	 * @return <code>true</code> true iff the source and target should be swapped
	 */
	boolean requiresSwap(final Linkable source, final Linkable target);
}
