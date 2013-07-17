package org.bibsonomy.wiki.tags.shared;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.wiki.tags.SharedTag;

/**
 * Allows to include some predefined CSS styles. Wrapper needed for no direct inclusion
 * of a CSS file.
 * @author niebler
 */
public class DesignTag extends SharedTag {
	
	/** The tag name. */
	private final static String TAG_NAME = "design";
	/** The style attribute, which defines the used CSS file. */
	private final static String STYLE = "style";
	/** The list of allowed attributes for this tag. */
	private final static Set<String> ALLOWED_ATTRIBUTES_SET = new HashSet<String>(Arrays.asList(STYLE));
	
	/**
	 * standard constructor.
	 */
	public DesignTag() {
		super(TAG_NAME);
	}

	/**
	 * checks, if attName is an allowed attribute.
	 * @param attName a string with a possible attribute name.
	 * @return true, if attName is an element of the set of allowed attributes.
	 */
	@Override
	public boolean isAllowedAttribute(final String attName) {
		return ALLOWED_ATTRIBUTES_SET.contains(attName);
	}

	/**
	 * Rendering Method. Creates a HTML string to be included in the Wiki.
	 */
	@Override
	protected String renderSharedTag() {
		// Loesche alles, was nach sonderbarer Navigation aussieht, aus dem Attributswert.
		final String style = this.getAttributes().get(STYLE).toLowerCase().replace("/", "").replace(".", "");
		return "<link rel=\"stylesheet\" href=\"/resources/css/cv_styles/" + style + ".css\" />";
	}

}
