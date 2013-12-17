package org.bibsonomy.webapp.util.tags;

import java.net.URL;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.servlet.tags.RequestContextAwareTag;


/**
 * This tag looks for the specified CSS file {@link #path}
 * if it can't find it, it replaces it the LESS file 
 * with the same name (for development usage)
 * 
 * @author dzo
  */
public class StyleSheetTag extends RequestContextAwareTag {
	private static final long serialVersionUID = 3240785013748446953L;
	
	private String path;
	
	@Override
	protected int doStartTagInternal() throws Exception {
		final String styleSheetPath;
		
		final URL resource = this.pageContext.getServletContext().getResource(this.path);
		/*
		 * if css file does not exists use the less file
		 */
		if (resource != null) {
			styleSheetPath = this.path;
		} else {
			final String fullPath = this.path.substring(0, this.path.lastIndexOf("/") + 1);
			final String fileNameWithoutExtension = FilenameUtils.getBaseName(this.path);
			styleSheetPath = fullPath + fileNameWithoutExtension + ".less";
		}
		
		this.pageContext.getOut().print("<link rel=\"stylesheet\" type=\"text/" + FilenameUtils.getExtension(styleSheetPath) + "\" href=\"" + styleSheetPath + "\" />");
		return SKIP_BODY;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
}
