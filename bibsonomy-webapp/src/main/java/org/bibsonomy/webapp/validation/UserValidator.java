package org.bibsonomy.webapp.validation;

import java.util.regex.Pattern;

import org.bibsonomy.model.User;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * @author rja
 * @version $Id$
 */
public class UserValidator implements Validator<User> {

	
	private static final int USERNAME_MAX_LENGTH = 15;
	/**
	 * We allow only a..z A..Z 0..9 - . _ 
	 * (this covers more than 99% of all usernames before introducing this
	 * restriction)
	 */
	public static final Pattern USERNAME_DISALLOWED_CHARACTERS_PATTERN = Pattern.compile("[^a-zA-Z0-9\\.\\-_]");

	/**
	 * @param user
	 * @param errors
	 */
	public static void validateUser(final User user, final Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", ERROR_FIELD_REQUIRED_KEY);
		if (!errors.hasFieldErrors("email") && !UserUtils.isValidMailAddress(user.getEmail())) {
			errors.rejectValue("email","error.field.valid.user.email");
		}
		
		/*
		 * FIXME: if the homepage is already a URL in the bean (command, model),
		 * and the user enters a non-valid URL, an exception is thrown. How can
		 * we catch this exception and instead here care for a correct URL?
		 * <br/>
		 * Not a real solution, but a workaround: 
		 *   set <code>typeMismatch.java.net.URL</code> in messages.properties
		 * <br/>  
		 * Note, that changing values inside <code>errors</code> is no possible, 
		 * hence, we can not just remove the error there.
		 */
		if (!UserUtils.isValidHomePage(user.getHomepage())) {
			errors.rejectValue("homepage", "error.field.valid.user.homepage");
		}
	}
	
	/** 
	 * Validates {@link User} and also subclasses of {@link User}. 
	 *  
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(final Class<?> clazz) {
		if (clazz != null) {
			return User.class.isAssignableFrom(clazz);
		}
		return false;
	}

	/** Validates the given user object. The object must not be null.
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(final Object userObj, final Errors errors) {

		final User user = (User) userObj;

		/*
		 * Let's check, that the given user is not null.
		 */
		Assert.notNull(user);

		/*
		 * Before we make a detailed check on correctness, we look,
		 * if required attributes are set. 
		 */
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", ERROR_FIELD_REQUIRED_KEY);
		if (!errors.hasFieldErrors("name")) validateName(user.getName(), errors); 
		
		validateUser(user, errors);
	}

	/**
	 * Checks the validity of the user name. There are some user names which are 
	 * reserved (public, private, friends, null) and some characters which are not
	 * allowed (whitespace, -, +, /, &, ?, ", \, >, <, %).
	 * 
	 * @param name
	 * @param errors
	 */
	private void validateName (final String name, final Errors errors) {
		if (name == null ||
				"".equals(name) ||
				"public".equals(name) ||
				"private".equals(name) ||
				"friends".equals(name) ||
				"null".equals(name) ||
				name.length() > USERNAME_MAX_LENGTH ||
				USERNAME_DISALLOWED_CHARACTERS_PATTERN.matcher(name).find())
		{
			errors.rejectValue("name","error.field.valid.user.name");
		}
	}

}
