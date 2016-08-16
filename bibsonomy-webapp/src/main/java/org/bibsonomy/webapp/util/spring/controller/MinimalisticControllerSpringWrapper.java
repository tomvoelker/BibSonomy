/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.util.spring.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.common.exceptions.ReadOnlyDatabaseException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.rest.exceptions.UnsupportedMediaTypeException;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.webapp.command.ContextCommand;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.TagResourceViewCommand;
import org.bibsonomy.webapp.controller.ajax.AjaxController;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ResponseLogic;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.spring.condition.Condition;
import org.bibsonomy.webapp.util.spring.security.exceptions.ServiceUnavailableException;
import org.bibsonomy.webapp.util.spring.security.exceptions.SpecialAuthMethodRequiredException;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.ExtendedRedirectViewWithAttributes;
import org.bibsonomy.webapp.view.Views;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.springframework.web.servlet.support.RequestContextUtils;

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
@SuppressWarnings("deprecation")
public class MinimalisticControllerSpringWrapper<T extends ContextCommand> extends BaseCommandController {
	private static final Log log = LogFactory.getLog(MinimalisticControllerSpringWrapper.class);
	
	private static final String CONTROLLER_ATTR_NAME = "minctrlatrr";
	
	/**
	 * @param listCommand
	 * @return
	 */
	private static <T extends Resource> int safeSize(ListCommand<Post<T>> listCommand) {
		final List<Post<T>> list = listCommand.getList();
		if (!present(list)) {
			return 0;
		}
		return list.size();
	}

	private String controllerBeanName;
	
	private String[] allowedFields;
	private String[] disallowedFields;
	
	private URLGenerator urlGenerator;
	
	private ConversionService conversionService;
	
	private Condition presenceCondition;

	/**
	 * @param conversionService the conversionService to set
	 */
	@Required
	public void setConversionService(final ConversionService conversionService) {
		this.conversionService = conversionService;
	}

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
	
	/**
	 * @param urlGenerator the urlGenerator to set
	 */
	@Required
	public void setUrlGenerator(final URLGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected boolean suppressValidation(final HttpServletRequest request, final Object command) {
		final MinimalisticController<T> controller = (MinimalisticController<T>) request.getAttribute(CONTROLLER_ATTR_NAME);
		
		// Do not validate on first call
		if (((T)command).getContext().isFirstCall()) {
			return true;
		}
			
		if (controller instanceof ValidationAwareController<?>) {
			return !((ValidationAwareController<T>) controller).isValidationRequired((T)command);
		}
		
		return false;
	}

	/**
	 * instantiates, initializes and runs the MinimalisticController
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final ApplicationContext applicationContext = this.getApplicationContext();
		final RequestLogic requestLogic = applicationContext.getBean("requestLogic", RequestLogic.class);
		requestLogic.setRequest(request); // hack but thats springs fault
		applicationContext.getBean("responseLogic", ResponseLogic.class).setResponse(response); // hack but thats springs fault
		
		final String requestURI = request.getRequestURI();
		final String realRequestPath = getRequestPath(request);
		final String query = request.getQueryString();
		log.debug("Processing " + requestURI + "?" + query + " from " + requestLogic.getInetAddress());
		if (presenceCondition != null && !presenceCondition.eval()) {
			throw new NoSuchRequestHandlingMethodException(request);
		}
		
		final MinimalisticController<T> controller = (MinimalisticController<T>) applicationContext.getBean(controllerBeanName);
		
		/*
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
			final Enumeration<?> e = request.getAttributeNames();
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
		final RequestWrapperContext context = (RequestWrapperContext) request.getAttribute(RequestWrapperContext.class.getName());
		command.setContext(context);
		
		/*
		 * command has only been called previously, if HTTP-Method is POST
		 */
		context.setFirstCall(!request.getMethod().equals("POST"));

		/*
		 * set validator for this instance
		 */
		if (controller instanceof ValidationAwareController<?>) {
			this.setValidator(((ValidationAwareController<T>) controller).getValidator());
		}
		
		/*
		 * flash attributes on redirect
		 */
		final Map<String, ?> flashAttributes = RequestContextUtils.getInputFlashMap(request);
		
		/*
		 * bind request attributes to command
		 */
		final ServletRequestDataBinder binder = bindAndValidate(request, command);
		final BindException errors = new BindException(binder.getBindingResult());
		
		if (present(flashAttributes) && flashAttributes.containsKey(ExtendedRedirectViewWithAttributes.ERRORS_KEY)) {
			final Errors flashErrors = (Errors) flashAttributes.get(ExtendedRedirectViewWithAttributes.ERRORS_KEY);
			errors.addAllErrors(flashErrors);
		}
		
		if (controller instanceof ErrorAware) {
			((ErrorAware)controller).setErrors(errors);
		}
		
		View view;
		
		/*
		 * define error view
		 */
		if (controller instanceof AjaxController) {
			view = Views.AJAX_ERRORS;
		} else {
			view = Views.ERROR;
		}
		
		try {
			view = controller.workOn(command);
		} catch (final MalformedURLSchemeException malformed) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			errors.reject("error.http.notFound", malformed.getMessage());
			log.warn("Could not complete controller (invalid URL scheme) : " + malformed.getMessage());
		} catch (final AccessDeniedException ad) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			errors.reject(ad.getMessage(), ad.getMessage());
			log.warn("Could not complete controller (AccessDeniedException), occured in: " + ad.getStackTrace()[0] + ", msg is: " + ad.getMessage());
		} catch (final SpecialAuthMethodRequiredException sam) {
			// ok -> pass to filter to do the required authentication
			throw sam;
		} catch (final ServiceUnavailableException e) {
			response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			response.setHeader("Retry-After", Long.toString(e.getRetryAfter()));
			errors.reject(e.getMessage(), new Object[]{e.getRetryAfter()}, "Service unavailable");
			/*
			 *  this exception is only thrown in UserLoginController
			 *  if desired, add some logging there. Otherwise, our error logs get
			 *  cluttered.(dbe)
			 */
			// log.warn("Could not complete controller (Service unavailable): " + e.getMessage());
		} catch (final ResourceMovedException e) {
			response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
			response.setHeader("Location", urlGenerator.getPostUrl(e.getResourceType(), e.getNewIntraHash(), e.getUserName()));
		} catch (final ObjectNotFoundException e) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			errors.reject("error.object.notfound", e.getMessage());
			view = Views.ERROR404;
		} catch (final org.springframework.security.access.AccessDeniedException ex) {
			/*
			 * we rethrow the exception here in order that Spring Security can
			 * handle the exception (saving request and redirecting to the login
			 * page (if user is not logged in) or to the access denied page)
			 */
			throw ex;
		} catch (final ReadOnlyDatabaseException e) {
			errors.reject("system.readOnly.notice");
		} catch (final UnsupportedMediaTypeException e) {
			response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
			errors.reject("system.error.unsupportedmediatype");
		} catch (final Exception ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			errors.reject("error.internal", new Object[]{ex}, "Internal Server Error: " + ex.getMessage());
			log.error("Could not complete controller (general exception) for request " + realRequestPath + "?" + request.getQueryString() + " with referer " + request.getHeader("Referer"), ex);
		}
		
		log.debug("Exception catching block passed, putting comand+errors into model.");
		
		final Map<String, Object> model = new HashMap<String, Object>();
		model.put(getCommandName(), command);
		
		if (command instanceof TagResourceViewCommand) {
			final TagResourceViewCommand tagResourceViewCommand = (TagResourceViewCommand) command;
			final int totalCount = safeSize(tagResourceViewCommand.getBibtex()) + safeSize(tagResourceViewCommand.getBookmark()) + safeSize(tagResourceViewCommand.getGoldStandardBookmarks()) + safeSize(tagResourceViewCommand.getGoldStandardPublications());
			
			if (totalCount == 0) {
				// here we check if the requested tags contain a  to handle our old wrong url form encoding in url paths
				if (tagResourceViewCommand.getRequestedTags().contains("+")) {
					
					final List<String> pathElements = Arrays.asList(realRequestPath.split("/"));
					final StringBuilder newRequestUriBuilder = new StringBuilder("/");
					
					final Iterator<String> pathIterator = pathElements.iterator();
					while (pathIterator.hasNext()) {
						final String path = pathIterator.next();
						if (pathIterator.hasNext()) {
							newRequestUriBuilder.append(path);
							newRequestUriBuilder.append("/");
						} else {
							// simple heuristic: the last path element is the path element containing the requested tags
							newRequestUriBuilder.append(path.replaceAll("\\+", "%20"));
						}
					}
					
					if (present(query)) {
						newRequestUriBuilder.append("?").append(query);
					}
					view = new ExtendedRedirectView(newRequestUriBuilder.toString(), true);
				} else {
					// no resources found, render 404 for search engines (soft-404 warning)
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				}
			}
		}
		
		/*
		 * put errors into model 
		 */
		model.putAll(errors.getModel());
		
		
		if (present(flashAttributes)) {
			model.put("flashAttributes", flashAttributes);
		}
		
		log.debug("Returning model and view for " + request.getRequestURI() + "?" + request.getQueryString() + " from " + requestLogic.getInetAddress());
		
		/*
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

	/**
	 * @param request
	 * @return the real request path
	 */
	public static String getRequestPath(final HttpServletRequest request) {
		final Object attribute = request.getAttribute("requPath");
		if (present(attribute)) {
			return attribute.toString();
		}
		return "";
	}

	@Override
	protected void initBinder(final HttpServletRequest request, final ServletRequestDataBinder binder) throws Exception {
		super.initBinder(request, binder);

		/*
		 * set convertion service
		 */
		binder.setConversionService(this.conversionService);
		
		/*
		 * setting the dis/allowed fields for the binder
		 */
		binder.setAllowedFields(allowedFields);
		binder.setDisallowedFields(disallowedFields);
	}

	/**
	 * @return the presenceCondition
	 */
	public Condition getPresenceCondition() {
		return this.presenceCondition;
	}

	/**
	 * @param presenceCondition the presenceCondition to set
	 */
	public void setPresenceCondition(Condition presenceCondition) {
		this.presenceCondition = presenceCondition;
	}
}
