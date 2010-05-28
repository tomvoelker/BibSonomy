package tags;

import javax.servlet.jsp.JspTagException;

import org.springframework.beans.NotReadablePropertyException;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.tags.RequestContextAwareTag;
import org.springframework.web.util.ExpressionEvaluationUtils;

/**
 * The tag checks, if the given (command) path exists and only
 * then executes the content of its body.
 *  
 * @author rja
 * @version $Id$
 */
public class Exists extends RequestContextAwareTag {
	private static final long serialVersionUID = 8378817318583491829L;
	
	
	private String path;
	
	@Override
	protected int doStartTagInternal() throws Exception {
		final String resolvedPath = ExpressionEvaluationUtils.evaluateString("path", getPath(), pageContext);

		try {
			new BindStatus(getRequestContext(), resolvedPath, false);
		} catch (final IllegalStateException ex) {
			throw new JspTagException(ex.getMessage());
		} catch (final NotReadablePropertyException ex) {
			/*
			 * property not found, skip body of tag
			 */
			return SKIP_BODY;
		}
		return EVAL_BODY_INCLUDE;
	}

	
	/**
	 * Set the path that this tag should apply. Can be a bean (e.g. "person"),
	 * or a bean property (e.g. "person.name"). The tag checks 
	 * 
	 * @param path 
	 * 
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Return the path that this tag applies to.
	 * @return The path that this tag applies to.
	 */
	public String getPath() {
		return this.path;
	}
}
