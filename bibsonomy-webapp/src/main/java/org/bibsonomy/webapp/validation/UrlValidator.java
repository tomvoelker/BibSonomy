package org.bibsonomy.webapp.validation;

import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.command.ajax.AjaxURLCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
/**
 * 
 * FIXME: duplicate of {@link BookmarkValidator}.
 * 
 * @author rja
 * @version $Id$
 */
public class UrlValidator implements Validator<AjaxURLCommand> {
	
	@Override
	public boolean supports(final Class<?> clazz) {
		return AjaxURLCommand.class.equals(clazz);
	}
	
	@Override
	public void validate(final Object obj, final Errors errors) {
		
		Assert.notNull(obj);
		
		if (obj instanceof String) {
			final AjaxURLCommand command = (AjaxURLCommand) obj;
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "url", "error.field.required");

			
			/*
			 * clean url
			 * 
			 * FIXME: a validator MUST NOT modify objects
			 */
			command.setUrl(UrlUtils.cleanUrl(command.getUrl()));
			
			/*
			 * check url
			 */
			final String url = command.getUrl();
			if (url == null || url.equals("http://") || url.startsWith(UrlUtils.BROKEN_URL)) {
				errors.rejectValue("url", "error.field.valid.url");
			}
			
		}
	}

}
