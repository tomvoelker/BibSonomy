package org.bibsonomy.webapp.util.spring.security.exceptions;

import org.springframework.security.access.AccessDeniedException;

/**
 * @author dzo
 * @version $Id$
 */
public class AccessDeniedNoticeException extends AccessDeniedException {
	private static final long serialVersionUID = 8538409864663313358L;
	
	private final String notice;
	
	/**
	 * @param msg the message to set
	 * @param notice the notice to set
	 */
	public AccessDeniedNoticeException(final String msg, final String notice) {
		super(msg);
		this.notice = notice;
	}

	/**
	 * @return the notice
	 */
	public String getNotice() {
		return notice;
	}
}
