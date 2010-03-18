package org.bibsonomy.model.util;

import java.util.HashMap;
import java.util.Map;

import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;

/**
 * Static methods to handle Resources.
 * 
 * @author rja
 * @version $Id$
 */
public class ResourceUtils {

	
	private static final Map<String, Class<? extends Resource>> byStringMap = new HashMap<String, Class<? extends Resource>>();
	private static final Map<Class<? extends Resource>, String> toStringMap = new HashMap<Class<? extends Resource>, String>();
	static {
		byStringMap.put("BOOKMARK", Bookmark.class);
		byStringMap.put("BIBTEX", BibTex.class);
		byStringMap.put("ALL", Resource.class);
		for (final Map.Entry<String, Class<? extends Resource>> entry : byStringMap.entrySet()) {
			toStringMap.put(entry.getValue(), entry.getKey());
		}
	}

	/**
	 * @param resourceType
	 * @return resource
	 */
	public static Class<? extends Resource> getResource(final String resourceType) {
		if (resourceType == null) throw new UnsupportedResourceTypeException("ResourceType is null");
		Class<? extends Resource> rVal = byStringMap.get(resourceType);
		if (rVal == null) {
			rVal = byStringMap.get(resourceType.trim().toUpperCase());
			if (rVal == null) {
				throw new UnsupportedResourceTypeException();
			}
		}
		return rVal;
	}
	
	/**
	 * Returns an instance of a {@link Resource} with the given type.
	 * 
	 * @param resourceType
	 * @return An instance of a resource with the given type.
	 * 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static Resource getInstance(final String resourceType) throws InstantiationException, IllegalAccessException {
		return getResource(resourceType).newInstance();
	}

	/**
	 * @param clazz
	 * @return string
	 */
	public static String toString(final Class<? extends Resource> clazz) {
		final String rVal = toStringMap.get(clazz);
		if (rVal == null) {
			throw new UnsupportedResourceTypeException();
		}
		return rVal;
	}
}
