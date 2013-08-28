package org.bibsonomy.wiki.tags;

/**
 * abstract tag for user tags
 * 
 * @author tni
 */
public abstract class UserTag extends AbstractTag {

	/**
	 * default constructor
	 * @param name
	 */
	public UserTag(final String name) {
		super(name);
	}

	@Override
	protected String renderSafe() {
		String errorString = this.messageSource.getMessage("cv.error.user", new Object[]{this.getName()}, this.locale);
		return this.requestedUser != null  ? this.renderUserTag() : errorString;
	}
	
	/**
	 * internal render method
	 * @return the string to append to the output
	 */
	protected abstract String renderUserTag();

}
