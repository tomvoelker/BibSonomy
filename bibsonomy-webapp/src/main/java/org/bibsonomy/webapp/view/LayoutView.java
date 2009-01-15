package org.bibsonomy.webapp.view;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.layout.AbstractLayoutRenderer;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.webapp.command.SimpleResourceViewCommand;
import org.springframework.web.servlet.view.AbstractView;

/**
 * View which uses an {@link AbstractLayoutRenderer} to render the output.
 * 
 * @author rja
 * @version $Id$
 */
public class LayoutView extends AbstractView {
	private static final Log log = LogFactory.getLog(LayoutView.class);
	
	private AbstractLayoutRenderer layoutRenderer;
	
	@Override
	protected void renderMergedOutputModel(final Map model, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		/*
		 * get requested layout
		 * FIXME: we transfer this by the UrlRewriteFilter using request attributes 
		 * ... probably this is not a good idea 
		 */
		final String layout = (String) request.getAttribute("layout");
		log.debug("rendering layout " + layout);

		/*
		 * get the data
		 */
		final Object object = model.get("command");
		if (object instanceof SimpleResourceViewCommand) {
			final SimpleResourceViewCommand command = (SimpleResourceViewCommand) object;
			final String loginUserName = command.getContext().getLoginUser().getName();
			/*
			 * check which list (resource type) needs to be rendered
			 */
			final List<Post<Bookmark>> bookmarkPosts = command.getBookmark().getList();
			final List<Post<BibTex>> publicationPosts = command.getBibtex().getList();
			if (bookmarkPosts != null && bookmarkPosts.size() > 0) {
				/*
				 * render bookmarks posts
				 */
				getLayoutRenderer().renderResponse(layout, bookmarkPosts, loginUserName, response);
			} else if (publicationPosts != null && publicationPosts.size() > 0) {
				/*
				 * render publication posts
				 */
				getLayoutRenderer().renderResponse(layout, publicationPosts, loginUserName, response);
			} else {
				/*
				 * FIXME: what to do here?
				 */
			}
		}
		/*
		 * FIXME: what todo here?
		 */

	}

	/**
	 * @return The layout renderer for this view.
	 */
	public AbstractLayoutRenderer getLayoutRenderer() {
		return this.layoutRenderer;
	}

	/** Set the layout renderer to be used by this view.
	 * 
	 * @param layoutRenderer
	 */
	public void setLayoutRenderer(AbstractLayoutRenderer layoutRenderer) {
		log.debug("using layout renderer " + layoutRenderer);
		this.layoutRenderer = layoutRenderer;
	}

}
