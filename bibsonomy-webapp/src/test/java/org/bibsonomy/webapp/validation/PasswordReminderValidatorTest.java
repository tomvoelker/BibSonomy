package org.bibsonomy.webapp.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.bibsonomy.webapp.command.actions.PasswordReminderCommand;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;

/**
 * @author daill
 * @version $Id$
 */
public class PasswordReminderValidatorTest {
	
	/**
	 * Tests, if the PasswordReminderValidator supports() function works as expected.
	 * The validator should only return <code>true</code> on the UserRegistrationCommand's 
	 * class. 
	 */
	@Test
	public void testSupports(){
		final PasswordReminderValidator validator = new PasswordReminderValidator();
		
		assertFalse(validator.supports(String.class));
		
		assertFalse(validator.supports(null));
		
		assertTrue(validator.supports(PasswordReminderCommand.class));
	}
	
	
	/**
	 * tests if the validator fails if the're no arguments
	 */
	@Test
	public void testValidateNullArgument() {
		final PasswordReminderValidator validator = new PasswordReminderValidator();
		@SuppressWarnings("rawtypes")
		final Errors errors = new MapBindingResult(new HashMap(), "user");
		
		try {
			validator.validate(null, errors);
			fail("Should raise an IllegalArgumentException");
		} catch (final IllegalArgumentException e) {
			// ok
		}
	}
	
	/**
	 * non-null requested user should not fail with exception but give some errors.
	 */
	@Test
	public void testValidate() {
		final PasswordReminderValidator validator = new PasswordReminderValidator();
		final PasswordReminderCommand command = new PasswordReminderCommand();
		
		final Errors errors = new BindException(command, "command");
		
		
		assertFalse(errors.hasErrors());
		
		/*
		 * should not fail
		 */
		validator.validate(command, errors);
		
		/*
		 * should contain some entries
		 */
		assertTrue(errors.hasErrors());	
	}
	
	/**
	 * If HTML for captcha is set, fail
	 */
	@Test
	public void testValidateFailOnGivenCaptchaHTML() {
		final PasswordReminderValidator validator = new PasswordReminderValidator();
		final PasswordReminderCommand command = new PasswordReminderCommand();
		
		final Errors errors = new BindException(command, "command");

		
		command.setUserEmail("fooo@bar.de");
		command.setUserName("foobar");
		command.setRecaptcha_response_field("response");
		
		
		assertFalse(errors.hasErrors());
		
		/*
		 * should not fail
		 */
		validator.validate(command, errors);

		/*
		 * no errors
		 */
		assertFalse(errors.hasErrors());
		
		/*
		 * set HTML
		 */
		command.setCaptchaHTML("bar");

		/*
		 * should not fail
		 */
		validator.validate(command, errors);
		
		/*
		 * should contain some entries
		 */
		assertTrue(errors.hasErrors());
	}

}
