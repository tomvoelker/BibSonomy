/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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

import org.bibsonomy.webapp.command.ajax.AdminAjaxCommand;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * @author bkr
 */
public class AdminActionsValidatorTest {

	/**
	 * Tests, if the AdminActionsValidator's support() method returns <code>true</code>
	 * only, if AdminAjaxCommand.class is given.
	 */
	@Test
	public void testSupports() {
		final AdminActionsValidator validator = new AdminActionsValidator();
		
		assertFalse(validator.supports(String.class));
		
		assertFalse(validator.supports(null));
		
		assertTrue(validator.supports(AdminAjaxCommand.class));
	}
	
	/**
	 * validate() should not accept null arguments.
	 */
	@Test
	public void testValidateNullArgument() {
		final AdminActionsValidator validator = new AdminActionsValidator();
		final Errors errors = new BindException(new AdminAjaxCommand(), "AdminAjaxCommand");
		
		try {
			validator.validate(null, errors);
			fail("Should raise an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// ok
		}
	}
	
	/**
	 * Complete information given, regular expression is not ok
	 */
	@Test
	public void testValidateFails2() {
		final AdminActionsValidator validator = new AdminActionsValidator();
		final AdminAjaxCommand settings = new AdminAjaxCommand();
		final Errors errors = new BindException(settings, "AdminAjaxCommand");
		
		/*
		 * test regular expression
		 */
		
		settings.setKey("whitelist_exp");
		settings.setValue("?i)foo");
		

		assertFalse(errors.hasErrors());

		/*
		 * should produce no errors
		 */
		validator.validate(settings, errors);
		
		assertTrue(errors.hasErrors());
	}
	
	/**
	 * Complete information given, regular expression is ok
	 */
	@Test
	public void testValidatePasses() {
		final AdminActionsValidator validator = new AdminActionsValidator();
		final AdminAjaxCommand settings = new AdminAjaxCommand();
		final Errors errors = new BindException(settings, "AdminViewCommand");
		
		/*
		 * test regular expression
		 */
		settings.setKey("whitelist_exp");
		settings.setValue("t*est");
		

		assertFalse(errors.hasErrors());

		/*
		 * should produce no errors
		 */
		validator.validate(settings, errors);
		
		assertFalse(errors.hasErrors());
	}
	
	

}
