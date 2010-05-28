package tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author mgr
 * @version $Id$
 * 
 * Anzahl der USer, die auch diese Ressource getaggt haben: Einfärbung des Hintergrundes (v)
 */
public class XotherPersonsStyle extends TagSupport {
	private static final long serialVersionUID = -5215482499853870382L;
	
	private String value;
	
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	
	@Override
	public int doStartTag() throws JspException {
		try {
			// get integer from string
			int v = Integer.parseInt(value) + 4;
			// set maximum
			if (v > 1024) {
				v = 1024;
			}
			// 1024 posts = 100% (=50% of brightness)
			v = (int) (100.0 - Math.log(v / Math.log(2) * 2.0));
			pageContext.getOut().print("background-color: rgb("+v+"%, "+v+"%, "+v+"%);");
		} catch (IOException ioe) {
			throw new JspException("Error: IOException while writing to client" + ioe.getMessage());
		} catch (NumberFormatException e) {
			throw new JspException("Error: NumberFormatException while writing to client" + e.getMessage());
		}
		return SKIP_BODY;
	}
}