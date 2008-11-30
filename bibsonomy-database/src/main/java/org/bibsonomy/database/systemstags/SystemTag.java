package org.bibsonomy.database.systemstags;

import org.bibsonomy.model.Post;

/**
 * @author Andreas Koch
 * @version $Id$ 
 */
public abstract class SystemTag {
	private final String name;
	private String value;

	/**
	 * used if no start value for the system tag is given
	 * 
	 * @param name
	 *            of the system tag
	 */
	public SystemTag(String name) {
		this(name, null);
	}

	/**
	 * 
	 * @param name
	 *            of the system tag
	 * @param value
	 *            of the system tag
	 */
	public SystemTag(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * action to perform before the update/delete action
	 * 
	 * @param post
	 */
	public abstract void performBefore(Post post);

	/**
	 * action to perform after the update/delete action
	 * 
	 * @param post
	 */
	public abstract void performAfter(Post post);

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
