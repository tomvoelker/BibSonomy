package org.bibsonomy.webapp.validation;

import java.util.List;

import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.actions.EditPostCommand;
import org.springframework.validation.Errors;


/**
 * 
 * @param <R> 
 * @author dzo
 * @version $Id$
 */
public class GoldStandardPostValidator<R extends Resource> extends PostValidator<R> {
	
	@Override
	protected void validateTags(Errors errors, EditPostCommand<R> command) {
		// goldstandards have no tags
	}
	
	@Override
	public void validateGroups(Errors errors, String abstractGrouping, List<String> groups) {
		// golstandards have no groups
	}
}
