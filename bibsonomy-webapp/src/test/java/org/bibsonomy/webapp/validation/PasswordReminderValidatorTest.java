package org.bibsonomy.webapp.validation;

import java.util.HashMap;

import junit.framework.Assert;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.actions.PasswordReminderCommand;
import org.bibsonomy.webapp.command.actions.UserRegistrationCommand;
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
		
		Assert.assertFalse(validator.supports(String.class));
		
		Assert.assertFalse(validator.supports(null));
		
		Assert.assertTrue(validator.supports(PasswordReminderCommand.class));
	}
	
	
	/**
	 * tests if the validator fails if the're no arguments
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testValidateNullArgument() {
		final PasswordReminderValidator validator = new PasswordReminderValidator();
		final Errors errors = new MapBindingResult(new HashMap(), "user");
		
		try {
			validator.validate(null, errors);
			Assert.fail("Should raise an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			
		}
	}
	
	/**
	 * requestedUser = null should not pass validation
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testValidateNullUser() {
		final PasswordReminderValidator validator = new PasswordReminderValidator();
		final Errors errors = new MapBindingResult(new HashMap(), "user");
		final PasswordReminderCommand command = new PasswordReminderCommand();
		
		try {
			validator.validate(command, errors);
			Assert.fail("Should raise an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			
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
		
		command.setRequestedUser(new User());
		
		
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
	
	/**
	 * If HTML for captcha is set, fail
	 */
	@Test
	public void testValidateFailOnGivenCaptchaHTML() {
		final PasswordReminderValidator validator = new PasswordReminderValidator();
		final PasswordReminderCommand command = new PasswordReminderCommand();
		
		final Errors errors = new BindException(command, "command");
		
		final User validUser = getValidUser();
		command.setRequestedUser(validUser);
		command.setRecaptcha_response_field("response");
		
		
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
		Assert.assertTrue(errors.hasErrors());
	}
	
	private User getValidUser() {
		final User user = new User();
		user.setName("john_doe");
		user.setEmail("devnull@cs.uni-kassel.de");
		return user;
	}
}
