package org.bibsonomy.webapp.util.tags;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.jsp.JspException;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.services.URLGenerator;
import org.springframework.web.servlet.tags.RequestContextAwareTag;


/**
 * @author wla
 * @version $Id$
 */
public class PublicationReferenceTag extends RequestContextAwareTag {
	private static final long serialVersionUID = 3722057578923267058L;
	
	/**
	 * text
	 */
	private String text;
	
	
	/**
	 * Matcher groups:
	 * 1: resource type (bookmark, url, bibtex, publication), optional
	 * 2: intrahash 33 oder 32 characters, required
	 * 3: userName optional, not required for gold standard posts
	 */
	private static final Pattern linkPattern = Pattern.compile("\\[\\[(?:(bookmark|url|bibtex|publication)(?:\\/))?([0-9a-fA-F]{32,33})(?:\\/(.*?))?\\]\\]");
	private static final int RES_TYPE_GROUP_ID = 1;
	private static final int HASH_GROUP_ID = 2;
	private static final int USER_NAME_GROUP_ID = 3;
	
	private static final Set<String> BIBTEX_RESOURCE_TYPES = new HashSet<String>(Arrays.asList(new String[] {"bibtex", "publication"}));
	
	@Override
	protected int doStartTagInternal() throws Exception {
		URLGenerator urlGenerator = this.getRequestContext().getWebApplicationContext().getBean(URLGenerator.class);
		Matcher matcher= linkPattern.matcher(text);
		while (matcher.find()) {
			StringBuilder url = new StringBuilder("<a href=\"");
			String postHash = matcher.group(HASH_GROUP_ID);
			if(postHash.length() == 33) {
				postHash = postHash.substring(1);
			}
			url.append(urlGenerator.getPostUrl(getResourceClass(matcher.group(RES_TYPE_GROUP_ID)), postHash, matcher.group(USER_NAME_GROUP_ID)));
			url.append("\">" + matcher.group(0) +"</a>");
			text = text.replace(matcher.group(0), url);
		}
		try {
			pageContext.getOut().print(text);		
		} catch (final IOException ex) {
			throw new JspException("Error: IOException while writing to client" + ex.getMessage());
		}
		return SKIP_BODY;
	}

	/**
	 * 
	 * @param resType
	 * @return
	 */
	private Class<? extends Resource> getResourceClass(String resType) {
		if (!present(resType) || BIBTEX_RESOURCE_TYPES.contains(resType)) {
			return BibTex.class;
		}
		return Bookmark.class;
	}
	
	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

}
