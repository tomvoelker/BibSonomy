/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
package org.bibsonomy.model.util;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.util.UrlUtils;

/**
 * Static methods to handle Resources.
 * 
 * @author rja
 */
public class ResourceUtils {
	
	private static final Map<String, Class<? extends Resource>> byStringMap = new HashMap<String, Class<? extends Resource>>();
	private static final Map<Class<? extends Resource>, String> toStringMap = new HashMap<Class<? extends Resource>, String>();
	static {
		byStringMap.put("BOOKMARK", Bookmark.class);
		byStringMap.put("BIBTEX", BibTex.class);
		byStringMap.put("ALL", Resource.class);
		// TODO: shouldn't there be goldstandard-Resources too? - I dont want to break stuff relying on this
		for (final Map.Entry<String, Class<? extends Resource>> entry : byStringMap.entrySet()) {
			toStringMap.put(entry.getValue(), entry.getKey());
		}
	}

	/**
	 * @param clazz
	 * @return string
	 * @deprecated  please use {@link ResourceFactory#getResourceName(Class)}
	 */
	@Deprecated
	public static String toString(final Class<? extends Resource> clazz) {
		final String rVal = toStringMap.get(clazz);
		if (rVal == null) {
			throw new UnsupportedResourceTypeException();
		}
		return rVal;
	}
	
	/**
	 * 
	 * @param requiredType
	 * @return list with required resource types.
	 */
	@SuppressWarnings("unchecked")
	public static Class<? extends Resource>[] getResourceTypesByClass(final Class<? extends Resource> requiredType) {
		if (Resource.class.equals(requiredType)) {
			return new Class[]{Bookmark.class, BibTex.class};
		}
		return new Class[]{requiredType};
	}
	
	@SuppressWarnings("unchecked")
	@Deprecated // use ResourceFactory instead
	public static Class<? extends Resource> getResourceClassBySimpleName(String resourceName) {
		if (resourceName != null) {
			Class<? extends Resource> rVal = byStringMap.get(resourceName.toUpperCase());
			if (rVal != null) {
				return rVal;
			}
			try {
				Class<?> cls = Class.forName(BibTex.class.getPackage().getName() + "." + resourceName);
				if (Resource.class.isAssignableFrom(cls)) {
					return (Class<? extends Resource>) cls;
				}
			} catch (ClassNotFoundException e) {
			}
		}
		throw new NoSuchElementException(resourceName);
	}
	
	
	/**
	 * Extracts a URL from the post. Easy for bookmarks, a little more difficult
	 * for publications.
	 * 
	 * @param post
	 * @return the extracted URL
	 */
	public static String getLinkAddress(final Post<? extends Resource> post) {
		final Resource resource = post.getResource();
		if (resource instanceof Bookmark) {
			return ((Bookmark) resource).getUrl();
		} else if (resource instanceof BibTex) {
			final BibTex bibtex = (BibTex) resource;

			final String url = bibtex.getUrl();
			if (present(url)) return UrlUtils.cleanBibTeXUrl(url);
			bibtex.serializeMiscFields();

			final String ee = bibtex.getMiscField("ee");
			if (present(ee)) return UrlUtils.cleanBibTeXUrl(ee);
		}
		return null;
	}
}
