package tags;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class FormatDate extends TagSupport {

	/*
	 * pattern="yyyy-MM-dd'T'HH:mm:ssZ" 
	 */
	
	private static final long serialVersionUID = 4202115254832670512L;
	private Date value;
	private String type;

	public void setType(String type) {
		this.type = type;
	}
	public void setValue(Date value) {
		this.value = value;
	}

	public int doStartTag() throws JspException {
		try {
			String date = null;
			if ("rss".equals(type)) {
				date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(value);
			} else {
				date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(value);
			}
			pageContext.getOut().print(date.substring(0, date.length()-2) + ":" + date.substring(date.length()-2, date.length()));
		} catch (IOException ioe) {
			throw new JspException("Error: IOException while writing to client" + ioe.getMessage());
		}
		return SKIP_BODY;
	}

}
