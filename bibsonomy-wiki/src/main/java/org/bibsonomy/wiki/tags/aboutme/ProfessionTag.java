package org.bibsonomy.wiki.tags.aboutme;

import static org.bibsonomy.util.ValidationUtils.present;
import info.bliki.htmlcleaner.Utils;

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
		final StringBuilder renderedHTML = new StringBuilder();
		final String profession = this.requestedUser.getProfession();
		if (present(profession)) {
			renderedHTML.append(Utils.escapeXmlChars(requestedUser.getProfession()));
		}
		
		return renderedHTML;
	}

}
