/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.validation.util.ValidationTestUtils;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * @author rja
 */
public class UserValidatorTest {
	private static final UserValidator VALIDATOR = new UserValidator();

	/**
	 * Tests, if the UserValidator's support() method returns <code>true</code>
	 * only, if User.class is given.
	 */
	@Test
	public void testSupports() {
		assertFalse(VALIDATOR.supports(String.class));
		assertFalse(VALIDATOR.supports(null));
		assertTrue(VALIDATOR.supports(User.class));
	}
	
	/**
	 * Tests, if the user has a valid 
	 */
	@Test
	public void testValidateEmail() {
		final User user = new User();
		user.setName("testuser");
		
		Errors errors = ValidationTestUtils.validate(VALIDATOR, user);
		assertTrue(errors.hasErrors()); // no mail
		
		user.setEmail("thisisnotanemail");
		errors = ValidationTestUtils.validate(VALIDATOR, user);
		assertTrue(errors.hasErrors());
		
		user.setEmail("testuser1@bibsonomy.org");
		errors = ValidationTestUtils.validate(VALIDATOR, user);
		
		assertFalse(errors.hasErrors());
	}
	
	/**
	 * validate() should not accept null arguments.
	 */
	@Test
	public void testValidateNullArgument() {
		final Errors errors = new BindException(new User(), "registerUser");
		
		try {
			VALIDATOR.validate(null, errors);
			fail("Should raise an IllegalArgumentException");
		} catch (final IllegalArgumentException e) {
			// ok
		}
	}

	/**
	 * Nothing given
	 */
	@Test
	public void testValidateFails() {
		final Errors errors = new BindException(new User(), "registerUser");
		
		assertFalse(errors.hasErrors());
		
		/*
		 * should produce some errors
		 */
		VALIDATOR.validate(new User(), errors);
		
		assertTrue(errors.hasErrors());
	}
	
	/**
	 * Complete registration information given
	 */
	@Test
	public void testValidatePasses() {
		final User user = new User();
		final Errors errors = new BindException(user, "registerUser");
		
		/*
		 * populate user
		 */
		
		user.setName("john");
		user.setEmail("john@example.com");
		user.setPassword("password");
		

		assertFalse(errors.hasErrors());

		/*
		 * should produce no errors
		 */
		VALIDATOR.validate(user, errors);
		
		assertFalse(errors.hasErrors());
	}
	
	/**
	 * Whitespace in user name
	 */
	@Test
	public void testValidateFails2() {
		final User user = new User();
		final Errors errors = new BindException(user, "registerUser");
		
		/*
		 * populate user
		 */
		
		user.setName("john ");
		user.setEmail("john@example.com");
		user.setPassword("password");
		

		assertFalse(errors.hasErrors());

		/*
		 * should produce no errors
		 */
		VALIDATOR.validate(user, errors);
		
		assertTrue(errors.hasErrors());
	}
	
	/**
	 * Linebreak in user name
	 */
	@Test
	public void testValidateFails4() {
		final User user = new User();
		final Errors errors = new BindException(user, "registerUser");
		
		/*
		 * populate user
		 */
		
		user.setName("john\r\n");
		user.setEmail("john@example.com");
		user.setPassword("password");
		

		assertFalse(errors.hasErrors());

		/*
		 * should produce no errors
		 */
		VALIDATOR.validate(user, errors);
		
		assertTrue(errors.hasErrors());
	}
	
	/**
	 * disallowed character in user name
	 */
	@Test
	public void testValidateFails5() {
		final User user = new User();
		final Errors errors = new BindException(user, "registerUser");
		
		/*
		 * populate user
		 */
		
		user.setName("Sören");
		user.setEmail("john@example.com");
		user.setPassword("password");
		

		assertFalse(errors.hasErrors());

		/*
		 * should produce no errors
		 */
		VALIDATOR.validate(user, errors);
		
		assertTrue(errors.hasErrors());
	}
	
	/**
	 * No user name given
	 */
	@Test
	public void testValidateFails3() {
		final User user = new User();
		final Errors errors = new BindException(user, "registerUser");
		
		/*
		 * populate user
		 */
		user.setEmail("john@example.com");
		user.setPassword("password");
		

		assertFalse(errors.hasErrors());

		/*
		 * should produce no errors
		 */
		VALIDATOR.validate(user, errors);
		assertTrue(errors.hasErrors());
	}
}
