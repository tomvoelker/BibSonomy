package org.bibsonomy.wiki.tags;

public abstract class SharedTag extends AbstractTag {

	public SharedTag(final String name) {
		super(name);
	}

	@Override
	protected String render() {
		// If the group is "null" then its a user (obviously)
		return this.requestedGroup != null  ? this.renderGroupTag() : this.renderUserTag();
	}

	protected abstract String renderGroupTag();
	protected abstract String renderUserTag();

}
