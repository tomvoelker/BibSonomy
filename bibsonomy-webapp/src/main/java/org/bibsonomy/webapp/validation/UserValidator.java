package org.bibsonomy.webapp.validation;

import java.net.URL;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * @author rja
 * @version $Id$
 */
public class UserValidator implements Validator<User> {

	@SuppressWarnings("unchecked")
	public boolean supports(final Class clazz) {
		return User.class.equals(clazz);
	}

	/** Validates the given user object. The object must not be null.
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
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
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "error.field.required");
		
		/*
		 * detailed checks
		 */
		validateEmail(user.getEmail(), errors);
		validateName(user.getName(), errors);


		/*
		 * FIXME: if the homepage is already a URL in the bean (command, model),
		 * and the user enters a non-valid URL, an exception is thrown. How can
		 * we catch this exception and instead here care for a correct URL?
		 */
		validateHomepage(user.getHomepage(), errors);

	}


	/** Validates the correctness of the email-address. This is done by 
	 * some simple tests, e.g., if the address contains whitespace, an '@'
	 * or a '.'.
	 * 
	 * @param email
	 * @param errors
	 */
	private void validateEmail (final String email, final Errors errors) {
		if (email == null ||
				"".equals(email.trim()) || 
				email.indexOf(' ') != -1 ||
				email.indexOf('@') == -1 || 
				email.length() > 255 ||
				email.lastIndexOf(".") < email.lastIndexOf("@") ||
				email.lastIndexOf("@") != email.indexOf("@") ||
				email.length() - email.lastIndexOf(".") < 2	) {
			errors.rejectValue("email","error.field.valid.email");
		}
	}

	/** Checks the validity of the homepage. The homepage might either be NULL 
	 * or a http (or https) address.
	 * 
	 * @param homepage
	 * @param errors
	 */
	private void validateHomepage(final URL homepage, final Errors errors) {
		if (homepage != null) {
			if (!("http".equals(homepage.getProtocol()) || "https".equals(homepage.getProtocol()))) {
				errors.rejectValue("homepage", "error.field.valid.homepage");
			}
		}
	}

	/** Checks the validity of the user name. There are some user names which are 
	 * reserved (public, private, friends, null) and some characters which are not
	 * allowed (whitespace, -, +, /, &, ?, ", \, >, <, %).
	 * 
	 * @param name
	 * @param errors
	 */
	private void validateName (final String name, final Errors errors) {
		/* username must not contain %, otherwise cookie auth does not work, 
		 * because %20 separates username from password in cookie auth */
		if (name == null ||
				"".equals(name) ||
				"public".equals(name) ||
				"private".equals(name) ||
				"friends".equals(name) ||
				"null".equals(name) ||
				name.length()      > 30 ||
				name.matches(".*\\s.*") ||
				name.indexOf('-') != -1 ||
				name.indexOf('+') != -1 ||
				name.indexOf('/') != -1 ||
				name.indexOf(':') != -1 ||
				name.indexOf('&') != -1 ||
				name.indexOf('?') != -1 ||
				name.indexOf('"') != -1 ||
				name.indexOf('\'') != -1 ||
				name.indexOf('>') != -1 ||
				name.indexOf('<') != -1 ||
				name.indexOf('%') != -1) {
			errors.rejectValue("name","error.field.valid.username");
		}
	}

}
