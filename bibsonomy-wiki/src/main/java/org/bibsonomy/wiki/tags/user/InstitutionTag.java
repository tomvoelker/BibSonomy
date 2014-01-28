package org.bibsonomy.wiki.tags.user;

import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.wiki.tags.UserTag;

/**
 * This is a simple institution-tag.
 * Usage: <institution />
 * @author Bernd
 */
public class InstitutionTag extends UserTag {
	private static final String TAG_NAME = "institution";
	
	/**
	 * set the name of the tag
	 */
	public InstitutionTag() {
		super(TAG_NAME);
	}
	
	@Override
	protected String renderUserTag() {
		final String institution = this.renderString(this.requestedUser.getInstitution());
		return ValidationUtils.present(institution) ? "<div id='institution'>" + institution + "</div>" : "";
	}

}
