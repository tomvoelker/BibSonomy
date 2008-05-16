package org.bibsonomy.webapp.validation;

import java.net.URL;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * @author rja
 * @version $Id$
 */
public class UserValidator implements Validator<User> {

	@SuppressWarnings("unchecked")
	public boolean supports(final Class clazz) {
		return clazz.equals(User.class);
	}

	public void validate(final Object userObj, final Errors errors) {
		/*
		 * Before we make a detailed check on correctness, we look,
		 * if required attributes are set. 
		 */
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "error.field.required");
		
		/*
		 * detailed checks
		 */
		final User user = (User) userObj;
		validateEmail(user.getEmail(), errors);
		validateName(user.getName(), errors);


		/*
		 * FIXME: if the homepage is already a URL in the bean (command, model),
		 * and the user enters a non-valid URL, an exception is thrown. How can
		 * we catch this exception and instead here care for a correct URL?
		 */
		validateHomepage(user.getHomepage(), errors);

	}


	private void validateEmail (final String email, final Errors errors) {
		if (email == null ||
				"".equals(email) || 
				email.indexOf(' ') != -1 ||
				email.indexOf('@') == -1 || 
				email.length() > 255 ||
				email.lastIndexOf(".") < email.lastIndexOf("@") ||
				email.lastIndexOf("@") != email.indexOf("@") ||
				email.length() - email.lastIndexOf(".") < 2	) {
			errors.rejectValue("email","error.field.valid.email");
		}
	}

	private void validateHomepage(final URL homepage, final Errors errors) {
		if (homepage != null) {
			if (!("http".equals(homepage.getProtocol()) || "https".equals(homepage.getProtocol()))) {
				errors.rejectValue("homepage", "error.field.valid.homepage");
			}
		}
	}

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
