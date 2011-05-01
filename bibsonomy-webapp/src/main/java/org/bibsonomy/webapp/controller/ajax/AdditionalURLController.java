package org.bibsonomy.webapp.controller.ajax;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.extra.BibTexExtra;
import org.bibsonomy.webapp.command.ajax.AjaxURLCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * AdditionalURLController which extends AjaxController and implements MinimalisticController
 * 
 * This controller handles the additinal URL requests for a given Post
 * 
 * @author Bernd Terbrack
 * @version $Id$
 */
public class AdditionalURLController extends AjaxController implements
		MinimalisticController<AjaxURLCommand> {

	private static final Log log = LogFactory
			.getLog(AdditionalURLController.class);
	private URL url;
	private String urlName;
	private String cKey;
	private String iHash;
	private final ResourceBundle localizedStrings = ResourceBundle.getBundle("messages");
	private final static String ADD_URL = "addUrl";
	private final static String DEL_URL = "deleteUrl";

	@Override
	public AjaxURLCommand instantiateCommand() {
		return new AjaxURLCommand();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.MinimalisticController#workOn(org.bibsonomy.webapp.command.ContextCommand)
	 */
	@Override
	public View workOn(AjaxURLCommand command) {
		log.debug("workOn AdditionalURLController");
		urlName = command.getText();
		cKey = command.getCkey();
		iHash = command.getHash();
		
		//TODO: Probably create a validateRequest method which returns true/false
		
		//-- Validating the request --
		/*
		 * Check whether user is logged in
		 */
		if (!command.getContext().isUserLoggedIn()) {
			command.setResponseString(getXmlError("error.general.login"));
			return Views.AJAX_XML;
		}

		/*
		 * check if the ckey is valid
		 */
		if (!command.getContext().isValidCkey()) {
			command.setResponseString(getXmlError("error.field.valid.ckey"));
			return Views.AJAX_XML;
		}

		/*
		 * Check if the url is valid
		 */
		try {
			url = new URL(command.getUrl());
		} catch (MalformedURLException e) {
			command.setResponseString(getXmlError("error.url.format"));
			return Views.AJAX_XML;
		} catch (Exception e) {
			command.setResponseString(getXmlError("error.url.general"));
			return Views.AJAX_XML;
		}
		//-- End Validating the request --

		/*
		 * If the user wants to add/delete the url, do so, else generate an XML
		 * error string.
		 */
		if (AdditionalURLController.ADD_URL.equals(command.getAction())) {
			command.setResponseString(addURL(command));
		} else if (AdditionalURLController.DEL_URL.equals(command.getAction())) {
			command.setResponseString(deleteURL(command));
		} else {
			command.setResponseString(getXmlError("error.action.valid"));
		}

		return Views.AJAX_XML;
	}

	/**
	 * Method which adds the url to the given post.
	 * 
	 * @param command
	 * @return if succeeded -> XML string containing status, url, text
	 * if not succeeded -> generated XML error string
	 */
	private String addURL(AjaxURLCommand command) {

		log.debug("Adding URL: " + command.getUrl() + " to database. User: "
				+ command.getContext().getLoginUser().getName());

		final Post<? extends Resource> post = logic.getPostDetails(
				command.getHash(), logic.getAuthenticatedUser().getName());
		BibTex resource = ((BibTex) post.getResource());
		BibTexExtra tempBibtEx = new BibTexExtra();
		tempBibtEx.setUrl(url);
		tempBibtEx.setText(urlName);

		/*
		 * Check if the given URL already exists within the given post.
		 * If so -> generate XML error string
		 */
		for (BibTexExtra bibTE : resource.getExtraUrls()) {
			if (tempBibtEx.getUrl().toExternalForm()
					.equals(bibTE.getUrl().toExternalForm())) {
				return getXmlError("error.url.exists");
			}
		}

		resource.getExtraUrls().clear();
		resource.getExtraUrls().add(tempBibtEx);

		final List<Post<? extends Resource>> postList = Collections
				.<Post<? extends Resource>> singletonList(post);
		try{
			logic.updatePosts(postList, PostUpdateOperation.UPDATE_URLS);
		}catch (Exception e){
			log.debug("Error while updatePosts from URLController.");
			return getXmlError("database.exception.unspecified");
		}
		return getXmlSucceeded();
	}

	/**
	 * Method which deletes the url from the given post.
	 * 
	 * @param command
	 * @return if succeeded -> XML string containing status, url, text
	 * if not succeeded -> generated XML error string
	 */
	private String deleteURL(AjaxURLCommand command) {
		
		log.debug("Deleting URL: " + command.getUrl()
				+ " from database. User: "
				+ command.getContext().getLoginUser().getName());
		final Post<? extends Resource> post = logic.getPostDetails(
				command.getHash(), logic.getAuthenticatedUser().getName());
		BibTex resource = (BibTex) post.getResource();

		/*
		 * If the url exists in the db, delete it. Else return an error
		 */
		for (BibTexExtra bibTE : resource.getExtraUrls()) {
			if (url.equals(bibTE.getUrl())) {
				resource.getExtraUrls().clear();
				resource.getExtraUrls().add(bibTE);
				final List<Post<? extends Resource>> postList = Collections
						.<Post<? extends Resource>> singletonList(post);
				try{
					logic.updatePosts(postList, PostUpdateOperation.UPDATE_URLS);
				} catch (Exception e) {
					log.debug("Error while updatePosts from URLController.");
					return getXmlError("database.exception.unspecified");
				}
				return getXmlSucceeded();
			}
		}

		return getXmlError("error.url.exists_not");
	}

	/**
	 * generates AJAX_XML response string with status = error and given reason
	 * 
	 * @param reason
	 *        error reason
	 * @return XML error string.
	 */
	// TODO: Java Utils impl?
	private String getXmlError(String reason) {
		String errorMsg = localizedStrings.getString("error.addUrl").replace(
				"{0}", localizedStrings.getString(reason));
		return "<root><status>error</status><reason>" + errorMsg
				+ "</reason><url>" + url + "</url><text>" + urlName
				+ "</text></root>";
	}
	/**
	 * Method which generates a XML response string [success]
	 * @return XML success string.
	 */
	private String getXmlSucceeded() {
		return "<root><status>ok</status><ckey>"+cKey+"</ckey><hash>"+iHash+"</hash><url>"+ url + "</url><text>" + urlName + "</text></root>";
	}

	/**
	 * @return the log
	 */
	public static Log getLog() {
		return log;
	}

	/**
	 * @return the url
	 */
	public URL getUrl() {
		return this.url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(URL url) {
		this.url = url;
	}

	/**
	 * @return the urlName
	 */
	public String getUrlName() {
		return this.urlName;
	}

	/**
	 * @param urlName the urlName to set
	 */
	public void setUrlName(String urlName) {
		this.urlName = urlName;
	}

}
