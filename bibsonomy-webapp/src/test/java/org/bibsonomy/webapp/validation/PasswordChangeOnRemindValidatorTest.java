package org.bibsonomy.webapp.validation;
import java.util.HashMap;

import junit.framework.Assert;

import org.bibsonomy.webapp.command.actions.PasswordChangeOnRemindCommand;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;

/**
 * @author daill
 * @version $Id$
 */
public class PasswordChangeOnRemindValidatorTest {
	/**
	 * Tests, if the PasswordReminderValidator supports() function works as expected.
	 * The validator should only return <code>true</code> on the UserRegistrationCommand's 
	 * class. 
	 */
	@Test
	public void testSupports(){
		final PasswordChangeOnRemindValidator validator = new PasswordChangeOnRemindValidator();
		
		Assert.assertFalse(validator.supports(String.class));
		
		Assert.assertFalse(validator.supports(null));
		
		Assert.assertTrue(validator.supports(PasswordChangeOnRemindCommand.class));
	}
	
	/**
	 * Tests if the validator fails if the're no arguments
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testValidateNullArgument() {
		final PasswordChangeOnRemindValidator validator = new PasswordChangeOnRemindValidator();
		final Errors errors = new MapBindingResult(new HashMap(), "user");
		
		try {
			validator.validate(null, errors);
			Assert.fail("Should raise an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			
		}
	}
	
	/**
	 * Tests if the validator fails if the passwords are not equal
	 */
	@Test
	public void testValidateFailOnNonMatchingPasswords() {
		final PasswordChangeOnRemindValidator validator = new PasswordChangeOnRemindValidator();
		final PasswordChangeOnRemindCommand command = new PasswordChangeOnRemindCommand();
		
		final Errors errors = new BindException(command, "command");
		command.setNewPassword("foo");
		command.setUserName("robert");
		command.setPasswordCheck("foo");
		
		
		Assert.assertFalse(errors.hasErrors());
		
		/*
		 * should not fail
		 */
		validator.validate(command, errors);

		/*
		 * no errors
		 */
		Assert.assertFalse(errors.hasErrors());
		
		/*
		 * set different password
		 */
		command.setPasswordCheck("bar");

		/*
		 * should not fail
		 */
		validator.validate(command, errors);
		
		/*
		 * should contain some entries
		 */
		Assert.assertTrue(errors.hasErrors());
		
	}
}
