package org.bibsonomy.webapp.util.tags;

import java.net.URL;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.servlet.tags.RequestContextAwareTag;


/**
 * @author dzo
 * @version $Id$
 */
public class StyleSheetTag extends RequestContextAwareTag {
	private static final long serialVersionUID = 3240785013748446953L;
	
	private String path;
	
	@Override
	protected int doStartTagInternal() throws Exception {
		final String fullPath = FilenameUtils.getFullPath(this.path);
		final String fileNameWithoutExtension = FilenameUtils.getBaseName(this.path);
		String extension = FilenameUtils.getExtension(this.path);
		URL resource = this.pageContext.getServletContext().getResource(this.path);
		/*
		 * if css file does not exists use the less file
		 */
		if (resource == null) {
			extension = "less";
		}
		final String styleSheetPath = FilenameUtils.concat(fullPath, fileNameWithoutExtension) + "." + extension;
		this.pageContext.getOut().print("<link rel=\"stylesheet\" type=\"text/" + extension + "\" href=\"" + styleSheetPath + "\" />");
		return SKIP_BODY;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
}
