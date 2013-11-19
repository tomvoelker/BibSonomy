package org.bibsonomy.webapp.util.tags;

import org.springframework.web.servlet.tags.RequestContextAwareTag;

/**
 * @author sbo <sbo@cs.uni-kassel.de>
 * @version $Id$
 */
public class StringShortenerTag extends RequestContextAwareTag {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String value;
	
	private int maxlen;
	
	/**
	 * @return maxlen
	 */
	public int getMaxlen() {
		return this.maxlen;
	}

	/**
	 * @param maxlen
	 */
	public void setMaxlen(final int maxlen) {
		this.maxlen = maxlen;
	}

	/**
	 * @return filename
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * @param value
	 */
	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	protected int doStartTagInternal() throws Exception {
		String newFilename = "";
		if(value.length() >= maxlen) {
			
			int offset = maxlen / 2;
			
			newFilename += value.substring(0, offset) + "…" + value.substring(value.length()-offset, value.length());
			this.pageContext.getOut().print(newFilename);
		} else {
			this.pageContext.getOut().print(value);
		}
		return SKIP_BODY;

	}

}
