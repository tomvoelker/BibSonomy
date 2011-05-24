package org.bibsonomy.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.exceptions.DatabaseException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.extra.BibTexExtra;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.command.ajax.AjaxURLCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * FIXME: URLs having a protocol with >2 slashes may result into errors. (e.g. "file:///c|/")
 * FIXME: URLs having spaces may result into errors. (e.g. "http://a a")
 * AdditionalURLController which extends AjaxController and implements
 * MinimalisticController
 * 
 * This controller handles the additional URL requests for a given Post
 * 
 * @author Bernd Terbrack
 * @version $Id: AdditionalURLController.java,v 1.5 2011-05-12 20:48:59 berndt
 *          Exp $
 */
public class AdditionalURLController extends AjaxController implements MinimalisticController<AjaxURLCommand>, ErrorAware {

	private static final Log log = LogFactory.getLog(AdditionalURLController.class);
	private Errors errors;
	
	@Override
	public AjaxURLCommand instantiateCommand() {
		return new AjaxURLCommand();
	}

	/**
	 *
	 * 
	 * @see
	 * org.bibsonomy.webapp.util.MinimalisticController#workOn(org.bibsonomy.webapp.command.ContextCommand)
	 */
	@Override
	public View workOn(AjaxURLCommand command) {
		log.debug("workOn AdditionalURLController");

		// TODO: Probably create a validateRequest method which returns
		// true/false

		// -- Validating the request --
		/*
		 * Check whether user is logged in
		 */
		if (!command.getContext().isUserLoggedIn()) {
			return handleError("error.general.login");
		}

		/*
		 * check if the ckey is valid
		 */
		if (!command.getContext().isValidCkey()) {
			return handleError("error.field.valid.ckey");
		}

		/*
		 * Check if the given url-text is not empty while ADDING a URL
		 */
		if (!present(command.getText()) && HttpMethod.POST.equals(requestLogic.getHttpMethod())) {
			return handleError("error.url.emptyName");
		}

		/*
		 * Check if the given url is not empty
		 */
		if (!present(command.getUrl())) {
			return handleError("error.url.emptyUrl");
		}

		/*
		 * Check if the url is valid
		 */
		if(!UrlUtils.isValid(command.getUrl())) {
			return handleError("error.field.valid.url");
		}
		
		URL url;
		String cleanedUrl = UrlUtils.cleanUrl(command.getUrl());
		try {
			url = new URL(cleanedUrl);
		} catch (MalformedURLException e) {
			return handleError("error.field.valid.url");
		} catch (Exception e) {
			return handleError("error.url.general");
		}
		// -- End Validating the request --

		/*
		 * If the user wants to add/delete the url, do so, add error to errors
		 * and return error view.
		 */
		try {
			switch (this.requestLogic.getHttpMethod()) {
			case POST:
				return addURL(command, url);
			case GET:
				return deleteURL(command, url);
			default:
				return handleError("error.405");
			}
		} catch (final ValidationException ex) {
			return handleError("error.405");
		}
	}

	/**
	 * Method which adds the url to the given post.
	 * 
	 * @param command
	 * @return if succeeded -> XML string containing status, url, text if not
	 *         succeeded -> add error to errors and return error view.
	 */
	private View addURL(AjaxURLCommand command, URL url) {

		log.debug("Adding URL: " + command.getUrl() + " to database. User: " + command.getContext().getLoginUser().getName());

		final Post<? extends Resource> post = logic.getPostDetails(command.getHash(), logic.getAuthenticatedUser().getName());
		BibTex resource = ((BibTex) post.getResource());
		BibTexExtra bibTexExtra = new BibTexExtra();
		bibTexExtra.setUrl(url);
		bibTexExtra.setText(command.getText());
		resource.getExtraUrls().clear();
		resource.getExtraUrls().add(bibTexExtra);

		final List<Post<? extends Resource>> postList = Collections.<Post<? extends Resource>> singletonList(post);

		try {
			logic.updatePosts(postList, PostUpdateOperation.UPDATE_URLS_ADD);
		} catch (DatabaseException e) {
			return handleError("error.url.exists");
		} catch (Exception e) {
			return handleError("database.exception.unspecified");
		}
		command.setResponseString(getXmlSucceeded(command, url));
		return Views.AJAX_XML;
	}

	/**
	 * Method which deletes the url from the given post.
	 * 
	 * @param command
	 * @return if succeeded -> XML string containing status, url, text if not
	 *         succeeded -> add error to errors and return error view.
	 */
	private View deleteURL(AjaxURLCommand command, URL url) {

		log.debug("Deleting URL: " + command.getUrl() + " from database. User: " + command.getContext().getLoginUser().getName());

		final Post<? extends Resource> post = logic.getPostDetails(command.getHash(), logic.getAuthenticatedUser().getName());

		BibTex resource = ((BibTex) post.getResource());
		BibTexExtra bibTexExtra = new BibTexExtra();
		bibTexExtra.setUrl(url);
		resource.getExtraUrls().clear();
		resource.getExtraUrls().add(bibTexExtra);

		final List<Post<? extends Resource>> postList = Collections.<Post<? extends Resource>> singletonList(post);

		try {
			logic.updatePosts(postList, PostUpdateOperation.UPDATE_URLS_DELETE);
		} catch (Exception e) {
			return handleError("database.exception.unspecified");
		}
		command.setResponseString(getXmlSucceeded(command, url));
		return Views.AJAX_XML;
	}

	/**
	 * Method which generates a XML response string [success]
	 * 
	 * @return XML success string.
	 */
	private String getXmlSucceeded(AjaxURLCommand command, URL url) {
		return "<root><status>ok</status><ckey>" + command.getCkey() + "</ckey><hash>" + command.getHash() + "</hash><url>" + url.toExternalForm() + "</url><text>" + command.getText() + "</text></root>";
	}
	
	/*
	 * Method to handle Errors based on urlError enum.
	 */
	private View handleError(final String messageKey) {
		log.debug("An error occured: " + messageKey);
		errors.reject(messageKey);
		return Views.AJAX_ERRORS;
	}

	/**
	 * @return the log
	 */
	public static Log getLog() {
		return log;
	}

	@Override
	public Errors getErrors() {
		return errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}

}
