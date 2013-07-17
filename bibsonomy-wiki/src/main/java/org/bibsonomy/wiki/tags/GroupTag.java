package org.bibsonomy.wiki.tags;

public abstract class GroupTag extends AbstractTag {

	public GroupTag(final String name) {
		super(name);
	}

	@Override
	protected String renderSafe() {
		String errorString = this.messageSource.getMessage("cv.error.group", new Object[]{this.getName()}, this.locale);
		return this.requestedGroup != null  ? this.renderGroupTag() : errorString;
	}

	protected abstract String renderGroupTag();

}
