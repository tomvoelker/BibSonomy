package org.bibsonomy.webapp.view.urlbuilder;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.User;
import org.bibsonomy.services.URLGenerator;

/**
 * Overwrites {@link #getPublicationUrl(BibTex, User)} to allow flexible biburl attribute writing during bibtex export
 * 
 * @author jensi
 * @version $Id$
 */
public class MiscFieldPublicationUrlURLGenerator extends URLGenerator {

	private String publicationUrlPrefix;
	private String miscField;

	@Override
	public String getPublicationUrl(BibTex publication, User user) {
		if ("".equals(miscField)) {
			return super.getPublicationUrl(publication, user);
		} 
		return addPrefix(getMiscFieldData(publication));	
	}

	protected String addPrefix(String miscFieldData) {
		return publicationUrlPrefix + miscFieldData;
	}

	private String getMiscFieldData(BibTex publication) {
		String rVal = publication.getMiscField(miscField);
		if (rVal == null) {
			return "";
		}
		return rVal;
	}

	/**
	 * @return the prefix of the biburl (full url except miscField data)
	 */
	public String getPublicationUrlPrefix() {
		return this.publicationUrlPrefix;
	}

	/**
	 * @param publicationUrlPrefix the prefix of the biburl (full url except miscField data)
	 */
	public void setPublicationUrlPrefix(String publicationUrlPrefix) {
		this.publicationUrlPrefix = publicationUrlPrefix;
	}

	/**
	 * @return the name of a bibtex misc field containing data to be appended to the publicationUrlPrefix
	 */
	public String getMiscField() {
		return this.miscField;
	}

	/**
	 * @param miscField the name of a bibtex misc field containing data to be appended to the publicationUrlPrefix
	 */
	public void setMiscField(String miscField) {
		this.miscField = miscField;
	}
	
}
