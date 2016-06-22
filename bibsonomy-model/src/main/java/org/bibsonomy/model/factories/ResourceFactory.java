/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.model.factories;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandard;
import org.bibsonomy.model.GoldStandardBookmark;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Resource;

/**
 * @author dzo
 */
public class ResourceFactory {
	
	/**
	 * the string identifying the {@link Resource} class
	 */
	public static final String RESOURCE_CLASS_NAME = "all";

	/**
	 * the string identifying the {@link BibTex} class
	 */
	public static final String PUBLICATION_CLASS_NAME = "publication";

	/**
	 * the string identifying the {@link Bookmark} class
	 */
	public static final String BOOKMARK_CLASS_NAME = "bookmark";
	
	/**
	 * the string identifying the {@link GoldStandardBookmark}
	 */
	public static final String GOLDSTANDARD_BOOKMARK_CLASS_NAME = "goldstandardbookmark";

	/**
	 * the string identifying the {@link GoldStandardPublication}
	 */
	public static final String GOLDSTANDARD_PUBLICATION_CLASS_NAME = "goldstandardpublication";

	/**
	 * all known resource classes
	 */
	private static final Map<String, Class<? extends Resource>> RESOURCE_CLASSES_BY_NAME = new HashMap<>();
	
	private static final Map<Class<? extends Resource>, String> RESOURCE_CLASS_NAMES = new HashMap<>();
	
	private static final Map<Class<? extends Resource>, Class<? extends Resource>> RESOURCE_CLASSES_SUPERIOR_MAP = new HashMap<>();
	
	static {
		RESOURCE_CLASSES_BY_NAME.put(BOOKMARK_CLASS_NAME, Bookmark.class);
		RESOURCE_CLASSES_BY_NAME.put(PUBLICATION_CLASS_NAME, BibTex.class);
		RESOURCE_CLASSES_BY_NAME.put(GOLDSTANDARD_PUBLICATION_CLASS_NAME, GoldStandardPublication.class);
		RESOURCE_CLASSES_BY_NAME.put(GOLDSTANDARD_BOOKMARK_CLASS_NAME, GoldStandardBookmark.class);
		RESOURCE_CLASSES_BY_NAME.put(RESOURCE_CLASS_NAME, Resource.class);
		
		RESOURCE_CLASSES_SUPERIOR_MAP.put(Bookmark.class, Bookmark.class);
		RESOURCE_CLASSES_SUPERIOR_MAP.put(BibTex.class, BibTex.class);
		RESOURCE_CLASSES_SUPERIOR_MAP.put(GoldStandardBookmark.class, Bookmark.class);
		RESOURCE_CLASSES_SUPERIOR_MAP.put(GoldStandardPublication.class, BibTex.class);
		
		for (final Entry<String, Class<? extends Resource>> entry : RESOURCE_CLASSES_BY_NAME.entrySet()) {
			RESOURCE_CLASS_NAMES.put(entry.getValue(), entry.getKey());
		}
		
		// XXX: for backward compatibility; note: not added to RESOURCE_CLASS_NAMES
		RESOURCE_CLASSES_BY_NAME.put("bibtex", BibTex.class);
	}
	
	/** 
	 * @param resourceName
	 * @return the class of the resource class by a name, e.g. "bookmark"
	 * returns the {@link Bookmark} class
	 */
	public static final Class<? extends Resource> getResourceClass(String resourceName) {
		if (!present(resourceName)) throw new UnsupportedResourceTypeException("ResourceType is null");
		resourceName = resourceName.toLowerCase();
		return RESOURCE_CLASSES_BY_NAME.get(resourceName);
	}
	
	/**
	 * @param resourceClass
	 * @return the name of the resource class (all lowercase)
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
	 * @return a new instance of the class
	 */
	@SuppressWarnings("unchecked")
	public <R extends Resource> R createResource(final Class<R> clazz) {
		if (clazz != null && Bookmark.class.isAssignableFrom(clazz)) {
			return (R) this.createBookmark((Class<? extends Bookmark>) clazz);
		}
		
		if (clazz != null && BibTex.class.isAssignableFrom(clazz)) {
			return (R) this.createPublication((Class<? extends BibTex>) clazz);
		}
		
		throw new UnsupportedResourceTypeException("resource " + clazz + " not supported");
	}
	
	/**
	 * 
	 * @param clazz
	 * @return a new instance of the class
	 */
	public Bookmark createBookmark(final Class<? extends Bookmark> clazz) {
		if (Bookmark.class.equals(clazz)) {
			return this.createBookmark();
		}
		
		if (GoldStandardBookmark.class.equals(clazz)) {
			return this.createGoldStandardBookmark();
		}
		
		throw new UnsupportedResourceTypeException("resource " + clazz + " not supported");
	}
	
	/**
	 * 
	 * @return a new {@link GoldStandardBookmark}
	 */
	public GoldStandardBookmark createGoldStandardBookmark() {
		return new GoldStandardBookmark();
	}

	/**
	 * @return creates a new {@link Bookmark}
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

	/**
	 * @param resource
	 * @return <code>true</code> iff the resource represents a community post
	 */
	public static boolean isCommunityResource(final Resource resource) {
		return resource instanceof GoldStandard<?>;
	}

	/**
	 * @param resource
	 * @return the superior resource class
	 */
	public static Class<? extends Resource> findSuperiorResourceClass(final Resource resource) {
		return RESOURCE_CLASSES_SUPERIOR_MAP.get(resource.getClass());
	}
}
