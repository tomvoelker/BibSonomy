package org.bibsonomy.webapp.util.spring.security.saml.util;

import org.opensaml.common.binding.decoding.URIComparator;
import org.opensaml.xml.parse.ParserPool;

/**
 * adapter for the {@link HTTPArtifactDecoderImpl} to add the correct {@link URIComparator}
 * @author dzo
 */
@Deprecated // remove when updating the saml spring security system
public class HTTPPAOS11Decoder extends org.opensaml.liberty.binding.decoding.HTTPPAOS11Decoder {

	/**
	 * @param comparator
	 */
	public HTTPPAOS11Decoder(final URIComparator comparator) {
		super();
		this.setURIComparator(comparator);
	}

	/**
	 *
	 * @param pool
	 * @param comparator
	 */
	public HTTPPAOS11Decoder(final ParserPool pool, final URIComparator comparator) {
		super(pool);

		this.setURIComparator(comparator);
	}
}
