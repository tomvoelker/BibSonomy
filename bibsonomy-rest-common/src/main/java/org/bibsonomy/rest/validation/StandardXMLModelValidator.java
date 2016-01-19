/**
 * BibSonomy-Rest-Common - Common things for the REST-client and server.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
