package org.bibsonomy.model.factories;

import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.model.BibTex;

/**
 * @author dzo
 * @version $Id$
 */
public class PublicationFactory extends ResourceFactory {
	
	/**
	 * @param clazz
	 * @return a new Publication instance of the clazz
	 */
	public BibTex createPublication(final Class<? extends BibTex> clazz) {
		if (BibTex.class.isAssignableFrom(clazz)) {
			return (BibTex) this.createResource(clazz);
		}
		
		throw new UnsupportedResourceTypeException("resource not supported");
	}
}
