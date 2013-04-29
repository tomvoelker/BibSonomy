package org.bibsonomy.webapp.util.spring.security.saml.metadata;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.saml.metadata.MetadataGenerator;
import org.springframework.security.saml.metadata.MetadataGeneratorFilter;

/**
 * @author jensi
 * @version $Id$
 */
public class HttpsMetadataGeneratorFilter extends MetadataGeneratorFilter {

	private boolean useHttps;

	/**
	 * delegates to parent class
	 * @param generator
	 */
	public HttpsMetadataGeneratorFilter(MetadataGenerator generator) {
		super(generator);
	}
	
	@Override
	protected String getDefaultBaseURL(HttpServletRequest request) {
		String s = super.getDefaultBaseURL(request);
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

}
