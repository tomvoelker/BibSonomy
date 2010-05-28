package org.bibsonomy.community.webapp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.community.webapp.command.ContextCommand;
import org.bibsonomy.community.webapp.command.ResourceViewCommand;
import org.bibsonomy.community.webapp.util.RequestWrapperContext;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;


public abstract class AbstractBaseController<T extends ContextCommand> extends AbstractCommandController {
	private static final String OUTPUT_FORMAT = "html"; // "html" or "json"

	private String[] allowedFields;
	private String[] disallowedFields;	
	
	protected void initializeCommand(T command, final HttpServletRequest request) {
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
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected ModelAndView handle(HttpServletRequest request,
			HttpServletResponse response, Object commandObj, BindException errors)
			throws Exception {
		T command = (T)commandObj;
		initializeCommand(command, request);
		
		return workOn(command);
	}
	


	//------------------------------------------------------------------------
	// abstract interface
	//------------------------------------------------------------------------
	/**
	 * @param command a command object initialized by the framework based on
	 *                the parameters of som request-event like a http-request
	 * @return some symbol that describes the next state of the
	 *         application (the view)
	 */
	public abstract ModelAndView workOn(T command);
	protected abstract T instantiateCommand();	
	
	//------------------------------------------------------------------------
	// helper functions
	//------------------------------------------------------------------------
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