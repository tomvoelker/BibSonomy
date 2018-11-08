package org.bibsonomy.services.searcher.query;

import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.query.PostQuery;

import java.util.List;

/**
 * adds search specific query fields to the post query
 *
 * FIXME: when moving the system tags in the model module this class can be removed by changing the type of tags
 *
 * @author dzo
 * @param <R>
 */
public class PostSearchQuery<R extends Resource> extends PostQuery<R> {

	/**
	 * default constructor
	 *
	 * @param resourceClass
	 */
	public PostSearchQuery(Class<R> resourceClass) {
		super(resourceClass);
	}

	private List<String> requestedRelationNames;
	
	/** extracted from the system tag title */
	private String titleSearchTerms;
	
	/** extracted from the system tag author */
	private String authorSearchTerms;
	
	/** extracted from the system tag not */
	private List<String> negatedTags;
	
	private String year;
	
	private String firstYear;

	private String lastYear;

	private String bibtexKey;

	/**
	 * @return the requestedRelationNames
	 */
	public List<String> getRequestedRelationNames() {
		return requestedRelationNames;
	}

	/**
	 * @param requestedRelationNames the requestedRelationNames to set
	 */
	public void setRequestedRelationNames(List<String> requestedRelationNames) {
		this.requestedRelationNames = requestedRelationNames;
	}

	/**
	 * @return the titleSearchTerms
	 */
	public String getTitleSearchTerms() {
		return titleSearchTerms;
	}

	/**
	 * @param titleSearchTerms the titleSearchTerms to set
	 */
	public void setTitleSearchTerms(String titleSearchTerms) {
		this.titleSearchTerms = titleSearchTerms;
	}

	/**
	 * @return the authorSearchTerms
	 */
	public String getAuthorSearchTerms() {
		return authorSearchTerms;
	}

	/**
	 * @param authorSearchTerms the authorSearchTerms to set
	 */
	public void setAuthorSearchTerms(String authorSearchTerms) {
		this.authorSearchTerms = authorSearchTerms;
	}

	/**
	 * @return the negatedTags
	 */
	public List<String> getNegatedTags() {
		return negatedTags;
	}

	/**
	 * @param negatedTags the negatedTags to set
	 */
	public void setNegatedTags(List<String> negatedTags) {
		this.negatedTags = negatedTags;
	}

	/**
	 * @return the year
	 */
	public String getYear() {
		return year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(String year) {
		this.year = year;
	}

	/**
	 * @return the firstYear
	 */
	public String getFirstYear() {
		return firstYear;
	}

	/**
	 * @param firstYear the firstYear to set
	 */
	public void setFirstYear(String firstYear) {
		this.firstYear = firstYear;
	}

	/**
	 * @return the lastYear
	 */
	public String getLastYear() {
		return lastYear;
	}

	/**
	 * @param lastYear the lastYear to set
	 */
	public void setLastYear(String lastYear) {
		this.lastYear = lastYear;
	}

	/**
	 * @return the bibtexKey
	 */
	public String getBibtexKey() {
		return bibtexKey;
	}

	/**
	 * @param bibtexKey the bibtexKey to set
	 */
	public void setBibtexKey(String bibtexKey) {
		this.bibtexKey = bibtexKey;
	}
}
