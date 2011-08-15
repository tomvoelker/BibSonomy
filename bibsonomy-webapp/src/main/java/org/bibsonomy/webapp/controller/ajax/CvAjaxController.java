package org.bibsonomy.webapp.controller.ajax;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.webapp.command.ajax.AjaxCvCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * @author Bernd
 * @version $Id$
 */
public class CvAjaxController extends AjaxController implements MinimalisticController<AjaxCvCommand>,ErrorAware {

	private static final Log log = LogFactory.getLog(CvAjaxController.class);
	private Errors errors;
	
	@Override
	public AjaxCvCommand instantiateCommand() {
		return new AjaxCvCommand();
	}
	
	@Override
	public View workOn(AjaxCvCommand command) {
		log.debug("workOn CvAjaxController");
		System.out.println("Hallo");
		return Views.AJAX_JSON;
	}
	
	@Override
	public Errors getErrors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setErrors(Errors errors) {
		// TODO Auto-generated method stub
		
	}

}
