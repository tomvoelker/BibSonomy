package org.bibsonomy.webapp.util.spring.security.saml.metadata;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.saml.metadata.MetadataGenerator;
import org.springframework.security.saml.metadata.MetadataGeneratorFilter;

/**
 * @author jensi
 */
public class HttpsMetadataGeneratorFilter extends MetadataGeneratorFilter {

	private boolean useHttps;
	private boolean removeTrailingBaseUrlSlash;

	/**
	 * delegates to parent class
	 * @param generator
	 */
	public HttpsMetadataGeneratorFilter(MetadataGenerator generator) {
		super(generator);
	}
	
	@Override
	public void afterPropertiesSet() throws ServletException {
		String s = generator.getEntityBaseURL();
		if (removeTrailingBaseUrlSlash) {
			if (s.endsWith("/")) {
				s = s.substring(0, s.length() - 1);
			}
		}
		s = convertToHttpsIfRequired(s);
		generator.setEntityBaseURL(s);
		super.afterPropertiesSet();
	}
	
	@Override
	protected String getDefaultBaseURL(HttpServletRequest request) {
		String s = super.getDefaultBaseURL(request);
		return convertToHttpsIfRequired(s);
	}

	protected String convertToHttpsIfRequired(String s) {
		if (useHttps == false) {
			return s;
		}
		s = s.replace("http://", "https://");
		if (s.endsWith(":80")) {
			s = s.replace(":80", "");
		} else if (s.endsWith(":8080")) {
			s = s.replace(":8080", ":8443");
		}
		return s;
	}

	/**
	 * @return the useHttps
	 */
	public boolean isUseHttps() {
		return this.useHttps;
	}

	/**
	 * @param useHttps the useHttps to set
	 */
	public void setUseHttps(boolean useHttps) {
		this.useHttps = useHttps;
	}

	/**
	 * @param removeTrailingBaseUrlSlash the removeTrailingBaseUrlSlash to set
	 */
	public void setRemoveTrailingBaseUrlSlash(boolean removeTrailingBaseUrlSlash) {
		this.removeTrailingBaseUrlSlash = removeTrailingBaseUrlSlash;
	}

}
