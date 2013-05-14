package org.bibsonomy.webapp.view.urlbuilder;

/**
 * @author jensi
 * @version $Id$
 */
public class VuFindURLGenerator extends MiscFieldPublicationUrlURLGenerator {
	private String ebscoPublicationUrlPrefix;
	
	@Override
	protected String addPrefix(String miscFieldData) {
		if (miscFieldData.contains("|")) {
			return ebscoPublicationUrlPrefix + miscFieldData;
		}
		return super.addPrefix(miscFieldData);
	}

	/**
	 * @return the ebscoPublicationUrlPrefix
	 */
	public String getEbscoPublicationUrlPrefix() {
		return this.ebscoPublicationUrlPrefix;
	}

	/**
	 * @param ebscoPublicationUrlPrefix the ebscoPublicationUrlPrefix to set
	 */
	public void setEbscoPublicationUrlPrefix(String ebscoPublicationUrlPrefix) {
		this.ebscoPublicationUrlPrefix = ebscoPublicationUrlPrefix;
	}
}
