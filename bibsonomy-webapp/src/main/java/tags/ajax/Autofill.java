package tags.ajax;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Encodes the given value with URLEncoder in UTF-8 encoding.
 *
 */
public class Autofill extends TagSupport {
	private static final long serialVersionUID = 868854563296030689L;
	private String var;

	public void setVar(String var) {
	    this.var = var;
	}
	
   public int doStartTag() throws JspException {
      try {
         pageContext.getOut().print("<span class=\"autofill\" id=\"" + var + "\"/>");
      } catch (IOException ioe) {
         throw new JspException("Error: IOException while writing to client" + ioe.getMessage());
      }
      return SKIP_BODY;
   }
}