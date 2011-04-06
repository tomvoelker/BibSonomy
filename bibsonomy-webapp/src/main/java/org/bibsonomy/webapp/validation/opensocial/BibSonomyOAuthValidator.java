package org.bibsonomy.webapp.validation.opensocial;

import org.bibsonomy.webapp.command.opensocial.OAuthCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.validation.Errors;

/**
 * @author fei
 * @version $Id$
 */
public class BibSonomyOAuthValidator implements  Validator<OAuthCommand>{

	@Override
	public boolean supports(Class<?> clazz) {
		return OAuthCommand.class.equals(clazz);
	}

	@Override
	public void validate(Object oAuthObject, Errors errors) {
		// TODO Auto-generated method stub
	}

}
