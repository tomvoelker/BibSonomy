package org.bibsonomy.webapp.util.tags;

import org.springframework.web.servlet.tags.RequestContextAwareTag;

/**
 * FIXME: currently a workaround to insert a closing and opending div in a loop
 *
 * @author dzo
 */
public class RowTag extends RequestContextAwareTag {
	private static final long serialVersionUID = 2238389821162281015L;

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.tags.RequestContextAwareTag#doStartTagInternal()
	 */
	@Override
	protected int doStartTagInternal() throws Exception {
		this.pageContext.getOut().print("</div><div class=\"row\">");
		return SKIP_BODY;
	}

}
