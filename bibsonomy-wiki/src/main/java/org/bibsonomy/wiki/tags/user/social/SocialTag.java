package org.bibsonomy.wiki.tags.user.social;

import org.bibsonomy.wiki.tags.AbstractTag;

/**
 * Defines a tag for a social inclusion stuff
 * like from linkedin, facebook, twitter...?
 * TODO: implement social tags
 * 
 * @author niebler
 */
public abstract class SocialTag extends AbstractTag {

	/**
	 * default constructor
	 */
	public SocialTag() {
		super("SOCIAL");
	}
	
	@Override
	protected String renderSafe() {
		// TODO Auto-generated method stub
		return null;
	}

}
