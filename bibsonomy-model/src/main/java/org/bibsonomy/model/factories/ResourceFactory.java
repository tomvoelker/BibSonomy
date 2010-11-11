package org.bibsonomy.model.factories;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
	 * all known resource classes
	 */
	private static final Map<String, Class<? extends Resource>> RESOURCE_CLASSES_BY_NAME = new HashMap<String, Class<? extends Resource>>();
	
	private static final Map<Class<? extends Resource>, String> RESOURCE_CLASS_NAMES = new HashMap<Class<? extends Resource>, String>();
	
	static {
		RESOURCE_CLASSES_BY_NAME.put("bookmark", Bookmark.class);
		RESOURCE_CLASSES_BY_NAME.put("publication", BibTex.class);
		RESOURCE_CLASSES_BY_NAME.put("goldStandardPublication", GoldStandardPublication.class);
		
		for (final Entry<String, Class<? extends Resource>> entry : RESOURCE_CLASSES_BY_NAME.entrySet()) {
			RESOURCE_CLASS_NAMES.put(entry.getValue(), entry.getKey());
		}
		
		// XXX: for backward compatibility; note: not added to RESOURCE_CLASS_NAMES
		RESOURCE_CLASSES_BY_NAME.put("bibtex", BibTex.class); 
	}
	
	/** 
	 * @param resourceName
	 * @return the class of the resource class by a name, e.g. "bookmark" returns
	 */
	public static final Class<? extends Resource> getResourceClass(final String resourceName) {
		return RESOURCE_CLASSES_BY_NAME.get(resourceName);
	}
	
	/**
	 * @param resourceClass
	 * @return the name of the resource class
	 */
	public static final String getResourceName(final Class<? extends Resource> resourceClass) {
		return RESOURCE_CLASS_NAMES.get(resourceClass);
	}
	
	/**
	 * all known resources
	 * @return a set containing the classes of all known resources of this factory
	 */
	public static Set<? extends Class<? extends Resource>> getAllResourceClasses() {
		return Collections.unmodifiableSet(RESOURCE_CLASS_NAMES.keySet());
	}
	
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
		
		throw new UnsupportedResourceTypeException("resource " + clazz + " not supported");
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
		
		throw new UnsupportedResourceTypeException("resource " + clazz + " not supported");
	}
	
	/**
	 * @return creates a new goldstandard publication
	 */
	public GoldStandardPublication createGoldStandardPublication() {
		return new GoldStandardPublication();
	}
}
