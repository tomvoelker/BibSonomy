package org.bibsonomy.model.util.cris;

import java.util.List;
import java.util.stream.Collectors;

import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.model.cris.Linkable;

/**
 * util methods for cris links
 *
 * @author dzo
 */
public class CRISLinkUtils {

	private CRISLinkUtils() {
		// noop
	}

	/**
	 * filters the link cris link list by the specified type
	 * @param linkableClass
	 * @return
	 */
	public static List<CRISLink> filterCRISLinksBySourceType(final Class<? extends Linkable> linkableClass, final List<CRISLink> crisLinks) {
		return crisLinks.stream().filter(link -> linkableClass.isAssignableFrom(link.getSource().getClass())).collect(Collectors.toList());
	}
}
