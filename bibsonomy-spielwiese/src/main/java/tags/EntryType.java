package tags;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Adaption of SWRC scheme types regarding user given entry types. E.g.: Mapping
 * article --> Article
 * 
 * @author mgr
 */
public class EntryType extends TagSupport {

	static final long serialVersionUID = 234345234589762349l;
	private String value;

	/** Definition of adapted types* */
	private String[] givenEntryNames = { "article", "book", "booklet", "inbook", "incollection", "inproceedings", "manual", "masterthesis", "misc", "phdthesis", "proceedings", "techreport", "unpublished" };
	private String[] mappedEntryNames = { "Article", "Book", "Booklet", "InBook", "InCollection", "InProceedings", "Manual", "MasterThesis", "Misc", "PhDThesis", "Proceedings", "TechnicalReport", "Unpublished" };

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int doStartTag() throws JspException {
		try {

			for (int i = 0; i < givenEntryNames.length; i++) {
				/* Comparison with current entrytype value */
				if (givenEntryNames[i].equals(value)) {
					/* match found -> print and stop loop */
					pageContext.getOut().print(URLEncoder.encode(mappedEntryNames[i], "UTF-8"));
					return SKIP_BODY;
				}
			}

			/* default value is misc */
			pageContext.getOut().print(URLEncoder.encode(mappedEntryNames[8], "UTF-8"));

		} catch (IOException ioe) {
			throw new JspException("Error: IOException while writing to client" + ioe.getMessage());
		}
		return SKIP_BODY;
	}
}