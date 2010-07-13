package org.bibsonomy.webapp.validation;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
/**
 * @author fba
 * @version $Id$
 */
public class BookmarkValidator implements Validator<Bookmark> {
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean supports(final Class clazz) {
		return Bookmark.class.equals(clazz);
	}
	
	@Override
	public void validate(final Object obj, final Errors errors) {
		
		Assert.notNull(obj);
		
		if (obj instanceof Bookmark) {
			final Bookmark bookmark = (Bookmark) obj;
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "url", "error.field.required");

			
			/*
			 * clean url
			 * 
			 * FIXME: a validator MUST NOT modify objects
			 */
			bookmark.setUrl(UrlUtils.cleanUrl(bookmark.getUrl()));
			
			/*
			 * check url
			 */
			final String url = bookmark.getUrl();
			if (url == null || url.equals("http://") || url.startsWith(UrlUtils.BROKEN_URL)) {
				errors.rejectValue("url", "error.field.valid.url");
			}
			
		}
	}

}
