package servlets;

import helpers.database.DBBibtexURLManager;
import helpers.database.DBPrivnoteManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.model.User;
import org.bibsonomy.model.extra.BibTexExtra;
import org.bibsonomy.util.spring.security.AuthenticationUtils;

import filters.ActionValidationFilter;
import filters.InitUserFilter;

@Deprecated
public class ExtendedFieldsHandler extends HttpServlet{

	private static final long serialVersionUID = 4051324539558769200L;

	@Override
	public void init(ServletConfig config) throws ServletException{	
		super.init(config); 
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final User user = AuthenticationUtils.getUser();
		final String currUser = user.getName(); 
		if (currUser == null) {
			response.sendRedirect("/login?referer=/basket");
			return;
		}

		/*
		 * FIXME: added to allow handling of some stuff at /bibtex/HASH/USER
		 */
		final String action = request.getParameter("action");
		final boolean validCkey = ActionValidationFilter.isValidCkey(request);
		if (validCkey && ("addURL".equals(action) || "deleteURL".equals(action))) {
			final String hash = request.getParameter("hash");
			
			/*
			 * add / delete extra URL
			 */
			final String urlString = request.getParameter("url");
			final URL url2;
			try {
				url2 = new URL(urlString);
			} catch (final MalformedURLException ex) {
				request.setAttribute("error", "The URL you entered is invalid (" + ex.getMessage() + ").");
				getServletConfig().getServletContext().getRequestDispatcher("/errors/error.jsp").forward(request, response);
				return;
			}
			if ("addURL".equals(action)) {
				DBBibtexURLManager.createURL (new BibTexExtra(url2, request.getParameter("text"), null), hash, currUser, validCkey);
			} else if ("deleteURL".equals(action)) {
				DBBibtexURLManager.deleteURL (new BibTexExtra(url2, null, null), hash, currUser, validCkey);
			}
			
			response.sendRedirect("/bibtex/" + HashID.INTRA_HASH.getId() + URLEncoder.encode(hash, "UTF-8") + "/" + URLEncoder.encode(currUser, "UTF-8"));
			return;
		}
	}


	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final User user = AuthenticationUtils.getUser();
		final String currUser = user.getName(); 
		if (currUser == null) {
			response.sendRedirect("/login?referer=/basket");
			return;
		}

		/*
		 * action update private note
		 */
		final String action = request.getParameter("action");
		final boolean validCkey = ActionValidationFilter.isValidCkey(request);
		if (action != null && validCkey) {
			if ("updatePrivateNote".equals(action)) {		
				final String hash = request.getParameter("hash");
				
				/*
				 * FIXME: missing check, if currUser owns this publication!
				 */
				final String privnote    = request.getParameter("privnote");
				final String oldprivnote = request.getParameter("oldprivnote");
				if (((privnote == null && oldprivnote != null) || (privnote != null && (oldprivnote == null || !privnote.equals(oldprivnote))))) {
					/*
					 * something has changed --> write it to DB
					 */
					DBPrivnoteManager.setPrivnoteForUser(privnote, currUser, hash);
				}
				
				response.sendRedirect("/bibtex/" + HashID.INTRA_HASH.getId() + URLEncoder.encode(hash, "UTF-8") + "/" + URLEncoder.encode(currUser, "UTF-8"));
				return;
			}
		}
	}

}
