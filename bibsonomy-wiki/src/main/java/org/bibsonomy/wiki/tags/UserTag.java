package org.bibsonomy.wiki.tags;

public abstract class UserTag extends AbstractTag {

	public UserTag(final String name) {
		super(name);
	}

	@Override
	protected String renderSafe() {
		// Das ueberprueft doch nur, ob this.requestedUser != null ist. Was ist aber, wenn ich eigentlich eine Gruppe haben moechte?
		
		return this.requestedUser != null  ? this.renderUserTag() : "The tag \"" +this.getName() +"\" is only available for users.";
	}

	protected abstract String renderUserTag();

}
