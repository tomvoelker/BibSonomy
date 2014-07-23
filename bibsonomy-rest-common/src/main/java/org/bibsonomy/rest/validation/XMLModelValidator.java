package org.bibsonomy.rest.validation;

import org.bibsonomy.rest.renderer.xml.BibtexType;
import org.bibsonomy.rest.renderer.xml.BookmarkType;
import org.bibsonomy.rest.renderer.xml.GroupType;
import org.bibsonomy.rest.renderer.xml.PostType;
import org.bibsonomy.rest.renderer.xml.TagType;
import org.bibsonomy.rest.renderer.xml.UserType;

/**
 * interface for validating the xml input from the API
 * 
 * @author dzo
 */
public interface XMLModelValidator {

	/**
	 * @param xmlPublication
	 */
	public void checkPublicationXML(BibtexType xmlPublication);

	/**
	 * @param xmlBookmark
	 */
	public void checkBookmarkXML(BookmarkType xmlBookmark);

	/**
	 * @param xmlPost
	 */
	public void checkPost(PostType xmlPost);

	/**
	 * @param xmlTag
	 */
	public void checkTag(TagType xmlTag);

	/**
	 * @param xmlGroup
	 */
	public void checkGroup(GroupType xmlGroup);

	/**
	 * @param xmlUser
	 */
	public void checkUser(UserType xmlUser);

	/**
	 * @param xmlPost
	 */
	public void checkStandardPost(PostType xmlPost);

}
