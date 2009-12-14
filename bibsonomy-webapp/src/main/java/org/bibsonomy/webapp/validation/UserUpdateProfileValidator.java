package org.bibsonomy.webapp.validation;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/**
 * @author cvo
 * @version $Id$
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
		
		/*
		 * validate user
		 */
		//check entered real name
		//check gender
		//check birthday
		//check place
		//check group
		//check email
		//check homepage
		//check openURL
		//check profession
		//check interests
		//check hobbies
		
		
		
	}

}
