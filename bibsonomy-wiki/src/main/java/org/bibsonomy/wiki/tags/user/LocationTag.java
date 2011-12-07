package org.bibsonomy.wiki.tags.user;

import org.bibsonomy.wiki.tags.UserTag;
/**
 * This is a simple location-tag.
 * Usage: <location />
 * @author Bernd
 *
 */
public class LocationTag extends UserTag{
	private static final String TAG_NAME = "location";
	
	/**
	 * set name of the tag
	 */
	public LocationTag() {
		super(TAG_NAME);
		System.out.println("constructor location test");
	}
	
	@Override
	protected String renderUserTag() {
		return this.renderString(this.requestedUser.getPlace());
	}
}
