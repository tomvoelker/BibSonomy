package org.bibsonomy.wiki.tags;

public abstract class UserTag extends AbstractTag {

	public UserTag(final String name) {
		super(name);
	}

	@Override
	protected String render() {
		return this.requestedUser != null  ? this.renderUserTag() : "The tag \"" +this.getName() +"\" is only available for users.";
	}

	protected abstract String renderUserTag();

}
