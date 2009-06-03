package tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Writes CData tags around the given content
 * @author rja
 * @version $Id$
 */
public class CData extends TagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4294965097134366232L;

	/**
	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			pageContext.getOut().print("<![CDATA[");
		} catch (IOException ex) {
			throw new JspException(ex);
		}
		return EVAL_BODY_INCLUDE;
	}
	
	@Override
	public int doEndTag() throws JspException {
		try {
			pageContext.getOut().print("]]>");
		} catch (IOException ex) {
			throw new JspException(ex);
		}
		return super.doEndTag();
	}
	
}
