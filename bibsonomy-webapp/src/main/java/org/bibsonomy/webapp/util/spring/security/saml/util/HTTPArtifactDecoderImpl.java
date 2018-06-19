package org.bibsonomy.webapp.util.spring.security.saml.util;

import org.opensaml.common.binding.decoding.URIComparator;
import org.opensaml.xml.parse.ParserPool;
import org.springframework.security.saml.websso.ArtifactResolutionProfile;

/**
 * adapter for the {@link HTTPArtifactDecoderImpl} to add the correct {@link URIComparator}
 * @author dzo
 */
@Deprecated // remove when updating the saml spring security system
public class HTTPArtifactDecoderImpl extends org.opensaml.saml2.binding.decoding.HTTPArtifactDecoderImpl {

	/**
	 * default constructor
	 *
	 * @param resolutionProfile
	 * @param parserPool
	 * @param comparator
	 */
	public HTTPArtifactDecoderImpl(final ArtifactResolutionProfile resolutionProfile, final ParserPool parserPool, final URIComparator comparator) {
		super(resolutionProfile, parserPool);

		this.setURIComparator(comparator);
	}
}
