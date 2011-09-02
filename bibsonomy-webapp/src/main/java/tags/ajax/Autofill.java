package tags.ajax;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * TODO: remove as soon as possible
 *
 */
@Deprecated // useless!
public class Autofill extends TagSupport {
	private static final long serialVersionUID = 868854563296030689L;
	private String var;

	/**
	 * @param var
	 */
	public void setVar(final String var) {
	    this.var = var;
	}
	
   @Override
   public int doStartTag() throws JspException {
      try {
         pageContext.getOut().print("<span class=\"autofill\" id=\"" + var + "\"/>");
      } catch (final IOException ioe) {
         throw new JspException("Error: IOException while writing to client" + ioe.getMessage());
      }
      return SKIP_BODY;
   }
}