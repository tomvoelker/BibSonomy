package org.bibsonomy.webapp.util.tags;

import java.util.Collection;
import java.util.Locale;

import javax.servlet.jsp.PageContext;

import org.bibsonomy.webapp.util.spring.i18n.ExposedResourceMessageBundleSource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

/**
 * TODO: add documentation to this class
 *
 * @author dzo
 */
public class MessageSourceKeysTag extends RequestContextAwareTag {
	private static final long serialVersionUID = 8814519023061404076L;
	
	private Locale locale;
	private String var;

	private int scope = PageContext.PAGE_SCOPE;
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.tags.RequestContextAwareTag#doStartTagInternal()
	 */
	@Override
	protected int doStartTagInternal() throws Exception {
		final RequestContext requestContext = this.getRequestContext();
		final WebApplicationContext context = requestContext.getWebApplicationContext();
		final ExposedResourceMessageBundleSource messageSource = context.getBean(ExposedResourceMessageBundleSource.class);
		final Collection<Object> keys = messageSource.getAllMessageKeys(this.locale);
		this.pageContext.setAttribute(this.var, keys, this.scope);
		return 0;
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * @param var the var to set
	 */
	public void setVar(String var) {
		this.var = var;
	}

	/**
	 * @param scope the scope to set
	 */
	public void setScope(int scope) {
		this.scope = scope;
	}
}
