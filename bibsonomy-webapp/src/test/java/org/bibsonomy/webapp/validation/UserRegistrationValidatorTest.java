package org.bibsonomy.webapp.validation;

import java.util.HashMap;

import junit.framework.Assert;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.actions.UserRegistrationCommand;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;


/**
 * @author rja
 * @version $Id$
 */
public class UserRegistrationValidatorTest {

	/**
	 * Tests, if the UserRegistrationValidators supports() function works as expected.
	 * The validator should only return <code>true</code> on the UserRegistrationCommand's 
	 * class. 
	 */
	@Test
	public void testSupports() {
		final UserRegistrationValidator validator = new UserRegistrationValidator();
		
		Assert.assertFalse(validator.supports(String.class));
		
		Assert.assertFalse(validator.supports(null));
		
		Assert.assertTrue(validator.supports(UserRegistrationCommand.class));
	}
	
	@Test
	public void testValidateNullArgument() {
		final UserRegistrationValidator validator = new UserRegistrationValidator();
		final Errors errors = new MapBindingResult(new HashMap(), "user");
		
		try {
			validator.validate(null, errors);
			Assert.fail("Should raise an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			
		}
	}
	
	@Test
	public void testValidateNullUser() {
		final UserRegistrationValidator validator = new UserRegistrationValidator();
		final Errors errors = new MapBindingResult(new HashMap(), "user");
		
		final UserRegistrationCommand command = new UserRegistrationCommand();
		
		try {
			validator.validate(command, errors);
			Assert.fail("Should raise an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			
		}
	}
	
	@Test
	public void testValidate() {
		final UserRegistrationValidator validator = new UserRegistrationValidator();
		final UserRegistrationCommand command = new UserRegistrationCommand();
		
		final Errors errors = new BindException(command, "command");
		
		command.setRegisterUser(new User());
		
		
		Assert.assertFalse(errors.hasErrors());
		
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
