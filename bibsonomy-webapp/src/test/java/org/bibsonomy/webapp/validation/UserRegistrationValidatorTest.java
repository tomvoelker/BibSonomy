package org.bibsonomy.webapp.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.actions.UserRegistrationCommand;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;


/**
 * @author rja
 * @version $Id$
 */
public class UserRegistrationValidatorTest {
	
	private static UserRegistrationValidator validator;
	
	/**
	 * sets up the validator
	 */
	@BeforeClass
	public static void setupValidator() {
		validator = new UserRegistrationValidator();
	}	

	/**
	 * Tests, if the UserRegistrationValidators supports() function works as expected.
	 * The validator should only return <code>true</code> on the UserRegistrationCommand's 
	 * class. 
	 */
	@Test
	public void testSupports() {
		assertFalse(validator.supports(String.class));
		
		assertFalse(validator.supports(null));
		
		assertTrue(validator.supports(UserRegistrationCommand.class));
	}
	
	/**
	 * tests, if null can be validated
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = IllegalArgumentException.class)
	public void testValidateNullArgument() {
		final Errors errors = new MapBindingResult(new HashMap(), "user");
		
		validator.validate(null, errors);
	}
	
	/**
	 * registerUser = null should not pass validation
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = IllegalArgumentException.class)
	public void testValidateNullUser() {
		final Errors errors = new MapBindingResult(new HashMap(), "user");
		
		final UserRegistrationCommand command = new UserRegistrationCommand();
		
		validator.validate(command, errors);
	}
	
	/**
	 * non-null register user should not fail with exception but give some errors.
	 */
	@Test
	public void testValidate() {
		final UserRegistrationCommand command = new UserRegistrationCommand();
		
		final Errors errors = new BindException(command, "command");
		
		command.setRegisterUser(new User());
		
		
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
	public void testValidateFailOnNonMatchingPasswords() {
		final UserRegistrationCommand command = new UserRegistrationCommand();
		
		final Errors errors = new BindException(command, "command");
		
		final User validUser = getValidUser();
		command.setRegisterUser(validUser);
		command.setPasswordCheck("foo");
		command.setRecaptcha_response_field("response");
		command.setAcceptPrivacy(true);
		
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
		 * set different password
		 */
		command.setPasswordCheck("bat");

		/*
		 * should not fail
		 */
		validator.validate(command, errors);
		
		/*
		 * should contain some entries
		 */
		assertTrue(errors.hasErrors());
	}
	
	private User getValidUser() {
		final User user = new User();
		user.setName("john_doe");
		user.setEmail("devnull@cs.uni-kassel.de");
		user.setPassword("foo");
		return user;
	}
	
}
