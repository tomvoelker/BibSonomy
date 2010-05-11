package tags;

import java.io.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import java.net.URLEncoder;

/**
 * Encodes the given value with URLEncoder in UTF-8 encoding.
 *
 */
public class Encode extends TagSupport {
	private static final long serialVersionUID = 868854563296030689L;
	private String value;

	public void setValue(String value) {
	    this.value = value;
	}
	
   public int doStartTag() throws JspException {
      try {
         pageContext.getOut().print(URLEncoder.encode(value, "UTF-8"));
      } catch (IOException ioe) {
         throw new JspException("Error: IOException while writing to client" + ioe.getMessage());
      }
      return SKIP_BODY;
   }
}