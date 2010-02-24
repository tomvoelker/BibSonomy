package org.bibsonomy.webapp.validation;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.MalformedURLException;
import java.net.URL;

import org.bibsonomy.common.enums.ProfilePrivlevel;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/**
 * @author cvo
 * @version $Id: UserUpdateProfileValidator.java,v 1.1 2009-12-14 13:50:03
 *          voigtmannc Exp $
 */
public class UserUpdateProfileValidator implements Validator<SettingsViewCommand> {

	@SuppressWarnings("unchecked")
	@Override
	public boolean supports(final Class clazz) {
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

		// check entered real name
		this.checkUserRealName(command.getUser().getRealname(), errors);

		// check gender
		this.checkUserGender(command.getUser().getGender(), errors);

		// check group
		this.checkUserViewableGroup(command.getGroup(), errors);
		// check email
		this.checkUserEmailAdress(command.getUser().getEmail(), errors);
		// check homepage
		this.checkUserHomepage(command.getUser().getHomepage(), errors);
		// check openURL
		this.checkUserOpenURL(command.getUser().getOpenURL(), errors);

		// do not have to be checked
		// check profession
		// check interests
		// check hobbies
		// check place

		// birthday will be checked automatically
	}

	private void checkUserGender(String gender, final Errors errors) {
		boolean match = false;

		if (present(gender)) {
			gender = gender.trim();
			
			final String[] a_genders = { "f", "m" };

			for (final String a_gender: a_genders) {
				if (a_gender.equals(gender)) {
					match = true;
					break;
				}
			}
		}

		if (!match) {
			errors.rejectValue("user.gender", "error.profile.gender");
		}
	}

	private void checkUserOpenURL(String str_URL, final Errors errors) {
		if (present(str_URL)) { // this field do not has to be set
			str_URL = str_URL.trim();
			try {
				new URL(str_URL);
			} catch (final MalformedURLException ex) {

				errors.rejectValue("user.openURL", "error.profile.openurl");
			}
		}
	}

	private void checkUserViewableGroup(final String level, final Errors errors) {
		if (!ProfilePrivlevel.isProfilePrivlevel(level)) {
			errors.rejectValue("user.settings.profilePrivlevel", "error.field.valid.groups"); // TODO: create error message ??!
		}
	}

	private void checkUserEmailAdress(String email, final Errors errors) {
		if (present(email)) { // email address do not have to be set
			email = email.trim();
			if (email.indexOf(' ') != -1 || email.indexOf('@') == -1 || email.length() > 255 || email.lastIndexOf(".") < email.lastIndexOf("@") || email.lastIndexOf("@") != email.indexOf("@") || email.length() - email.lastIndexOf(".") < 2) {
				errors.rejectValue("user.email", "error.field.valid.user.email");
			}
		}
	}

	private void checkUserRealName(final String realname, final Errors errors) {
		if (present(realname)) { // real name do not have to be set
			if ("public".equals(realname) || "private".equals(realname) || "friends".equals(realname) || "null".equals(realname) || realname.length() > 30 || 
//					realname.matches("(?s).*\\s.*") || 
//					realname.matches("[a-zA-Z]*") || 
					realname.indexOf('-') != -1 || realname.indexOf('+') != -1 || realname.indexOf('/') != -1 || realname.indexOf('\\') != -1 || realname.indexOf(':') != -1 || realname.indexOf('&') != -1 || realname.indexOf('?') != -1 || realname.indexOf('"') != -1 || realname.indexOf('\'') != -1 || realname.indexOf('>') != -1 || realname.indexOf('<') != -1 || realname.indexOf('%') != -1) {
				errors.rejectValue("user.realname", "error.field.valid.user.name");
			}
		}
	}

	private void checkUserHomepage(final URL homepage, final Errors errors) {
		if (present(homepage)) {
			if (!("http".equals(homepage.getProtocol()) || "https".equals(homepage.getProtocol()))) {
				errors.rejectValue("user.homepage", "error.field.valid.user.homepage");
			}
		}
	}
}
