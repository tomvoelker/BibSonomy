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
	@SuppressWarnings("unchecked")
	public Resource createResource(final Class<? extends Resource> clazz) {
		if (Bookmark.class.equals(clazz)) {
			return this.createBookmark();
		}
		
		if (clazz != null && BibTex.class.isAssignableFrom(clazz)) {
			return this.createPublication((Class<? extends BibTex>) clazz);
		}
		
		throw new UnsupportedResourceTypeException("resource " + clazz + "not supported");
	}
	
	/**
	 * @return creates a new bookmark
	 */
	public Bookmark createBookmark() {
		return new Bookmark();
	}
	
	/**
	 * @return creates a new publication
	 */
	public BibTex createPublication() {
		return new BibTex();
	}
	
	/**
	 * @param clazz
	 * @return a new Publication instance of the clazz
	 */
	public BibTex createPublication(final Class<? extends BibTex> clazz) {
		if (BibTex.class.equals(clazz)) {
			return this.createPublication();
		}
		
		if (GoldStandardPublication.class.equals(clazz)) {
			return this.createGoldStandardPublication();
		}
		
		throw new UnsupportedResourceTypeException("resource " + clazz + "not supported");
	}
	
	/**
	 * @return creates a new goldstandard publication
	 */
	public GoldStandardPublication createGoldStandardPublication() {
		return new GoldStandardPublication();
	}
}
