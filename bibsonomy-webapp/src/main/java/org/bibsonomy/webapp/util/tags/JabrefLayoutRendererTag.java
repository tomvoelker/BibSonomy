package org.bibsonomy.webapp.util.tags;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.taglibs.standard.tag.common.core.OutSupport;
import org.bibsonomy.common.exceptions.LayoutRenderingException;
import org.bibsonomy.layout.jabref.JabrefLayout;
import org.bibsonomy.layout.jabref.JabrefLayoutRenderer;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * a jsp tag that prints the rendered layout of the configured
 * JabRefLayoutRenderer in bibsonomy2-servlet.xml
 * 
 * @author dzo
 * @version $Id$
 */
public class JabrefLayoutRendererTag extends TagSupport {
	private static final long serialVersionUID = 8006189027834637063L;
	
	private static final Log log = LogFactory.getLog(JabrefLayoutRendererTag.class);
	
	
	private static final String SUPPORTED_EXTENSION = ".html";
	// TODO: move
	private static final String SERVLET_CONTEXT_PATH = "org.springframework.web.servlet.FrameworkServlet.CONTEXT.bibsonomy2";

	/**
	 * the posts to render
	 */
	private List<Post<? extends Resource>> posts;
	
	private String layout;

	@Override
	public int doStartTag() throws JspException {
		try {
			OutSupport.out(this.pageContext, true, this.renderPosts());	
		} catch (final IOException ex) {
			throw new JspException("Error: IOException while writing to client" + ex.getMessage());
		}
		
		return super.doStartTag();
	}
	
	private String renderPosts() {
		if (present(this.posts)) {
			final JabrefLayoutRenderer renderer = this.getJabRefLayoutRenderer();
			try {
				final JabrefLayout layout = renderer.getLayout(this.layout, "");
				if (!SUPPORTED_EXTENSION.equals(layout.getExtension())) {
					return "The requested layout is not valid; only HTML layouts are allowed. Requested extension is: " + layout.getExtension();
				}
				return renderer.renderLayout(layout, posts, true).toString();
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
	
	/**
	 * @return the configured jabref layout renderer in bibsonomy2-servlet.xml
	 * this requires the {@link ContextLoader} configured in web.xml
	 */
	private JabrefLayoutRenderer getJabRefLayoutRenderer() {
		final ServletContext servletContext = this.pageContext.getServletContext();
        final WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext, SERVLET_CONTEXT_PATH);
        final Map<String, JabrefLayoutRenderer> renderer = ctx.getBeansOfType(JabrefLayoutRenderer.class);
        
        return renderer.values().iterator().next();
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
