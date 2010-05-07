package org.bibsonomy.webapp.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * @author dzo
 * @version $Id$
 */
public class DeleteUserValidatorTest {
	private static final DeleteUserValidator validator = new DeleteUserValidator();
	
	/**
	 * tests {@link DeleteUserValidator#validate(Object, Errors)}
	 */
	@Test
	public void testDeleteUserValidator() {
		final SettingsViewCommand command = new SettingsViewCommand();
		command.setDelete("delete");
		final Errors errors = new BindException(command, "command");
		validator.validate(command, errors);
		
		assertFalse(errors.hasErrors());
		
		command.setDelete("");
		validator.validate(command, errors);
		
		assertTrue(errors.hasErrors());
	}
}
