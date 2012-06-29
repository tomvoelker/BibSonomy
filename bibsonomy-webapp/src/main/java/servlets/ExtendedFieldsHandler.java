package servlets;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.model.User;
import org.bibsonomy.util.spring.security.AuthenticationUtils;

import filters.ActionValidationFilter;

@Deprecated
public class ExtendedFieldsHandler extends AbstractServlet {
	private static final Log log = LogFactory.getLog(ExtendedFieldsHandler.class);
	
	private static final long serialVersionUID = 4051324539558769200L;

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		final User user = AuthenticationUtils.getUser();
		final String currUser = user.getName(); 
		if (currUser == null) {
			response.sendRedirect("/login");
			return;
		}

		/*
		 * action update private note
		 */
		final String action = request.getParameter("action");
		final boolean validCkey = ActionValidationFilter.isValidCkey(request);
		if ((action != null) && validCkey) {
			if ("updatePrivateNote".equals(action)) {		
				final String hash = request.getParameter("hash");
				
				/*
				 * FIXME: missing check, if currUser owns this publication!
				 */
				final String privnote    = request.getParameter("privnote");
				final String oldprivnote = request.getParameter("oldprivnote");
				if ((((privnote == null) && (oldprivnote != null)) || ((privnote != null) && ((oldprivnote == null) || !privnote.equals(oldprivnote))))) {
					/*
					 * something has changed --> write it to DB
					 */
					Connection connection = null;
					PreparedStatement statement = null;
					try {
						connection = this.getConnection();
						statement = connection.prepareStatement("UPDATE bibtex SET privnote = ? WHERE user_name = ? AND simhash2 = ?");
						statement.setString(1, privnote);
						statement.setString(2, currUser);
						statement.setString(3, hash);
						statement.executeUpdate();
					} catch (final SQLException e) {
						log.error("error updating privatenote for " + currUser + " (hash=" + hash + ")");
					} finally {
						this.closeAll(connection, statement);
					}
				}
				
				response.sendRedirect("/bibtex/" + HashID.INTRA_HASH.getId() + URLEncoder.encode(hash, "UTF-8") + "/" + URLEncoder.encode(currUser, "UTF-8"));
				return;
			}
		}
	}
}
