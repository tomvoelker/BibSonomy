package org.bibsonomy.layout.csl;

/**
 * 
 * @author jp
 */
public class CSLStyle {
	private String id;
	private String displayName;
	private String content;

	/**
	 * @param id 
	 * @param displayName 
	 * @param content 
	 */
	public CSLStyle(String id, String displayName, String content) {
		this.id = id;
		this.displayName = displayName;
		this.content = content;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * @param displayName
	 *            the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
}