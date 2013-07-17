package org.bibsonomy.wiki.tags;

public abstract class UserTag extends AbstractTag {

	public UserTag(final String name) {
		super(name);
	}

	@Override
	protected String renderSafe() {
		String errorString = this.messageSource.getMessage("cv.error.user", new Object[]{this.getName()}, this.locale);
		return this.requestedUser != null  ? this.renderUserTag() : errorString;
	}

	protected abstract String renderUserTag();

}
