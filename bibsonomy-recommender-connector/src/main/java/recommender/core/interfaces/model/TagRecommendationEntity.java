package recommender.core.interfaces.model;

import recommender.core.interfaces.model.RecommendationEntity;
import recommender.core.interfaces.model.RecommendationUser;

/**
 * This interface represents the entity to calculate tags for.
 * 
 * To use the recommendation framework this interface must be implemented accurately
 * to prevent failures during the calculation process.
 * 
 * In most cases this interface is implemented by some kind of resource.
 * 
 * This interface is absolutely mandatory.
 * 
 * @author Lukas
 *
 */
public interface TagRecommendationEntity extends RecommendationEntity {

	/**
	 * this method should return the owner of this entity
	 * 
	 * @return the user
	 */
	public RecommendationUser getUser();
	
	/**
	 * should pass the title of this entity
	 * 
	 * it is used by he @link{SimpleContentBasedTagRecommender}
	 * 
	 * @return the title
	 */
	public String getTitle();
	
	/**
	 * the URL can be null, but if not it should be a valid URL
	 * 
	 * used for example by @link{MetaInfoTagRecommender}
	 * 
	 * @return the URL for this recommendation entity
	 */
	public String getUrl();
	
	/**
	 * give some info about the entity
	 * 
	 * @return the info as string
	 */
	@Override
	public String toString();
	
	
}
