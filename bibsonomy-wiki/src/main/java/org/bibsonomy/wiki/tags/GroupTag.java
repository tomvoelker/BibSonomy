package org.bibsonomy.wiki.tags;

public abstract class GroupTag extends AbstractTag {

	public GroupTag(final String name) {
		super(name);
	}

	@Override
	protected String render() {
		return this.requestedGroup != null  ? this.renderGroupTag() : "The tag \"" +this.getName() +"\" is only available for groups.";
	}

	protected abstract String renderGroupTag();

}
