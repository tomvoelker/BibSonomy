package org.bibsonomy.rest.validation;

import org.bibsonomy.model.util.ModelValidationUtils;
import org.bibsonomy.rest.renderer.xml.BibtexType;
import org.bibsonomy.rest.renderer.xml.BookmarkType;
import org.bibsonomy.rest.renderer.xml.GroupType;
import org.bibsonomy.rest.renderer.xml.PostType;
import org.bibsonomy.rest.renderer.xml.TagType;
import org.bibsonomy.rest.renderer.xml.UserType;

/**
 * standard implementation for a xmlmodelvalidator
 *
 * @author dzo
 */
public class StandardXMLModelValidator implements XMLModelValidator {
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.validation.XMLModelValidator#checkPost(org.bibsonomy.rest.renderer.xml.PostType)
	 */
	@Override
	public void checkPost(PostType xmlPost) {
		ModelValidationUtils.checkPost(xmlPost);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.validation.XMLModelValidator#checkPublicationXML(org.bibsonomy.rest.renderer.xml.BibtexType)
	 */
	@Override
	public void checkPublicationXML(BibtexType xmlPublication) {
		ModelValidationUtils.checkPublication(xmlPublication);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.validation.XMLModelValidator#checkBookmarkXML(org.bibsonomy.rest.renderer.xml.BookmarkType)
	 */
	@Override
	public void checkBookmarkXML(BookmarkType xmlBookmark) {
		ModelValidationUtils.checkBookmark(xmlBookmark);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.validation.XMLModelValidator#checkTag(org.bibsonomy.rest.renderer.xml.TagType)
	 */
	@Override
	public void checkTag(TagType xmlTag) {
		ModelValidationUtils.checkTag(xmlTag);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.validation.XMLModelValidator#checkGroup(org.bibsonomy.rest.renderer.xml.GroupType)
	 */
	@Override
	public void checkGroup(GroupType xmlGroup) {
		ModelValidationUtils.checkGroup(xmlGroup);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.validation.XMLModelValidator#checkUser(org.bibsonomy.rest.renderer.xml.UserType)
	 */
	@Override
	public void checkUser(UserType xmlUser) {
		ModelValidationUtils.checkUser(xmlUser);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.validation.XMLModelValidator#checkStandardPost(org.bibsonomy.rest.renderer.xml.PostType)
	 */
	@Override
	public void checkStandardPost(PostType xmlPost) {
		ModelValidationUtils.checkStandardPost(xmlPost);
	}
}
