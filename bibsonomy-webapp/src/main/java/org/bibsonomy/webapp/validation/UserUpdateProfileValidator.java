package org.bibsonomy.webapp.validation;

import java.net.MalformedURLException;
import java.net.URL;

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
	public boolean supports(Class clazz) {

		return SettingsViewCommand.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		Assert.notNull(target);
		final SettingsViewCommand command = (SettingsViewCommand) target;

		/*
		 * Check the user data.
		 */
		final User user = command.getUser();
		Assert.notNull(user);

		// check entered real name
		checkUserRealName(command.getUser().getRealname(), errors);

		// check gender
		checkUserGender(command.getUser().getGender().trim(), errors);

		// check group
		checkUserViewableGroup(command.getGroup().trim(), errors);
		// check email
		checkUserEmailAdress(command.getUser().getEmail().trim(), errors);
		// check homepage
		checkUserHomepage(command.getUser().getHomepage(), errors);
		// check openURL
		checkUserOpenURL(command.getUser().getOpenURL().trim(), errors);

		// do not have to be checked
		// check profession
		// check interests
		// check hobbies
		// check place

		// birthday will be checked automatically
	}

	private void checkUserGender(String gender, Errors errors) {

		boolean match = false;

		final String[] a_gender = { "f", "m" };

		for (int i = 0; i < a_gender.length; i++) {
			if (gender.equals(a_gender[i])) {
				match = true;
				break;
			}
		}

		if (!match) {
			errors.rejectValue("user.gender", "error.profile.gender");
		}
	}

	private void checkUserOpenURL(String str_URL, Errors errors) {

		if (!"".equals(str_URL)) { // this field do not has to be set
			try {
				new URL(str_URL);
			} catch (MalformedURLException ex) {

				errors.rejectValue("user.openURL", "error.profile.openurl");
			}
		}
	}

	private void checkUserViewableGroup(String group, Errors errors) {

		boolean match = false;

		final String[] a_gender = { "public", "private", "friends" };

		for (int i = 0; i < a_gender.length; i++) {
			if (group.equals(a_gender[i])) {
				match = true;
				break;
			}
		}

		if (!match) {
			errors.rejectValue("group", "error.field.valid.groups");
		}
	}

	private void checkUserEmailAdress(String email, Errors errors) {
		if (email != null) { // email address do not have to be set
			if (email.indexOf(' ') != -1 || email.indexOf('@') == -1 || email.length() > 255 || email.lastIndexOf(".") < email.lastIndexOf("@") || email.lastIndexOf("@") != email.indexOf("@") || email.length() - email.lastIndexOf(".") < 2) {
				errors.rejectValue("user.email", "error.field.valid.user.email");
			}
		}
	}

	private void checkUserRealName(final String realname, Errors errors) {
		if (realname != null) { // real name do not have to be set
			if ("public".equals(realname) || "private".equals(realname) || "friends".equals(realname) || "null".equals(realname) || realname.length() > 30 || realname.matches("(?s).*\\s.*") || realname.indexOf('-') != -1 || realname.indexOf('+') != -1 || realname.indexOf('/') != -1 || realname.indexOf('\\') != -1 || realname.indexOf(':') != -1 || realname.indexOf('&') != -1 || realname.indexOf('?') != -1 || realname.indexOf('"') != -1 || realname.indexOf('\'') != -1 || realname.indexOf('>') != -1 || realname.indexOf('<') != -1 || realname.indexOf('%') != -1) {
				errors.rejectValue("user.realname", "error.field.valid.user.name");
			}
		}
	}

	private void checkUserHomepage(final URL homepage, Errors errors) {
		if (homepage != null) {
			if (!("http".equals(homepage.getProtocol()) || "https".equals(homepage.getProtocol()))) {
				errors.rejectValue("user.homepage", "error.field.valid.user.homepage");
			}
		}
	}
}
