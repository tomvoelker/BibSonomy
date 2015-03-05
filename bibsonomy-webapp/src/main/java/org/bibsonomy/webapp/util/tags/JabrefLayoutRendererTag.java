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
package org.bibsonomy.webapp.util.tags;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.LayoutRenderingException;
import org.bibsonomy.layout.jabref.AbstractJabRefLayout;
import org.bibsonomy.layout.jabref.JabrefLayoutRenderer;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.services.renderer.LayoutRenderer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

/**
 * a jsp tag that prints the rendered layout of the configured
 * JabRefLayoutRenderer in bibsonomy2-servlet.xml
 * 
 * @author dzo
 */
public class JabrefLayoutRendererTag extends RequestContextAwareTag {
	private static final long serialVersionUID = 8006189027834637063L;
	private static final Log log = LogFactory.getLog(JabrefLayoutRendererTag.class);
	
	private static final String SUPPORTED_EXTENSION = ".html";

	/**
	 * the posts to render
	 */
	private List<Post<? extends Resource>> posts;
	
	private String layout;
	
	@Override
	protected int doStartTagInternal() throws Exception {
		try {
			pageContext.getOut().print(this.renderPosts());
		} catch (final IOException ex) {
			throw new JspException("Error: IOException while writing to client" + ex.getMessage());
		}
		return SKIP_BODY;
	}
	
	private String renderPosts() {
		if (present(this.posts)) {
			final LayoutRenderer<AbstractJabRefLayout> renderer = this.getJabRefLayoutRenderer();
			try {
				final AbstractJabRefLayout jabRefLayout = renderer.getLayout(this.layout, "");
				if (!SUPPORTED_EXTENSION.equals(jabRefLayout.getExtension())) {
					return "The requested layout is not valid; only HTML layouts are allowed. Requested extension is: " + jabRefLayout.getExtension();
				}
				return renderer.renderLayout(jabRefLayout, posts, true).toString();
			} catch (final LayoutRenderingException ex) {
				log.error(ex.getMessage());
				return ex.getMessage();
			} catch (final UnsupportedEncodingException ex) {
				log.error(ex.getMessage());
				return "An Encoding error occured while trying to convert to layout '" + layout  + "'.";
			} catch (final IOException ex) {
				log.error(ex.getMessage());
				return "An I/O error occured while trying to convert to layout '" + layout  + "'."; 
			} catch (final Exception ex) {
				log.error(ex.getMessage());
				return "A unknown error occured while processing the layout " + layout + ".";
			}
		}
		return "";
	}
	
	private LayoutRenderer<AbstractJabRefLayout> getJabRefLayoutRenderer() {
		final WebApplicationContext ctx = this.getRequestContext().getWebApplicationContext();
		return ctx.getBean(JabrefLayoutRenderer.class);
	}
	
	/**
	 * @param posts the posts to set
	 */
	public void setPosts(final List<Post<? extends Resource>> posts) {
		this.posts = posts;
	}
	
	/**
	 * @param post the post to set
	 */
	public void setPost(final Post<? extends Resource> post) {
		this.posts = Collections.<Post<? extends Resource>>singletonList(post);
	}
	
	/**
	 * @param layout the layout to set
	 */
	public void setLayout(final String layout) {
		this.layout = layout;
	}
}
