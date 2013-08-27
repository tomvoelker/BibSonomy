package org.bibsonomy.wiki.tags.shared;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bibsonomy.wiki.tags.SharedTag;

/**
 * This is a simple name-tag.
 * Usage: <name />
 * 
 * @author Bernd Terbrack
 * @version $Id$
 */
public class NameTag extends SharedTag {
	
	protected final static String PLAIN = "plain";
	private final static Set<String> ALLOWED_ATTRIBUTES_SET = new HashSet<String>(Arrays.asList(PLAIN));
	
	/*
	 * TODO: DISCUSS: should we use the homepage link for the real name?
	 * would it not be better to have a homepage tag and have the name link to the bibsonomy-page of the user?
	 */
	private static final String TAG_NAME = "name";
	
	/**
	 * dafault construtor
	 */
	public NameTag() {
		super(TAG_NAME);
	}

	@Override
	public boolean isAllowedAttribute(final String attName) {
		return ALLOWED_ATTRIBUTES_SET.contains(attName);
	}
	
	@Override
	protected String renderSharedTag() {
		// TODO: Include parameter for enabling or disabling link to user page.
		
		final String name = this.getRequestedRealName();
		
		if (present(name)) {
			// Vielleicht hier noch einen Link zum CV des anderen Users rein? Oder
			// zur persoenlichen Homepage?
			return (this.getAttributes().get(PLAIN) != null ? "" : "<span id='name'><a href='/user/"
					+ this.renderString(this.getRequestedName()) + "'>")
					+ this.renderString(name)
					+ (this.getAttributes().get(PLAIN) != null ? "" : "</a></span>");
		}
		return "";
	}

}
