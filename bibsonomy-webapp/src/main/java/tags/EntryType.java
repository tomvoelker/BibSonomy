package tags;

import static org.bibsonomy.model.util.BibTexUtils.ENTRYTYPES;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Adaption of SWRC scheme types regarding user given entry types. E.g.: Mapping
 * article --> Article
 * 
 * @author mgr
 * @version $Id$
 */
@Deprecated
public class EntryType extends TagSupport {
	private static final long serialVersionUID = 234345234589762349l;
	
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
			for (int i = 0; i < ENTRYTYPES.length; i++) {
				/* Comparison with current entrytype value */
				if (ENTRYTYPES[i].equals(value)) {
					/* match found -> print and stop loop */
					pageContext.getOut().print(URLEncoder.encode(Functions.swrcEntryTypes[i], "UTF-8"));
					return SKIP_BODY;
				}
			}
			
			/* default value is misc */
			pageContext.getOut().print(URLEncoder.encode(Functions.swrcEntryTypes[11], "UTF-8"));  
			return SKIP_BODY;
		} catch (IOException ioe) {
			throw new JspException("Error: IOException while writing to client" + ioe.getMessage());
		}		
	}
}