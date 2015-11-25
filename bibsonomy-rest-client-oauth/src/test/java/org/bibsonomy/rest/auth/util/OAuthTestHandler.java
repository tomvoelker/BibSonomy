/**
 * BibSonomy-Rest-Client-OAuth - The REST-client OAuth Accessor.
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.auth.util;
import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.rest.auth.OAuthAPIAccessor;
import org.bibsonomy.rest.client.RestLogicFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * sample how to use {@link OAuthAPIAccessor} 
 * 
 * @author dzo
 */
public class OAuthTestHandler extends AbstractHandler {

	/** the port of the app */
	public static final int PORT = 9191;
	private static final String APP = "http://localhost:" + Integer.valueOf(PORT);

	private static final String SECRET = "thisissecretissecure";
	private static final String HOST = "http://localhost/";
	private static final String KEY = "key";
	
	private static final OAuthAPIAccessor ACCESSOR = new OAuthAPIAccessor(HOST, KEY, SECRET, APP);
	private static final LogicInterface INTERFACE = new RestLogicFactory(HOST + "api/").getLogicAccess(ACCESSOR);

	@Override
	public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		
		// userid => get request token
		final String userId = request.getParameter("user_id");
		if (present(userId)) {
			final String token = request.getParameter("oauth_token");
			ACCESSOR.setRequestToken(token);
			try {
				ACCESSOR.obtainAccessToken();
			} catch (final Exception e) {
				throw new ServletException(e);
			}
		}
		
		// no access token => get one 
		if (!present(ACCESSOR.getAccessToken())) {
			try {
				// print link to get access token
				response.getWriter().println("<a href=\"" + ACCESSOR.getAuthorizationUrl() + "\">Klick me</a>");
				return;
			} catch (final Exception e) {
				throw new ServletException();
			}
		}
		
		// print first ten bookmark titles
		final List<Post<Bookmark>> posts = INTERFACE.getPosts(Bookmark.class, GroupingEntity.USER, ACCESSOR.getRemoteUserId(), null, null, null, SearchType.LOCAL, null, null, null, null, 0, 9);
		for (final Post<Bookmark> post : posts) {
			final Bookmark bookmark = post.getResource();
			response.getWriter().println("<li><a href=\"" + bookmark.getUrl() + "\">" + bookmark.getTitle() + "</a></li>");
		}
	}
}
