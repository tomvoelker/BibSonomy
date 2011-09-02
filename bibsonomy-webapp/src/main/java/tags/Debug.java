package tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO: move to org.bibsonomy.webapp.util.tags package
 * Logs a debug message.
 * 
 * @author rja
 * @version $Id$
 */
public class Debug extends TagSupport {
	private static final Log log = LogFactory.getLog(Debug.class);
	private static final long serialVersionUID = 868854563296030689L;
	private String message;

	/**
	 * @param message the message to set
	 */
	public void setMessage(final String message) {
		this.message = message;
	}

	@Override
	public int doStartTag() throws JspException {
		log.debug(message);
		return SKIP_BODY;
	}
}