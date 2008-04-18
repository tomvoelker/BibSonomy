/*
 * Created on 07.10.2007
 */
package org.bibsonomy.webapp.util.spring.controller;

import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.command.BaseCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.spring.controller.conversion.ServletRequestAttributeDataBinder;
import org.bibsonomy.webapp.util.spring.factorybeans.Holder;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * Instances of this class wrap MinimalisticController and adapt them
 * to the spring Controller interface. It also registers a custom
 * databinder to also bind attributes to command-properties and not only
 * parameters.
 * 
 * @param <T> type of the command object used in the MinimalisticController
 * 
 * @author Jens Illig
 */
public class MinimalisticControllerSpringWrapper<T extends BaseCommand> extends BaseCommandController {
	private static final String CONTROLLER_ATTR_NAME = "minctrlatrr";
	private String controllerBeanName;
	private static final Logger LOGGER = Logger.getLogger(MinimalisticControllerSpringWrapper.class);
	
	
	/**
	 * @param controllerBeanName the name of the controller bean in the context
	 *                           of the renderer
	 */
	public void setControllerBeanName(final String controllerBeanName) {
		this.controllerBeanName = controllerBeanName;
	}
	
	@Override
	protected boolean suppressValidation(HttpServletRequest request, Object command) {
		final MinimalisticController<T> controller = (MinimalisticController<T>) request.getAttribute(CONTROLLER_ATTR_NAME);
		if (controller instanceof ValidationAwareController) {
			return !((ValidationAwareController<T>) controller).isValidationRequired((T)command);
		}
		return false;
	}

	/**
	 * instantiates, initializes and runs the MinimalisticController
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		((Holder<HttpServletRequest>) getApplicationContext().getBean("requestHolder")).setObj(request); // hack but thats springs fault
		final MinimalisticController<T> controller = (MinimalisticController<T>) getApplicationContext().getBean(controllerBeanName);
		request.setAttribute(CONTROLLER_ATTR_NAME, controller);
		Enumeration e = request.getAttributeNames();
		while (e.hasMoreElements()) {
			LOGGER.debug(e.nextElement().toString());			
		}
		final T command = controller.instantiateCommand();
		ServletRequestDataBinder binder = bindAndValidate(request, command);
		BindException errors = new BindException(binder.getBindingResult());
		if (controller instanceof ErrorAware) {
			((ErrorAware)controller).setErrors(errors);
		}
				
		View view;
		
		try {
			view = controller.workOn(command);
		}
		catch (MalformedURLSchemeException malformed) {
			response.setStatus(HttpStatus.SC_NOT_FOUND);
			command.setError(malformed.getMessage());
			LOGGER.error(malformed);
			view = Views.ERROR;
		}
		catch (ValidationException notValid) {
			response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			command.setError(notValid.getMessage());
			LOGGER.error(notValid);
			view = Views.ERROR;
		}
		catch (Exception ex) {
			response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			command.setError(ex.getMessage());
			LOGGER.error(ex);
			view = Views.ERROR;
		}
		
		final Map<String, Object> model = new HashMap<String, Object>();
		model.put(getCommandName(), command);
		
		return new ModelAndView(view.getName(), model);			
	}

	@Override
	protected ServletRequestDataBinder createBinder(HttpServletRequest request, Object command) throws Exception {
		ServletRequestDataBinder binder = new ServletRequestAttributeDataBinder(command, getCommandName());
		prepareBinder(binder);
		initBinder(request, binder);
		return binder;
	}
	
}
