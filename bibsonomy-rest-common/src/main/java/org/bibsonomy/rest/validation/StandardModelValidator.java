package org.bibsonomy.rest.validation;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.ModelValidationUtils;

/**
 * stanard implementation for a modelvalidator
 *
 * @author dzo
 */
public class StandardModelValidator implements ModelValidator {

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.validation.ModelValidator#checkPost(org.bibsonomy.model.Post)
	 */
	@Override
	public void checkPost(Post<? extends Resource> post) {
		if (post.getUser() == null) {
			throw new InternServerException("error no user assigned!");
		}
		// there may be posts whithout tags
		// 2009/01/07, fei: as stated above - there are situations where posts don't have tags
		//                  for example, when serializing a post for submission to a remote 
		//                  recommender -> commenting out
		// 2010/03/19, dzo: gold standard posts have also no tags
		// if( post.getTags() == null || post.getTags().size() == 0 ) throw new InternServerException( "error no tags assigned!" );
		if (post.getResource() == null) {
			throw new InternServerException("error no ressource assigned!");
		}
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.validation.ModelValidator#checkPublication(org.bibsonomy.model.BibTex)
	 */
	@Override
	public void checkPublication(BibTex publication) {
		// nothing to check
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.validation.ModelValidator#checkBookmark(org.bibsonomy.model.Bookmark)
	 */
	@Override
	public void checkBookmark(Bookmark bookmark) {
		ModelValidationUtils.checkBookmark(bookmark);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.validation.ModelValidator#checkUser(org.bibsonomy.model.User)
	 */
	@Override
	public void checkUser(User user) {
		ModelValidationUtils.checkUser(user);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.validation.ModelValidator#checkTag(org.bibsonomy.model.Tag)
	 */
	@Override
	public void checkTag(Tag tag) {
		ModelValidationUtils.checkTag(tag);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.validation.ModelValidator#checkGroup(org.bibsonomy.model.Group)
	 */
	@Override
	public void checkGroup(Group group) {
		ModelValidationUtils.checkGroup(group);
	}

}
