package org.bibsonomy.wiki.tags;

/**
 * abstract class for group tags
 * @author tni
 */
public abstract class GroupTag extends AbstractTag {

	/**
	 * default constructor
	 * @param name
	 */
	public GroupTag(final String name) {
		super(name);
	}

	@Override
	protected String renderSafe() {
		String errorString = this.messageSource.getMessage("cv.error.group", new Object[]{this.getName()}, this.locale);
		return this.requestedGroup != null  ? this.renderGroupTag() : errorString;
	}

	/**
	 * internal render method
	 * @return the text to append
	 */
	protected abstract String renderGroupTag();

}
