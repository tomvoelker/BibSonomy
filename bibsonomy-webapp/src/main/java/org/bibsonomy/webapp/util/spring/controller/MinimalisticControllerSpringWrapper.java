/*
 * Created on 07.10.2007
 */
package org.bibsonomy.webapp.util.spring.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.LuceneException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.webapp.command.BaseCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.exceptions.ServiceUnavailableException;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ResponseLogic;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.propertyeditors.CustomDateEditor;
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
 * 
 * @version $Id$
 */
public class MinimalisticControllerSpringWrapper<T extends BaseCommand> extends BaseCommandController {
	private static final String CONTROLLER_ATTR_NAME = "minctrlatrr";
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	private String controllerBeanName;
	private static final Log log = LogFactory.getLog(MinimalisticControllerSpringWrapper.class);
	
	private String[] allowedFields;
	private String[] disallowedFields;	

	/** 
	 * Sets the fields which Spring is allowed to bind to command objects.
	 * <br/>
	 * Note that in the current implementation, these fields are the same for ALL 
	 * controllers.
	 * 
	 * @param allowedFields
	 */
	@Required
	public void setAllowedFields(final String[] allowedFields) {
		this.allowedFields = allowedFields;
	}
	
	/** Sets the fields which Spring must not bind to command objects. 
	 * <br/>
	 * This list overrides the allowedFields, such that a field listed
	 * here will <em>not</em> be bound, even it appears in the allowed
	 * fields! 
	 * 
	 * @param disallowedFields
	 */
	public void setDisallowedFields(final String[] disallowedFields) {
		this.disallowedFields = disallowedFields;
	}
		
	
	/**
	 * @param controllerBeanName the name of the controller bean in the context
	 *                           of the renderer
	 */
	@Required
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
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		((RequestLogic) getApplicationContext().getBean("requestLogic")).setRequest(request); // hack but thats springs fault
		((ResponseLogic) getApplicationContext().getBean("responseLogic")).setResponse(response); // hack but thats springs fault
		final MinimalisticController<T> controller = (MinimalisticController<T>) getApplicationContext().getBean(controllerBeanName);
		/**
		 * Controller is put into request.
		 * 
		 * FIXME: is this still neccessary?
		 * 
		 * SuppressValidation retrieves controller from request again!
		 */
		request.setAttribute(CONTROLLER_ATTR_NAME, controller);
		
		/*
		 * DEBUG: log request attributes
		 */
		if (log.isDebugEnabled()) {
			Enumeration e = request.getAttributeNames();
			while (e.hasMoreElements()) {
				log.debug(e.nextElement().toString());			
			}
		}
		
		final T command = controller.instantiateCommand();

		/*
		 * put context into command
		 * 
		 * TODO: in the future this is hopefully no longer needed, since the wrapper
		 * only exists to transfer request attributes into the command.
		 */
		command.setContext((RequestWrapperContext) request.getAttribute(RequestWrapperContext.class.getName()));

		/*
		 * set validator for this instance
		 */
		if (controller instanceof ValidationAwareController) {
			setValidator(((ValidationAwareController<T>) controller).getValidator());
		}
		
		/*
		 * bind request attributes to command
		 */
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
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			errors.reject(malformed.getMessage());
			log.warn("Could not complete controller (invalid URL scheme) : " + malformed.getMessage());
			view = Views.ERROR;
		}
		catch (ValidationException notValid) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			errors.reject(notValid.getMessage());
			log.error("Could not complete controller (ValidationException)", notValid);
			view = Views.ERROR;
		}
		catch (ServiceUnavailableException e) {
			response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			response.setHeader("Retry-After", Long.toString(e.getRetryAfter()));
			errors.reject(e.getMessage(), new Object[]{e.getRetryAfter()}, "Service unavailable");
			log.warn("Could not complete controller (Service unavailable): " + e.getMessage());
			view = Views.ERROR;
		}
		catch (LuceneException le) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			errors.reject(le.getMessage(), new Object[]{le}, "Internal Server Error (LuceneException)");
			log.error("Could not complete controller (LuceneException).", le);
			view = Views.ERROR;
		}
		catch (Exception ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			errors.reject("error.internal", new Object[]{ex}, "Internal Server Error: " + ex.getMessage());
			log.error("Could not complete controller (general Exception)", ex);
			view = Views.ERROR;
		}
		
		log.debug("Exception catching block passed, putting comand+errors into model.");
		
		final Map<String, Object> model = new HashMap<String, Object>();
		model.put(getCommandName(), command);
		
		/*
		 * put errors into model 
		 */
		model.putAll(errors.getModel());
		
		log.debug("Returning model and view.");
		
		/**
		 * If the view is already a Spring view, use it directly.
		 * The primal reason for the this workaround is, that Spring's RedirctView
		 * automatically appends the model parameters to each redirected URL. This
		 * can only be avoided by calling setExposeModelAttributes(false) on the 
		 * RedirectView. Hence, we must directly create a redirect view instead of 
		 * using a "redirect:..." URL.  
		 */
		if (org.springframework.web.servlet.View.class.isAssignableFrom(view.getClass())) {
			return new ModelAndView((org.springframework.web.servlet.View) view, model);
		}
		return new ModelAndView(view.getName(), model);			
	}

	@Override
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		super.initBinder(request, binder);

		/*
		 * Register a custom date editor to support binding of date fields.
		 * 
		 * FIXME: This is a HACK to allow the DBLP update to set the date of 
		 * (bookmark) posts. The problem is, that the date format is now fixed 
		 * for ALL our controllers, since we can't override this initBinder
		 * method (since we're using this MinimalisticController ... wrapper)
		 *  
		 */
		binder.registerCustomEditor(Date.class, new CustomDateEditor(DATE_FORMAT,false));

		/*
		 * setting the dis/allowed fields for the binder
		 */
		binder.setAllowedFields(allowedFields);
		binder.setDisallowedFields(disallowedFields);
	}
	
}
