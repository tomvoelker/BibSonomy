package tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.bibsonomy.util.tex.TexDecode;

/**
 * Cleans up a string containing LaTeX markup and converts special chars to HTML special chars. 
 *
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
			pageContext.getOut().print(cleanBibtex(value).replaceAll("\"","\"\""));
		} catch (IOException ioe) {
			throw new JspException("Error: IOException while writing to client" + ioe.getMessage());
		}
		return SKIP_BODY;
	}

	/**
	 * Decodes a string containing TeX macros into Unicode using {@link TexDecode}.
	 * 
	 * @param value
	 * @return The decoded unicode string.
	 */
	public static String cleanBibtex(final String value) {
		return TexDecode.decode(value).trim();
	}

	
}