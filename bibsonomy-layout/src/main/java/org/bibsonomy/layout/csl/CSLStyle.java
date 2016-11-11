package org.bibsonomy.layout.csl;

/**
 * 
 * @author jp
 */
public class CSLStyle extends org.bibsonomy.model.Layout {
	
	private String id;
	private String content;
	private String aliasedTo;
	private boolean userLayout;

	/**
	 * @param id 
	 * @param displayName 
	 * @param content 
	 */
	public CSLStyle(String id, String displayName, String content) {
		super(id);
		this.id = id;
		this.displayName = displayName;
		this.content = content;
	}
	
	/**
	 * @param name
	 * @param id
	 * @param displayName
	 * @param content
	 * @param aliasedTo
	 */
	public CSLStyle(String id, String displayName, String content, String aliasedTo) {
		this(id, displayName, content);
		this.setAliasedTo(aliasedTo);
	}

	//TODO eigtl nur vorrübergehend.. vielleicht
	/**
	 * @param id 
	 */
	public CSLStyle(String id) {
		super(id);
		this.id = id;
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

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.Layout#hasEmbeddedLayout()
	 */
	@Override
	public boolean hasEmbeddedLayout() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * @return the userLayout
	 */
	public boolean isUserLayout() {
		return this.userLayout;
	}

	/**
	 * @param userLayout the userLayout to set
	 */
	public void setUserLayout(boolean userLayout) {
		this.userLayout = userLayout;
	}

	/**
	 * @return the aliasedTo
	 */
	public String getAliasedTo() {
		return this.aliasedTo;
	}

	/**
	 * @param aliasedTo the aliasedTo to set
	 */
	public void setAliasedTo(String aliasedTo) {
		this.aliasedTo = aliasedTo;
	}
}