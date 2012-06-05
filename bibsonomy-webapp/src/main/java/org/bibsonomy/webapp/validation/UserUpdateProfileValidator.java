package org.bibsonomy.webapp.validation;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import org.bibsonomy.model.User;
import org.bibsonomy.util.Sets;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/**
 * 
 * @author cvo
 * @version $Id$
 */
public class UserUpdateProfileValidator implements Validator<SettingsViewCommand> {
	private static final Set<String> ALLOWED_GENDERS = Sets.asSet("f", "m");
	
	@Override
	public boolean supports(final Class<?> clazz) {
		return SettingsViewCommand.class.equals(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors) {
		Assert.notNull(target);
		final SettingsViewCommand command = (SettingsViewCommand) target;

		/*
		 * Check the user data.
		 */
		final User user = command.getUser();
		Assert.notNull(user);

		this.checkUserRealName(user.getRealname(), errors);
		this.checkUserGender(user.getGender(), errors);
		this.checkUserOpenURL(user.getOpenURL(), errors);

		// do not have to be checked
		// check profession
		// check institution
		// check interests
		// check hobbies
		// check place

		// birthday will be checked automatically
		
		errors.pushNestedPath("user");
		UserValidator.validateUser(user, errors);
		errors.popNestedPath();
	}

	private void checkUserGender(String gender, final Errors errors) {
		if (present(gender)) {
			gender = gender.trim();
			
			if (ALLOWED_GENDERS.contains(gender)) {
				return;
			}
		}
		errors.rejectValue("user.gender", "error.profile.gender");
	}

	private void checkUserOpenURL(String str_URL, final Errors errors) {
		if (present(str_URL)) { // this field is optional
			str_URL = str_URL.trim();
			try {
				new URL(str_URL);
			} catch (final MalformedURLException ex) {
				errors.rejectValue("user.openURL", "error.profile.openurl");
			}
		}
	}

	private void checkUserRealName(final String realname, final Errors errors) {
		if (present(realname)) { // real name is optional
			if (realname.length() > 255) { 
				errors.rejectValue("user.realname", "error.field.valid.user.realname.length");
			}
		}
	}
}
