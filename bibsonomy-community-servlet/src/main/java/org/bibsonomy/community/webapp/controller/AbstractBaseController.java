package org.bibsonomy.community.webapp.controller;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.community.webapp.command.BaseCommand;
import org.bibsonomy.community.webapp.command.ClusterViewCommand;
import org.bibsonomy.community.webapp.command.ResourceViewCommand;
import org.bibsonomy.community.webapp.util.RequestWrapperContext;
import org.bibsonomy.model.Tag;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.mvc.AbstractCommandController;


public abstract class AbstractBaseController extends AbstractCommandController {
	private static final String OUTPUT_FORMAT = "html"; // "html" or "json"

	private String[] allowedFields;
	private String[] disallowedFields;	
	
	protected void initializeCommand(ClusterViewCommand<Tag> command, final HttpServletRequest request) {
		/*
		 * create context and populate it with the request
		 */
		final RequestWrapperContext context = new RequestWrapperContext();
		context.setRequest(request);
		/*
		 * put context into request
		 */
		request.setAttribute(RequestWrapperContext.class.getName(), context);
		command.setContext((RequestWrapperContext) request.getAttribute(RequestWrapperContext.class.getName()));
	}
	
	@Override
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		super.initBinder(request, binder);

		/*
		 * setting the dis/allowed fields for the binder
		 */
		binder.setAllowedFields(getAllowedFields());
		binder.setDisallowedFields(getDisallowedFields());
	}
	
	
	protected final String getOutputFormat(ResourceViewCommand command) {
		if( command!=null && command.getFormat()!=null ) {
			return command.getFormat();
		} else {
			return OUTPUT_FORMAT;
		}
	}
	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------

	public void setAllowedFields(String[] allowedFields) {
		this.allowedFields = allowedFields;
	}

	public String[] getAllowedFields() {
		return allowedFields;
	}

	public void setDisallowedFields(String[] disallowedFields) {
		this.disallowedFields = disallowedFields;
	}

	public String[] getDisallowedFields() {
		return disallowedFields;
	}
	
}