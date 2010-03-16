/**
 *  
 *  BibSonomy-Model - Java- and JAXB-Model.
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;

/**
 * Everything, which can be tagged, is derived from this class.
 * 
 * What may be accurate for representing the type of a Resource?
 * -> naturally its class! (which is lighter, more intuitive and flexible
 *    (eg reflective instantiation) and most notably more precise in
 *    type-safe generic methods than an enum).
 * 
 * @version $Id$
 */
public abstract class Resource implements Serializable{

	/**
	 * How many posts with this resource exist.
	 */
	private int count;

	/**
	 * The inter user hash is less specific than the {@link #intraHash}.
	 */
	private String interHash;

	/**
	 * The intra user hash is relativily strict and takes many fiels of this
	 * resource into account.
	 */
	private String intraHash;

	/**
	 * These are the {@link Post}s this resource belongs to.
	 */
	private List<Post<? extends Resource>> posts;

	/**
	 * Each resource has a title. 
	 * 
	 * TODO: It is given by the user and thus might better fit into the post.
	 */
	private String title;
	
	/**
	 * FIXME: This method does not belong to the model!!!! It would be fine to
	 * make it a static method of this class and use the resource (to
	 * recalculate hashes for) as parameter.
	 */
	public abstract void recalculateHashes();

	/**
	 * @return interHash
	 */
	public String getInterHash() {
		return this.interHash;
	}

	/**
	 * @param interHash
	 */
	public void setInterHash(String interHash) {
		this.interHash = interHash;
	}

	/**
	 * @return intraHash
	 */
	public String getIntraHash() {
		return this.intraHash;
	}

	/**
	 * @param intraHash
	 */
	public void setIntraHash(String intraHash) {
		this.intraHash = intraHash;
	}

	/**
	 * @return posts
	 */
	public List<Post<? extends Resource>> getPosts() {
		if (this.posts == null) {
			this.posts = new LinkedList<Post<? extends Resource>>();
		}
		return this.posts;
	}

	/**
	 * @param posts
	 */
	public void setPosts(List<Post<? extends Resource>> posts) {
		this.posts = posts;
	}

	/**
	 * @return count
	 */
	public int getCount() {
		return this.count;
	}

	/**
	 * @param count
	 */
	public void setCount(int count) {
		this.count = count;
	}

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
	
	@Override
	public String toString() {
		return "<" + intraHash + "/" + interHash + ">";
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}