package org.bibsonomy.webapp.util.tags;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.jsp.JspException;

import org.springframework.web.servlet.tags.RequestContextAwareTag;

/**
 * @author wla
 * @version $Id: PublicationReferenceTag.java,v 1.1 2012-03-23 15:55:43 wla Exp
 *          $
 */
public class PublicationReferenceTag extends RequestContextAwareTag {
	private static final long serialVersionUID = 3722057578923267058L;

	/**
	 * text
	 */
	private String text;

	/**
	 * Matcher groups: 1: resource type (bookmark, url, bibtex, publication),
	 * optional 2: intrahash 33 oder 32 characters, required 3: userName
	 * optional, not required for gold standard posts
	 */
	private static final Pattern linkPattern = Pattern.compile("\\[\\[(?:(bookmark|url|bibtex|publication)(?:\\/))?([0-9a-fA-F]{32,33})(?:\\/(.*?))?\\]\\]");
	private static final int RES_TYPE_GROUP_ID = 1;
	private static final int HASH_GROUP_ID = 2;
	private static final int USER_NAME_GROUP_ID = 3;

	private static final Set<String> BIBTEX_RESOURCE_TYPES = new HashSet<String>(Arrays.asList(new String[] { "bibtex", "publication" }));

	private static String BIBTEX = "bibtex";
	private static String BOOKMARK = "bookmark";

	@Override
	protected int doStartTagInternal() throws Exception {
		Matcher matcher = linkPattern.matcher(text);
		while (matcher.find()) {
			StringBuilder url = new StringBuilder("<a class=\"postlink\"href=\"/");
			
			url.append(getResourceString(matcher.group(RES_TYPE_GROUP_ID)));
			
			url.append("/");
			url.append(matcher.group(HASH_GROUP_ID));
			
			String userName = matcher.group(USER_NAME_GROUP_ID);
			if (present(userName)) {
				url.append("/");
				url.append(userName);
			}
			
			url.append("\">" + matcher.group(0) + "</a>");
			text = text.replace(matcher.group(0), url);
		}
		try {
			pageContext.getOut().print(text);
		} catch (final IOException ex) {
			throw new JspException("Error: IOException while writing to client" + ex.getMessage());
		}
		return SKIP_BODY;
	}

	private String getResourceString(String resType) {
		if (!present(resType) || BIBTEX_RESOURCE_TYPES.contains(resType)) {
			return BIBTEX;
		}
		return BOOKMARK;
	}

	/**
	 * @param text
	 *            the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

}
