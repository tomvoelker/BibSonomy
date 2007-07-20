package tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Cleans up a string containing LaTeX markup and converts special chars to HTML
 * special chars.
 */
public class Bibclean extends TagSupport {

	private static final long serialVersionUID = -5892761835713355690L;
	private String value;

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			// TODO: escape HTML Entities
			// removes some LaTeX-specific characters and normalizes whitespaces
			pageContext.getOut().print(stringToHTMLString(BibcleanCSV.cleanBibtex(value)));
		} catch (IOException ioe) {
			throw new JspException("Error: IOException while writing to client" + ioe.getMessage());
		}
		return SKIP_BODY;
	}

	public static String stringToHTMLString(String string) {
		StringBuffer sb = new StringBuffer(string.length());
		char c;

		for (int i = 0; i < string.length(); i++) {
			c = string.charAt(i);

			// HTML Special Chars
			if (c == '"')
				sb.append("&quot;");
			else if (c == '&')
				sb.append("&amp;");
			else if (c == '<')
				sb.append("&lt;");
			else if (c == '>')
				sb.append("&gt;");
			else {
				int ci = 0xffff & c;
				if (ci < 160)
					// nothing special only 7 Bit
					sb.append(c);
				else {
					// Not 7 Bit use the unicode system
					sb.append("&#");
					sb.append(new Integer(ci).toString());
					sb.append(';');
				}
			}
		}
		return sb.toString();
	}
}