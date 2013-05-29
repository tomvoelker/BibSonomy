
package org.bibsonomy.recommender.connector.model;

import org.bibsonomy.model.BibTex;
import recommender.core.interfaces.model.RecommendationResource;

public class BibTexWrapper implements RecommendationResource {

	/**
	 * for serialization
	 */
	private static final long serialVersionUID = -8716576273787930613L;
	
	private BibTex bibtex;
	
	
	public BibTexWrapper(BibTex bibtex) {
		this.bibtex = bibtex;
	}
	
	public BibTex getBibtex() {
		return bibtex;
	}
	
	public void setBibtex(BibTex bibtex) {
		this.bibtex = bibtex;
	}

	@Override
	public String getInterHash() {
		return this.bibtex.getInterHash();
	}

	@Override
	public void setInterHash(String interHash) {
		this.bibtex.setInterHash(interHash);
	}

	@Override
	public String getIntraHash() {
		return this.bibtex.getIntraHash();
	}

	@Override
	public void setIntraHash(String intraHash) {
		this.bibtex.setIntraHash(intraHash);
	}

	@Override
	public int getCount() {
		return this.bibtex.getCount();
	}

	@Override
	public void setCount(int count) {
		this.bibtex.setCount(count);
	}

	@Override
	public String getTitle() {
		return this.bibtex.getTitle();
	}

	@Override
	public void setTitle(String title) {
		this.bibtex.setTitle(title);
	}

	@Override
	public String getUrl() {
		return this.bibtex.getUrl();
	}

	@Override
	public void setUrl(String url) {
		this.bibtex.setUrl(url);
	}

	@Override
	public void recalculateHashes() {
		this.bibtex.recalculateHashes();
	}
	
}