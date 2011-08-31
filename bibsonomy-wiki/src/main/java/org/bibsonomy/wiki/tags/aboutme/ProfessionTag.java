package org.bibsonomy.wiki.tags.aboutme;

import org.bibsonomy.wiki.tags.AbstractTag;

/**
 * This is a simple profession-tag.
 * Usage: <profession />
 * @author Bernd
 *
 */
public class ProfessionTag extends AbstractTag{
	private static final String TAG_NAME = "profession";
	
	/**
	 * set the name of the tag
	 */
	public ProfessionTag() {
		super(TAG_NAME);
	}
	
	@Override
	protected StringBuilder render() {
		return this.renderString(this.requestedUser.getProfession());
	}

}
