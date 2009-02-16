package org.bibsonomy.webapp.view;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.layout.Layout;
import org.bibsonomy.layout.LayoutRenderer;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.SimpleResourceViewCommand;
import org.springframework.web.servlet.view.AbstractView;

import tags.Functions;

/**
 * View which uses an {@link LayoutRenderer} to render the output.
 * 
 * @author rja
 * @version $Id$
 * @param <LAYOUT> 
 */
public class LayoutView<LAYOUT extends Layout> extends AbstractView {
	private static final Log log = LogFactory.getLog(LayoutView.class);

	private LayoutRenderer<LAYOUT> layoutRenderer;

	@Override
	protected void renderMergedOutputModel(final Map model, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		/*
		 * get requested layout
		 * FIXME: we transfer this by the UrlRewriteFilter using request attributes 
		 * ... probably this is not a good idea 
		 */
		final String layout   = (String) request.getAttribute("layout");
		/*
		 * get the requested path
		 * we need it to generate the file names for inline content-disposition
		 * FIXME: The path is written into the request by the UrlRewriteFilter 
		 * ... probably this is not a good idea
		 */
		final String requPath = (String) request.getAttribute("requPath");
		
		log.info("rendering layout " + layout + " for path " + requPath);

		/*
		 * get the data
		 * FIXME: constant for "command" - where to get?
		 */
		final Object object = model.get("command");
		if (object instanceof SimpleResourceViewCommand) {
			final SimpleResourceViewCommand command = (SimpleResourceViewCommand) object;
			final String loginUserName = command.getContext().getLoginUser().getName();
			/*
			 * check which list (resource type) needs to be rendered
			 */
			final List<Post<Bookmark>> bookmarkPosts  = command.getBookmark().getList();
			final List<Post<BibTex>> publicationPosts = command.getBibtex().getList();

			try {
				if (bookmarkPosts != null && bookmarkPosts.size() > 0 && layoutRenderer.supportsResourceType(Bookmark.class)) {
					/*
					 * render bookmarks posts
					 */
					renderResponse(layout, requPath, bookmarkPosts, loginUserName, response);
				} else if (publicationPosts != null && publicationPosts.size() > 0 && layoutRenderer.supportsResourceType(BibTex.class)) {
					/*
					 * render publication posts
					 */
					renderResponse(layout, requPath, publicationPosts, loginUserName, response);
				} else {
					/*
					 * FIXME: what to do here?
					 */
				}
			} catch (final Exception e) {
				/*
				 * give user useful feedback, why rendering failed
				 */
			}
		}
		/*
		 * FIXME: what todo here?
		 */

	}
	
	private <T extends Resource> void renderResponse(final String layoutName, final String requPath, final List<Post<T>> posts, final String loginUserName, final HttpServletResponse response) throws IOException {
		
		final LAYOUT layout = layoutRenderer.getLayout(layoutName, loginUserName);
		
		log.info("got layout " + layout);
		
		/*
		 * set the content type headers
		 */				
		response.setContentType(layout.getMimeType());
		response.setCharacterEncoding("UTF-8");
		final String extension = layout.getExtension();
		/*
		 * If an extension is given, which is differrent from ".html", this suggests to the 
		 * browser to show a file dialog.
		 * 
		 * FIXME: add this ".html" check
		 */
		if (extension != null && !extension.trim().equals("") && !extension.equals(".html")) {
			response.setHeader("Content-Disposition", "attachement; filename=" + Functions.makeCleanFileName(requPath) + extension);
		}
		/*
		 * do the real rendering
		 */
		layoutRenderer.renderLayout(layout, posts, response.getOutputStream());
	}

	/**
	 * @return The layout renderer for this view.
	 */
	public LayoutRenderer<LAYOUT> getLayoutRenderer() {
		return this.layoutRenderer;
	}

	/** Set the layout renderer to be used by this view.
	 * 
	 * @param layoutRenderer
	 */
	public void setLayoutRenderer(final LayoutRenderer<LAYOUT> layoutRenderer) {
		this.layoutRenderer = layoutRenderer;
	}

}
