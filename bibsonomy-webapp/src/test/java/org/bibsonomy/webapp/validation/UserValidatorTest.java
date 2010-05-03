package org.bibsonomy.webapp.validation;

import junit.framework.Assert;

import org.bibsonomy.model.User;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * @author rja
 * @version $Id$
 */
public class UserValidatorTest {

	/**
	 * Tests, if the UserValidator's support() method returns <code>true</code>
	 * only, if User.class is given.
	 */
	@Test
	public void testSupports() {
		final UserValidator validator = new UserValidator();
		
		Assert.assertFalse(validator.supports(String.class));
		
		Assert.assertFalse(validator.supports(null));
		
		Assert.assertTrue(validator.supports(User.class));
	}
	
	/**
	 * validate() should not accept null arguments.
	 */
	@Test
	public void testValidateNullArgument() {
		final UserValidator validator = new UserValidator();
		final Errors errors = new BindException(new User(), "registerUser");
		
		try {
			validator.validate(null, errors);
			Assert.fail("Should raise an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			
		}
	}

	/**
	 * Nothing given
	 */
	@Test
	public void testValidateFails() {
		final UserValidator validator = new UserValidator();
		final Errors errors = new BindException(new User(), "registerUser");
		
		Assert.assertFalse(errors.hasErrors());
		
		/*
		 * should produce some errors
		 */
		validator.validate(new User(), errors);
		
		Assert.assertTrue(errors.hasErrors());
	}
	
	/**
	 * Complete registration information given
	 */
	@Test
	public void testValidatePasses() {
		final UserValidator validator = new UserValidator();
		final User user = new User();
		final Errors errors = new BindException(user, "registerUser");
		
		/*
		 * populate user
		 */
		
		user.setName("john");
		user.setEmail("john@example.com");
		user.setPassword("password");
		

		Assert.assertFalse(errors.hasErrors());

		/*
		 * should produce no errors
		 */
		validator.validate(user, errors);
		
		Assert.assertFalse(errors.hasErrors());
	}
	
	/**
	 * Whitespace in user name
	 */
	@Test
	public void testValidateFails2() {
		final UserValidator validator = new UserValidator();
		final User user = new User();
		final Errors errors = new BindException(user, "registerUser");
		
		/*
		 * populate user
		 */
		
		user.setName("john ");
		user.setEmail("john@example.com");
		user.setPassword("password");
		

		Assert.assertFalse(errors.hasErrors());

		/*
		 * should produce no errors
		 */
		validator.validate(user, errors);
		
		Assert.assertTrue(errors.hasErrors());
	}
	
	
	
	/**
	 * Linebreak in user name
	 */
	@Test
	public void testValidateFails4() {
		final UserValidator validator = new UserValidator();
		final User user = new User();
		final Errors errors = new BindException(user, "registerUser");
		
		/*
		 * populate user
		 */
		
		user.setName("john\r\n");
		user.setEmail("john@example.com");
		user.setPassword("password");
		

		Assert.assertFalse(errors.hasErrors());

		/*
		 * should produce no errors
		 */
		validator.validate(user, errors);
		
		Assert.assertTrue(errors.hasErrors());
	}
	
	/**
	 * disallowed character in user name
	 */
	@Test
	public void testValidateFails5() {
		final UserValidator validator = new UserValidator();
		final User user = new User();
		final Errors errors = new BindException(user, "registerUser");
		
		/*
		 * populate user
		 */
		
		user.setName("Sören");
		user.setEmail("john@example.com");
		user.setPassword("password");
		

		Assert.assertFalse(errors.hasErrors());

		/*
		 * should produce no errors
		 */
		validator.validate(user, errors);
		
		Assert.assertTrue(errors.hasErrors());
	}
	
	/**
	 * No user name given
	 */
	@Test
	public void testValidateFails3() {
		final UserValidator validator = new UserValidator();
		final User user = new User();
		final Errors errors = new BindException(user, "registerUser");
		
		/*
		 * populate user
		 */
		user.setEmail("john@example.com");
		user.setPassword("password");
		

		Assert.assertFalse(errors.hasErrors());

		/*
		 * should produce no errors
		 */
		validator.validate(user, errors);
		
		Assert.assertTrue(errors.hasErrors());
	}
}
