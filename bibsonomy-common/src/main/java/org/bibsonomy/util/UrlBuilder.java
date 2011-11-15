package org.bibsonomy.util;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author dzo
 * @version $Id$
 */
public class UrlBuilder {
	private final String baseUrl;
	private Map<String, String> parameters;
	
	/**
	 * @param baseUrl
	 */
	public UrlBuilder(final String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	/**
	 * @param key
	 * @param value
	 * @return the builder
	 */
	public UrlBuilder addParameter(final String key, final String value) {
		if (present(key) && present(value)) {
			if (this.parameters == null) {
				this.parameters = new HashMap<String, String>();
			}
			this.parameters.put(key, UrlUtils.safeURIEncode(value));
		}
		
		return this;
	}
	
	/**
	 * clears the parameters for reusing the builder
	 * @return the builder
	 */
	public UrlBuilder clearParameters() {
		if (present(this.parameters)) {
			this.parameters.clear();
		}
		
		return this;
	}
	
	/**
	 * @return the url as string
	 */
	public String asString() {
		final StringBuilder url = new StringBuilder(this.baseUrl);
		
		if (present(this.parameters)) {
			url.append("?");
			for (Entry<String, String> param : this.parameters.entrySet()) {
				url.append(param.getKey()).append("=").append(param.getValue());
				url.append("&");
			}
			/*
			 * remove the last &
			 */
			url.substring(0, url.length() - 1);
		}
		
		return url.toString();
	}
}
