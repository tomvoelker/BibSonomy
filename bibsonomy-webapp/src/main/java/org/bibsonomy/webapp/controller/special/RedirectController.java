/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.webapp.controller.special;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.model.User;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.command.special.RedirectCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.HeaderUtils;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.Errors;

/**
 * Controller for handling various redirects, in particular /my* pages, 
 * the main page search form, and /uri/ content negotiation.
 * 
 * <p>Currently, the following /my* pages are available:
 * <ul>
 * <li>/myBibSonomy or myPUMA</li>
 * <li>/myBibTeX</li>
 * <li>/myRelations</li>
 * <li>/myPDF</li>
 * <li>/myDuplicates</li>
 * </ul>
 * </p>
 * 
 * @author rja
 */
public class RedirectController implements MinimalisticController<RedirectCommand>, RequestAware, ErrorAware {
	private static final Log log = LogFactory.getLog(RedirectController.class);

	private RequestLogic requestLogic;
	private Errors errors;
	private URLGenerator urlGenerator;

	@Override
	public View workOn(final RedirectCommand command) {
		log.debug("handling /redirect URLs");
		String redirectUrl = "/"; // TODO: which URL would be good?

		final User user = command.getContext().getLoginUser();
		final String myPage = command.getMyPage();
		final String search = command.getSearch();
		final String url    = command.getUrl();
		final String scope = command.getScope();
		log.debug("input: myPage=" + myPage + ", search=" + search + ", scope=" + scope + ", url=" + url);

		if (present(myPage)) {
			if (!command.getContext().isUserLoggedIn()) {
				throw new AccessDeniedException("please log in");
			}
			/*
			 * handle /my* pages
			 */
			redirectUrl = this.getMyPageRedirect(myPage, user.getName());
		} else if (present(search) && present(scope)) {
			/*
			 * handle main page search form
			 */
			redirectUrl = this.getSearchPageRedirect(search, scope, command.getRequUser());
		} else if (present(url)) { 
			/* 
			 * Handle /uri/ content negotiating using the Accept: header.
			 */
			log.debug("doing content negotiation for URL " + url);
			redirectUrl = this.getContentNegotiationRedirect(url, this.requestLogic.getAccept());
		}
		log.debug("finally redirecting to " + redirectUrl);
		return new ExtendedRedirectView(redirectUrl);
	}

	/** 
	 * CONTENT NEGOTIATION
	 * Creates a redirect to the requested output format dependent on the HTTP "accept" header.
	 * 
	 * @param url - the requested URL 
	 * @param acceptHeader - the accepted formats
	 * @return - the redirect URL.
	 */
	private String getContentNegotiationRedirect(final String url, final String acceptHeader) {
		log.debug("accepted formats: " + acceptHeader);
		/*
		 * determine relevant resource type
		 */
		int resourceType = 2;
		if (url.startsWith("url")) {
			resourceType = 1;
		}

		/*
		 * build redirectUrl
		 */
		final String responseFormat = HeaderUtils.getResponseFormat(acceptHeader, resourceType);
		/*
		 * check, if specific format returned
		 */
		if (present(responseFormat)) {
			return "/" + responseFormat + "/" + url;
		} 
		/*
		 * redirect to default format
		 */
		return "/" + url;
	}

	/** Handles redirects for main page search form. 
	 * 
	 * @param search
	 * @param scope
	 * @return
	 * @throws UnsupportedEncodingException - if it could not encode the parameters for the redirect.
	 */
	private String getSearchPageRedirect(final String search, final String scope, final String requUser) {
		log.debug("handling redirect for main page search form");
		/*
		 * redirect either to /user/*, to /author/*, to /tag/* or to /concept/tag/* page 
		 */
		if ("author".equals(scope) && present(requUser)) {
			/*
			 * special handling, when requUser is given - this is for /author pages only
			 */
			log.debug("requUser given - handling /author");
			return "/author/" + UrlUtils.safeURIEncode(search) + "?requUser=" + UrlUtils.safeURIEncode(requUser);
		}
		if (scope.startsWith("user:")) {
			/*
			 * special handling, when scope is "user:USERNAME", this is search restricted to the given user name
			 */
			log.debug("scope is user:");
			return "/search/" + UrlUtils.safeURIEncode(search + " " + scope);
		}
		if (scope.startsWith("group:")) {
			/*
			 * special handling, when scope is "group:GROUPNAME", this is search restricted to the given group name
			 */
			log.debug("scope is group:");
			return "/search/" + UrlUtils.safeURIEncode(search + " " + scope);
		}
		/*
		 * all other pages simply go to /scope/search
		 */
		log.debug("generic handling of /scope/search");
		return "/" + scope + "/" + UrlUtils.safeURIEncode(search);


	}

	/** Handles pages starting with /my*, in particular
	 * <ul>
	 * <li>/myBibSonomy</li>
	 * <li>/myBibTeX</li>
	 * <li>/myRelations</li>
	 * <li>/myPDF</li>
	 * <li>/myDuplicates</li>
	 * </ul>
	 * <p>NOTE: this method only works for logged in users. If the user name is empty,
	 * or the myPage unknown, <code>null</code> is returned.</p> 
	 * 
	 * @param myPage - name of the page (i.e., <code>myRelations</code>).
	 * @param loginUserName - name of the logged in user
	 * @return The redirect to the appropriate page
	 */
	private String getMyPageRedirect(final String myPage, final String loginUserName) {
		/*
		 * redirects for /my* pages
		 */
		final String encodedLoggedinUserName = UrlUtils.safeURIEncode(loginUserName);
		if ("myRelations".equals(myPage)) {
			return "/relations/" + encodedLoggedinUserName;
		}
		
		final String userPage = urlGenerator.getUserUrlByUserName(loginUserName);
		/*
		 * XXX: it would be nice that myPUMA and myBibSonomy redirects are only
		 * available in the corresponding themes, but e.g. old and new help pages
		 * are linking to myBibSonomy at the moment
		 */
		if ("myBibSonomy".equals(myPage) || "myPUMA".equals(myPage)) {
			return userPage;
		}
		if ("myown".equalsIgnoreCase(myPage)) {
			return userPage + "/myown";
		}
		if ("myBibTeX".equals(myPage)) {
			return "/bib" + userPage + "?items=1000";
		}
		if ("myPDF".equals(myPage)) {
			return userPage + "?filter=" + FilterEntity.JUST_PDF;
		}
		if ("myDuplicates".equals(myPage)) {
			return userPage + "?filter=" + FilterEntity.DUPLICATES;
		}
		if ("myNotReported".equals(myPage)) {
			return userPage + "/myown+sys:not:reported:*";
		}
		
		log.error("Unknown /my* page called: " + myPage);
		/*
		 * we could not create an appropriate URL -> return null
		 */
		return null;
	}
	
	@Override
	public RedirectCommand instantiateCommand() {
		return new RedirectCommand();
	}
	
	/** 
	 * Supplies the requestLogic to this controller. The controller needs it to 
	 * get the HTTP "accept" header.
	 * 
	 * @see org.bibsonomy.webapp.util.ResponseAware#setResponseLogic(org.bibsonomy.webapp.util.ResponseLogic)
	 */
	@Override
	public void setRequestLogic(final RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}
	
	@Override
	public Errors getErrors() {
		return this.errors;
	}
	
	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}
	
	/**
	 * @param urlGenerator the urlGenerator to set
	 */
	public void setUrlGenerator(URLGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}
}