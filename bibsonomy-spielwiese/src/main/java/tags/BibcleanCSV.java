package tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Cleans up a string containing LaTeX markup and converts special chars to HTML
 * special chars.
 */
public class BibcleanCSV extends TagSupport {

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
			pageContext.getOut().print(cleanBibtex(value).replaceAll("\"", "\"\""));
		} catch (IOException ioe) {
			throw new JspException("Error: IOException while writing to client" + ioe.getMessage());
		}
		return SKIP_BODY;
	}

	public static String cleanBibtex(String value) {
		return value.replaceAll("\\{|\\}", "").replaceAll("\\s+", " ").replaceAll("\\\\\"o", "ö").replaceAll("\\\\\"u", "ü").replaceAll("\\\\\"a", "ä").replaceAll("\\\\\"O", "Ö").replaceAll("\\\\\"U", "Ü").replaceAll("\\\\\"A", "Ä").replaceAll("\\\\\"s", "ß").trim();
	}
}