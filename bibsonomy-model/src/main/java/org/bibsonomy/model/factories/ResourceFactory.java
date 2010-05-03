package org.bibsonomy.model.factories;

import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Resource;

/**
 * @author dzo
 * @version $Id$
 */
public class ResourceFactory {

	/**
	 * @param clazz
	 * @return a new instance of the clazz
	 */
	public Resource createResource(final Class<? extends Resource> clazz) {
		if (Bookmark.class.equals(clazz)) {
			return new Bookmark();
		}
		
		if (GoldStandardPublication.class.equals(clazz)) {
			return new GoldStandardPublication();
		}
		
		if (BibTex.class.equals(clazz)) {
			return new BibTex();
		}
		
		throw new UnsupportedResourceTypeException(clazz != null ? clazz.getSimpleName() + "isn't supported by this factory" : "clazz was null");
	}
}
