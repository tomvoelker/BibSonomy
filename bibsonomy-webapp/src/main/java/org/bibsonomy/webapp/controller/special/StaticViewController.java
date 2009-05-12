package org.bibsonomy.webapp.controller.special;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.bibsonomy.webapp.command.BaseCommand;

/**
 * This controller only returns the configured view and else does nothing.
 * 
 * @author rja
 * @version $Id$
 */
public class StaticViewController implements MinimalisticController<BaseCommand>{

	private static final Log log = LogFactory.getLog(StaticViewController.class);
	
	private Views view = Views.ERROR;
	
	@Override
	public BaseCommand instantiateCommand() {
		return new BaseCommand();
	}

	@Override
	public View workOn(BaseCommand command) {
		log.debug("returning view " + view);
		return view;
	}

	public Views getView() {
		return this.view;
	}

	/** Set the view this controller shall return.
	 * @param view
	 */
	public void setView(Views view) {
		this.view = view;
	}
	

}
