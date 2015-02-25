/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.validation;

import java.util.Set;
import java.util.regex.Pattern;

import org.bibsonomy.model.User;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.util.Sets;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * @author rja
 */
public class UserValidator implements Validator<User> {
	
	public static final int USERNAME_MAX_LENGTH = 15;
	
	/**
	 * We allow only a..z A..Z 0..9 - . _ 
	 * (this covers more than 99% of all usernames before introducing this
	 * restriction)
	 */
	public static final Pattern USERNAME_DISALLOWED_CHARACTERS_PATTERN = Pattern.compile("[^a-zA-Z0-9\\.\\-_]");
	
	private static final Set<String> SPECIAL_USER_NAMES = Sets.asSet("public", "private", "friends", "null");
	
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
		if (!errors.hasFieldErrors("name")) {
			validateName(user.getName(), errors, "error.field.valid.user.name");
		}
		
		validateUser(user, errors);
	}

	/**
	 * Checks the validity of the user name. There are some user names which are 
	 * reserved (public, private, friends, null) and some characters which are not
	 * allowed (whitespace, -, +, /, &, ?, ", \, >, <, %).
	 * 
	 * @param name
	 * @param errors
	 * @param errorCode 
	 */
	protected static void validateName(String name, final Errors errors, final String errorCode) {
		if (SPECIAL_USER_NAMES.contains(name) ||
				name.length() > USERNAME_MAX_LENGTH ||
				USERNAME_DISALLOWED_CHARACTERS_PATTERN.matcher(name).find())
		{
			errors.rejectValue("name", errorCode);
		}
	}

}
