package org.bibsonomy.model.extra;

import java.util.Date;

/**
 * @version $Id$
 */
public class ExtendedFields {

	private Date created;
	private Date lastModified;
	private int groupId;
	private String key;
	private String value;
	private String description;
	private int order;

	/**
	 * @return created
	 */
	public Date getCreated() {
		return this.created;
	}

	/**
	 * @param created
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @return lastModified
	 */
	public Date getLastModified() {
		return this.lastModified;
	}

	/**
	 * @param lastModified
	 */
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	/**
	 * @return groupId
	 */
	public int getGroupId() {
		return this.groupId;
	}

	/**
	 * @param groupId
	 */
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	/**
	 * @return key
	 */
	public String getKey() {
		return this.key;
	}

	/**
	 * @param key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return order
	 */
	public int getOrder() {
		return this.order;
	}

	/**
	 * @param order
	 */
	public void setOrder(int order) {
		this.order = order;
	}
}