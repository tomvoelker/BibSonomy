package recommender.impl.model;

import recommender.core.interfaces.model.AbstractRecommendationResult;
import recommender.core.interfaces.model.RecommendationResult;

/**
 * Internal implementation of a recommended tag.
 * 
 * @author lukas
 */
public class RecommendedTag extends AbstractRecommendationResult {
	
	private String name;
	
	/**
	 * default constructor
	 */
	public RecommendedTag() {
		// noop
	}
	
	public RecommendedTag(String name, double score, double confidence) {
		super(score, confidence);
		this.name = name;
	}
	
	/**
	 * @return the tagname
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the tagname to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.model.RecommendationResult#getId()
	 */
	@Override
	public String getRecommendationId() {
		return this.name;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (!(o instanceof RecommendedTag)) {
			return false;
		}
		final RecommendedTag other = (RecommendedTag) o;
		if (this.name == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!this.name.toLowerCase().equals(((RecommendedTag) o).getName().toLowerCase())) {
			return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see recommender.core.interfaces.model.RecommendationResult#compareToOtherRecommendationResult(recommender.core.interfaces.model.RecommendationResult)
	 */
	@Override
	public int compareToOtherRecommendationResult(RecommendationResult o) {
		if( o instanceof RecommendedTag ) {
			return this.name.toLowerCase().compareTo(((RecommendedTag) o).getName().toLowerCase());
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.model.RecommendationResult#getTitle()
	 */
	@Override
	public String getTitle() {
		return name;
	}

}
