package org.bibsonomy.webapp.controller.special;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.User;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.command.special.RedirectCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.springframework.validation.Errors;

/**
 * Controller for handling various redirects, in particular /my* pages, 
 * the main page search form, and /uri/ content negotiation.
 * 
 * <p>Currently, the following /my* pages are available:
 * <ul>
 * <li>/myBibSonomy</li>
 * <li>/myBibTeX</li>
 * <li>/myRelations</li>
 * <li>/myPDF</li>
 * <li>/myDuplicates</li>
 * </ul>
 * </p>
 * 
 * @author rja
 * @version $Id$
 */
public class RedirectController implements MinimalisticController<RedirectCommand>, RequestAware, ErrorAware {
	private RequestLogic requestLogic;
	private static final Log log = LogFactory.getLog(RedirectController.class);
	private Errors errors;



	/**
	 * Mapping of mime types to the supported export formats.
	 * Used for content negotiation using /uri/ 
	 */
	private static final String[][] FORMAT_URLS = new String[][] {
		/*   mime-type,     bookmark, publication    */
		{"html", 		null, 	null		},
		{"rss", 		"rss", 	"publrss"	},
		{"rdf+xml",		null,	"swrc"		},
		{"text/plain",  null, 	"bib"		},
		{"plain", 		null, 	"bib"		},
		{"xml", 		"xml",	"layout/dblp"},
		{"rdf",			null,	"burst"		},
		{"bibtex",		null,	"bib"		}
	};	

	public View workOn(final RedirectCommand command) {
		log.debug("handling /redirect URLs");
		String redirectUrl = "/login";

		final User user = command.getContext().getLoginUser();
		final String myPage = command.getMyPage();
		final String search = command.getSearch();
		final String url    = command.getUrl();
		log.debug("input: myPage=" + myPage + ", search=" + search + ", scope=" + command.getScope() + ", url=" + url);

		if (command.getContext().isUserLoggedIn() && ValidationUtils.present(myPage)) {
			/*
			 * handle /my* pages
			 */
			redirectUrl = getMyPageRedirect(myPage, user.getName());
		} else if (ValidationUtils.present(search)) {
			/*
			 * handle main page search form
			 */
			try {
				redirectUrl = getSearchPageRedirect(search, command.getScope(), command.getRequUser());
			} catch (UnsupportedEncodingException e) {
				log.error("Could not search form redirect URL.", e);
			}

		} else if (ValidationUtils.present(url)) { 
			/* ********************************************************************************
			 * 
			 */
			log.debug("doing content negotiation for URL " + url);
			redirectUrl = getContentNegotiationRedirect(url, requestLogic.getAccept());
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
		if (url.startsWith("url")) resourceType = 1;

		/* 
		 * get response format
		 */
		final int responseFormatId = getResponseFormatId(acceptHeader, resourceType);

		/*
		 * build redirectUrl
		 */
		return "/" + FORMAT_URLS[responseFormatId][resourceType] + "/" + url;
	}

	/** Handles redirects for main page search form. 
	 * 
	 * @param search
	 * @param scope
	 * @return
	 * @throws UnsupportedEncodingException - if it could not encode the parameters for the redirect.
	 */
	private String getSearchPageRedirect(final String search, final String scope, final String requUser) throws UnsupportedEncodingException {
		log.debug("handling redirect for main page search form");
		/*
		 * redirect either to /user/*, to /author/*, to /tag/* or to /concept/tag/* page 
		 */
		if ("author".equals(scope) && ValidationUtils.present(requUser)) {
			/*
			 * special handling, when requUser is given - this is for /author pages only
			 */
			log.debug("requUser given - handling /author");
			return "/author/" + URLEncoder.encode(search,"UTF-8") + "?requUser=" + URLEncoder.encode(requUser, "UTF-8");
		}
		if (scope.startsWith("user:")) {
			/*
			 * special handling, when scope is "user:USERNAME", this is search restricted to the given user name
			 */
			log.debug("scope is user:");
			return "/search/" + URLEncoder.encode(search + " " + scope, "UTF-8");
		}
		if (scope.startsWith("group:")) {
			/*
			 * special handling, when scope is "group:GROUPNAME", this is search restricted to the given group name
			 */
			log.debug("scope is group:");
			return "/search/" + URLEncoder.encode(search + " " + scope, "UTF-8");
		}
		/*
		 * all other pages simply go to /scope/search
		 */
		log.debug("generic handling of /scope/search");
		return "/" + scope + "/" + URLEncoder.encode(search, "UTF-8");


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
		 * we need a valid user name
		 */
		if (!ValidationUtils.present(loginUserName)) return null;
		/*
		 * redirects for /my* pages
		 */
		try {
			/*
			 * FIXME: use projectName here ?
			 */
			if ("myBibSonomy".equals(myPage)) {
				return "/user/" + URLEncoder.encode(loginUserName, "UTF-8");
			} else if ("myBibTeX".equals(myPage)) {
				return "/bib/user/" + URLEncoder.encode(loginUserName, "UTF-8") + "?items=1000";
			} else if ("myRelations".equals(myPage)) {
				return "/relations/" + URLEncoder.encode(loginUserName, "UTF-8");
			} else if ("myPDF".equals(myPage)) {
				return "/user/" + URLEncoder.encode(loginUserName, "UTF-8") + "?filter=myPDF";
			} else if ("myDuplicates".equals(myPage)) {
				return "/user/" + URLEncoder.encode(loginUserName, "UTF-8") + "?filter=myDuplicates";
			} else {
				log.error("Unknown /my* page called: " + myPage);
			}
		} catch (UnsupportedEncodingException e) {
			log.error("Could not create /my* URL.", e);
		}
		/*
		 * we could not create an appropriate URL -> return null
		 */
		return null;
	}	



	/**
	 * Gets the preferred response format which is supported in 
	 * dependence of the 'q-Value' (similar to a priority)
	 *
	 * @param acceptHeader 
	 * 			the HTML ACCEPT Header
	 * 			(example: 
	 * 				<code>ACCEPT: text/xml,text/html;q=0.9,text/plain;q=0.8,image/png</code>
	 * 				would be interpreted in the following precedence:
	 * 				1) text/xml
	 * 				2) image/png
	 * 				3) text/html
	 * 				4) text/plain)
	 * 			) 	
	 * @param contentType
	 * 			the contentType of the requested resource 
	 * 			<code>0</code> for bookmarks
	 * 			<code>1</code> for BibTeX
	 * @return 
	 * 			an index for access to the FORMAT_URLS array with the 
	 * 			url for redirect
	 */
	private int getResponseFormatId(final String acceptHeader, final int contentType) {		
		int responseFormat = 0;

		// if no acceptHeader is set, return default (= 0);
		if (acceptHeader == null) return responseFormat;

		// maps the q-value to output format (reverse order)
		final SortedMap<Double,Vector<String>> preferredTypes = new TreeMap<Double,Vector<String>>(new Comparator<Double>() {
			public int compare(Double o1, Double o2) {
				if (o1.doubleValue() > o2.doubleValue())
					return -1;
				else if (o1.doubleValue() < o2.doubleValue())
					return 1;
				else
					return o1.hashCode() - o2.hashCode();
			}				
		});		

		// fill map with q-values and formats
		Scanner scanner = new Scanner(acceptHeader.toLowerCase());
		scanner.useDelimiter(",");

		while(scanner.hasNext()) {
			String[] types = scanner.next().split(";");
			String type = types[0];
			double qValue = 1;

			if (types.length != 1) 
				qValue = Double.parseDouble(types[1].split("=")[1]);

			if (!preferredTypes.containsKey(qValue)) {
				Vector<String> v = new Vector<String>();
				v.add(type);				
				preferredTypes.put(qValue, v);
			} else {
				preferredTypes.get(qValue).add(type);					
			}
		}

		List<String> formatOrder = new ArrayList<String>();			
		for (Entry<Double, Vector<String>> entry: preferredTypes.entrySet()) {								
			for (String type: entry.getValue()) {					
				formatOrder.add(type);					
			}
		}

		// check for supported formats
		boolean found = false;
		for (String type: formatOrder) {
			if (found) break;
			for (int j=0; j<FORMAT_URLS.length; j++) {					
				String checkType = FORMAT_URLS[j][0];				
				if (type.indexOf(checkType) != -1) {						
					if (FORMAT_URLS[j][contentType] != null || checkType == "html") {
						responseFormat = j;
						found = true;
						break;	
					}
				}
			}
		}		
		return responseFormat;
	}

	public RedirectCommand instantiateCommand() {
		return new RedirectCommand();
	}
	/** 
	 * Supplies the requestLogic to this controller. The controller needs it to 
	 * get the HTTP "accept" header.
	 * 
	 * @see org.bibsonomy.webapp.util.ResponseAware#setResponseLogic(org.bibsonomy.webapp.util.ResponseLogic)
	 */
	public void setRequestLogic(final RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}
	public Errors getErrors() {
		return errors;
	}
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}
}