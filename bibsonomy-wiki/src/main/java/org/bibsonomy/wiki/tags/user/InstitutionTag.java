package org.bibsonomy.wiki.tags.user;


import org.bibsonomy.wiki.tags.AbstractTag;
/**
 * This is a simple institution-tag.
 * Usage: <institution />
 * @author Bernd
 * @version $Id$
 */
public class InstitutionTag extends AbstractTag {
	private static final String TAG_NAME = "institution";
	
	/**
	 * set the name of the tag
	 */
	public InstitutionTag() {
		super(TAG_NAME);
	}
	
	@Override
	protected String render() {
		return this.renderString(this.requestedUser.getInstitution());
	}

}
