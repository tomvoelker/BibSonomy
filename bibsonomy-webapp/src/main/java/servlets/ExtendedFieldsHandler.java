package servlets;

import helpers.database.DBPrivnoteManager;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.model.User;
import org.bibsonomy.util.spring.security.AuthenticationUtils;

import filters.ActionValidationFilter;

@Deprecated
public class ExtendedFieldsHandler extends HttpServlet{

	private static final long serialVersionUID = 4051324539558769200L;

	@Override
	public void init(ServletConfig config) throws ServletException{	
		super.init(config); 
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
