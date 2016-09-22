package org.bibsonomy.webapp.util.markdown;

import org.bibsonomy.search.es.help.HelpUtils;
import org.pegdown.ast.ExpLinkNode;

/**
 * replace the link variable with our replacement
 *
 * @author dzo
 */
public class LinkRenderer extends org.pegdown.LinkRenderer {
	
	private String projectHome;
	
	/**
	 * @param projectHome
	 */
	public LinkRenderer(String projectHome) {
		super();
		this.projectHome = projectHome;
	}
	
	/* (non-Javadoc)
	 * @see org.pegdown.LinkRenderer#render(org.pegdown.ast.ExpLinkNode, java.lang.String)
	 */
	@Override
	public Rendering render(ExpLinkNode node, String text) {
		final String url = node.url.replace("${" + HelpUtils.PROJECT_HOME + "}", this.projectHome);
		final Rendering rendering = new Rendering(url, text);
		return rendering;
	}
}
