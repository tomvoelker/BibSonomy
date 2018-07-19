package org.bibsonomy.model.cris;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

import java.util.Date;

/**
 * this class represents the link of a publication to an Linkable object of the CRIS system
 *
 * @author dzo
 */
public class CRISLink {

	/** the publication that is linked */
	private Post<? extends BibTex> publication;

	private Linkable linkable;

	private Date linkDate;

	/**
	 * @return the publication
	 */
	public Post<? extends BibTex> getPublication() {
		return publication;
	}

	/**
	 * @param publication the publication to set
	 */
	public void setPublication(final Post<? extends BibTex> publication) {
		this.publication = publication;
	}

	/**
	 * @return the linkable
	 */
	public Linkable getLinkable() {
		return linkable;
	}

	/**
	 * @param linkable the linkable to set
	 */
	public void setLinkable(Linkable linkable) {
		this.linkable = linkable;
	}

	/**
	 * @return the linkDate
	 */
	public Date getLinkDate() {
		return linkDate;
	}

	/**
	 * @param linkDate the linkDate to set
	 */
	public void setLinkDate(Date linkDate) {
		this.linkDate = linkDate;
	}
}
