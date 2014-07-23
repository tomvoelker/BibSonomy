package org.bibsonomy.rest.validation;

import org.bibsonomy.rest.renderer.xml.BibtexType;
import org.bibsonomy.rest.renderer.xml.BookmarkType;
import org.bibsonomy.rest.renderer.xml.GroupType;
import org.bibsonomy.rest.renderer.xml.PostType;
import org.bibsonomy.rest.renderer.xml.TagType;
import org.bibsonomy.rest.renderer.xml.UserType;

/**
 * an implementation that doesn't validate anything
 *
 * @author dzo
 */
public class NonValidatingXMLModelValidator implements XMLModelValidator {

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.validation.XMLModelValidator#checkPublicationXML(org.bibsonomy.rest.renderer.xml.BibtexType)
	 */
	@Override
	public void checkPublicationXML(BibtexType xmlPublication) {
		// noop
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.validation.XMLModelValidator#checkBookmarkXML(org.bibsonomy.rest.renderer.xml.BookmarkType)
	 */
	@Override
	public void checkBookmarkXML(BookmarkType xmlBookmark) {
		// noop
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.validation.XMLModelValidator#checkPost(org.bibsonomy.rest.renderer.xml.PostType)
	 */
	@Override
	public void checkPost(PostType xmlPost) {
		// noop
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.validation.XMLModelValidator#checkTag(org.bibsonomy.rest.renderer.xml.TagType)
	 */
	@Override
	public void checkTag(TagType xmlTag) {
		// noop
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.validation.XMLModelValidator#checkGroup(org.bibsonomy.rest.renderer.xml.GroupType)
	 */
	@Override
	public void checkGroup(GroupType xmlGroup) {
		// noop
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.validation.XMLModelValidator#checkUser(org.bibsonomy.rest.renderer.xml.UserType)
	 */
	@Override
	public void checkUser(UserType xmlUser) {
		// noop
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.validation.XMLModelValidator#checkStandardPost(org.bibsonomy.rest.renderer.xml.PostType)
	 */
	@Override
	public void checkStandardPost(PostType xmlPost) {
		// noop
	}

}
