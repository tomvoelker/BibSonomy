package org.bibsonomy.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;

/**
 * Everything, which can be tagged in BibSonomy, is derived from this class.
 * 
 * What may be accurate for representing the type of a Resource?
 * -> naturally its class! (which is lighter, more intuitive and flexible
 *    (eg reflective instantiation) and most notably more precise in
 *    type-safe generic methods than an enum).
 */
public abstract class Resource {
	
	/** 
	 * An Id for this resource; by default ConstantID.IDS_UNDEFINED_CONTENT_ID
	 * FIXME why don't we name it id?!? 
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
	 * FIXME: This method does not belong to the model!!!! It would be fine to
	 * make it a static method of this class and use the resource (to
	 * recalculate hashes for) as parameter.
	 */
	public abstract void recalculateHashes();
	
	public String getInterHash() {
		return this.interHash;
	}

	public void setInterHash(String interHash) {
		this.interHash = interHash;
	}

	public String getIntraHash() {
		return this.intraHash;
	}

	public void setIntraHash(String intraHash) {
		this.intraHash = intraHash;
	}

	public List<Post<? extends Resource>> getPosts() {
		if (this.posts == null) {
			this.posts = new LinkedList<Post<? extends Resource>>();
		}
		return this.posts;
	}

	public void setPosts(List<Post<? extends Resource>> posts) {
		this.posts = posts;
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	
	private static final Map<String,Class<? extends Resource>> byStringMap = new HashMap<String, Class<? extends Resource>>();
	private static final Map<Class<? extends Resource>,String> toStringMap = new HashMap<Class<? extends Resource>,String>();
	static {
		byStringMap.put("BOOKMARK", Bookmark.class);
		byStringMap.put("BIBTEX", BibTex.class);
		byStringMap.put("ALL", Resource.class);
		for (Map.Entry<String, Class<? extends Resource>> entry : byStringMap.entrySet()) {
			toStringMap.put(entry.getValue(), entry.getKey());
		}
	}
	
	public static Class<? extends Resource> getResourceType( final String resourceType ) {
		if( resourceType == null ) throw new InternServerException( "ResourceType is null" );
		Class<? extends Resource> rVal = byStringMap.get(resourceType);
		if (rVal == null) {
			rVal = byStringMap.get(resourceType.trim().toUpperCase());
			if (rVal == null) {
				throw new UnsupportedResourceTypeException( resourceType );
			}
		}
		return rVal;
	}
	
	public static String toString(Class<? extends Resource> clazz) {
		final String rVal = toStringMap.get(clazz);
		if (rVal == null) {
			throw new UnsupportedResourceTypeException( clazz.toString() );
		}
		return rVal;
	}
}