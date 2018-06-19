package org.bibsonomy.webapp.util.spring.security.saml.util;

import org.opensaml.common.binding.decoding.BasicURLComparator;

/**
 * This removes the query params of the url before comparing it
 *
 * code copy of org.springframework.security.saml.util.DefaultURLComparator
 * please remove as soon as we updated the spring security saml dependency
 *
 * @author dzo
 */
@Deprecated // code copy, update saml dependency
public class URLComparator extends BasicURLComparator {

	@Override
	public boolean compare(String uri1, String uri2) {
		if (uri2 == null){
			return uri1 == null;
		}
		int queryStringIndex = uri2.indexOf('?');
		if (queryStringIndex >= 0){
			uri2 = uri2.substring(0, queryStringIndex);
		}

		return super.compare(uri1, uri2);
	}
}
