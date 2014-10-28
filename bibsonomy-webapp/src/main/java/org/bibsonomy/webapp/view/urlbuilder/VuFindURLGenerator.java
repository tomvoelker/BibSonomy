package org.bibsonomy.webapp.view.urlbuilder;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.services.URLGenerator;

/**
 * @author jensi
 */
public class VuFindURLGenerator extends URLGenerator {
	private String pumaUrlMiscFieldName = "pumaurl";
	private String vuFindUrlMiscFieldName;
	private String ebscoVuFindUrlPrefix;
	private String vuFindUrlPrefix;

	private String getHebisUrlFromUniquId(final String uniqueId) {
		if (uniqueId.contains("|")) {
			return ebscoVuFindUrlPrefix + uniqueId;
		}
		return vuFindUrlPrefix + uniqueId;
	}

	@Override
	public void setBibtexMiscUrls(final Post<BibTex> post) {
		if (pumaUrlMiscFieldName != null) {
			if (present(post.getUser()) && present(post.getUser().getName())) {
				post.getResource().addMiscField(pumaUrlMiscFieldName, getPublicationUrl(post.getResource(), post.getUser()).toString());
			}
		}
		if (vuFindUrlMiscFieldName != null) {
			final String uniqueId = post.getResource().getMiscField("uniqueid");
			if (uniqueId != null) {
				post.getResource().addMiscField(vuFindUrlMiscFieldName, getHebisUrlFromUniquId(uniqueId));
			}
		}
	}

	/**
	 * @return the vuFindUrlMiscFieldName
	 */
	public String getVuFindUrlMiscFieldName() {
		return this.vuFindUrlMiscFieldName;
	}

	/**
	 * @param vuFindUrlMiscFieldName the vuFindUrlMiscFieldName to set
	 */
	public void setVuFindUrlMiscFieldName(String vuFindUrlMiscFieldName) {
		this.vuFindUrlMiscFieldName = vuFindUrlMiscFieldName;
	}

	/**
	 * @return the ebscoHebisUrlPrefix
	 */
	public String getEbscoVuFindUrlPrefix() {
		return this.ebscoVuFindUrlPrefix;
	}

	/**
	 * @param ebscoHebisUrlPrefix the ebscoHebisUrlPrefix to set
	 */
	public void setEbscoVuFindUrlPrefix(String ebscoHebisUrlPrefix) {
		this.ebscoVuFindUrlPrefix = ebscoHebisUrlPrefix;
	}

	/**
	 * @return the vuFindUrlPrefix
	 */
	public String getVuFindUrlPrefix() {
		return this.vuFindUrlPrefix;
	}

	/**
	 * @param vuFindUrlPrefix the vuFindUrlPrefix to set
	 */
	public void setVuFindUrlPrefix(String vuFindUrlPrefix) {
		this.vuFindUrlPrefix = vuFindUrlPrefix;
	}

	/**
	 * @return the pumaUrlMiscFieldName
	 */
	public String getPumaUrlMiscFieldName() {
		return this.pumaUrlMiscFieldName;
	}

	/**
	 * @param pumaUrlMiscFieldName the pumaUrlMiscFieldName to set
	 */
	public void setPumaUrlMiscFieldName(String pumaUrlMiscFieldName) {
		this.pumaUrlMiscFieldName = pumaUrlMiscFieldName;
	}
}
