package tags;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Encodes the given value TWICE with URLEncoder in UTF-8 encoding.
 */
public class Encode2 extends TagSupport {
	private static final long serialVersionUID = 5195959484762735264L;
	private String value;

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			pageContext.getOut().print(URLEncoder.encode(URLEncoder.encode(value, "UTF-8"), "UTF-8"));
		} catch (IOException ioe) {
			throw new JspException("Error: IOException while writing to client" + ioe.getMessage());
		}
		return SKIP_BODY;
	}
}