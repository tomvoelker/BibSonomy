package org.bibsonomy.layout;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public abstract class AbstractLayoutRenderer {

	public <T extends Resource> void renderResponse(final String layout, final List<Post<T>> posts, final String loginUserName, final HttpServletResponse response) throws IOException {
		renderInternal(layout, posts, loginUserName, response.getOutputStream());
	}
	
	protected  abstract <T extends Resource> void renderInternal(final String layout, final List<Post<T>> posts, final String loginUserName, final OutputStream outputStream) throws IOException;
	
}

