package org.bibsonomy.webapp.controller.actions;

import org.bibsonomy.webapp.command.actions.EditPublicationCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;

/**
 * 
 * For strange Java Generics reasons I could not implement the 
 * {@link #instantiateEditPostCommand()} method in exactly the
 * same way in the {@link AbstractEditPublicationController}. Thus I had
 * to make that controller abstract and implement the method 
 * here.
 * 
 * The underlying problem is a bit deeper: I had to parametrize
 * {@link AbstractEditPublicationController} to subclass it in
 * {@link PostPublicationController}.
 * 
 * @author rja
 * @version $Id$
 */
public class EditPublicationController extends AbstractEditPublicationController<EditPublicationCommand> implements MinimalisticController<EditPublicationCommand>, ErrorAware {

	@Override
	protected EditPublicationCommand instantiateEditPostCommand() {
		return new EditPublicationCommand();
	}
	
}
