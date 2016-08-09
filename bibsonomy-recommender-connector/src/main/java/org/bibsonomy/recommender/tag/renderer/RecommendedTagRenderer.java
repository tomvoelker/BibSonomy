package org.bibsonomy.recommender.tag.renderer;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.recommender.renderer.json.JSONRecommendationRenderer;
import org.bibsonomy.recommender.tag.model.RecommendedTag;

import recommender.core.interfaces.renderer.RecommendationRenderer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * {@link RecommendationRenderer} for {@link RecommendedTag}
 *
 * @author lha, dzo
 */
public class RecommendedTagRenderer extends JSONRecommendationRenderer<Post<? extends Resource>, RecommendedTag> {
	
	@Override
	protected ObjectMapper createObjectMapper() {
		final ObjectMapper createObjectMapper = super.createObjectMapper();
		final SimpleModule module = new SimpleModule();
		createObjectMapper.addMixInAnnotations(User.class, UserConfig.class);
		createObjectMapper.addMixInAnnotations(RecommendedTag.class, RecommendedTagConfig.class);
		createObjectMapper.addMixInAnnotations(Resource.class, ResourceConfig.class);
		createObjectMapper.addMixInAnnotations(Post.class, PostConfig.class);
		createObjectMapper.addMixInAnnotations(BibTex.class, PublicationConfig.class);
		createObjectMapper.registerModule(module);
		return createObjectMapper.copy();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.renderer.json.JSONRecommendationRenderer#getEntityClass()
	 */
	@Override
	protected Class<?> getEntityClass() {
		return Post.class;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.renderer.json.JSONRecommendationRenderer#getRecommendationResultClass()
	 */
	@Override
	protected Class<?> getRecommendationResultClass() {
		return RecommendedTag.class;
	}
	
	@JsonIgnoreProperties({ "scraperId", "miscFieldParsed", "simHash0", "simHash1", "simHash2"})
	private abstract class PublicationConfig extends ResourceConfig {
		// noop
	}

	@JsonIgnoreProperties({ "ranking", "picked", "inboxPost" })
	private abstract class PostConfig {
		// noop
	}
	
	@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
	private abstract class RecommendedTagConfig {
		
		@JsonProperty
		public abstract String getName();
		
		@JsonProperty
		public abstract double getConfidence();
		
		@JsonProperty
		public abstract double getScore();
	}
	
	@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
	private abstract class UserConfig {
		
		@JsonProperty
		public abstract String getName();
	}
	
	@JsonTypeInfo(use=Id.CLASS, include=As.PROPERTY, property="class")
	@JsonIgnoreProperties("count")
	private abstract class ResourceConfig {
		// noop
	}


}
