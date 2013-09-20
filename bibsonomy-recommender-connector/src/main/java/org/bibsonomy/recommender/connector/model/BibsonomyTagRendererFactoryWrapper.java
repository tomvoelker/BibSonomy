package org.bibsonomy.recommender.connector.model;

import java.io.Reader;
import java.io.Writer;
import java.util.SortedSet;

import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.recommender.connector.utilities.RecommendationUtilities;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.RendererFactory;
import org.bibsonomy.rest.renderer.RenderingFormat;
import org.bibsonomy.rest.renderer.UrlRenderer;

import recommender.core.error.BadRequestOrResponseException;
import recommender.core.interfaces.model.TagRecommendationEntity;
import recommender.core.interfaces.renderer.RecommendationRenderer;

/**
 * This class provides access to the BibSonomy renderer for webservice tag recommendations.
 * 
 * @author lukas
 *
 */
public class BibsonomyTagRendererFactoryWrapper implements RecommendationRenderer<TagRecommendationEntity, recommender.impl.model.RecommendedTag> {

	private Renderer renderer;
	
	public BibsonomyTagRendererFactoryWrapper() {
		this.renderer = new RendererFactory(new UrlRenderer("/api/")).getRenderer(RenderingFormat.XML);
	}
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.renderer.RecommendationRenderer#serializeRecommendationEntity(java.io.Writer, recommender.core.interfaces.model.RecommendationEntity)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void serializeRecommendationEntity(Writer writer,
			TagRecommendationEntity post) {
		if(post instanceof PostWrapper<?>) {
			this.renderer.serializePost(writer, ((PostWrapper) post).getPost(),
					new org.bibsonomy.rest.ViewModel());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.renderer.RecommendationRenderer#parseRecommendationResultList(java.io.Reader)
	 */
	@Override
	public SortedSet<recommender.impl.model.RecommendedTag> parseRecommendationResultList(Reader reader)
			throws BadRequestOrResponseException {
		return RecommendationUtilities.getRecommendedTagsFromBibRecTags(this.renderer.parseRecommendedTagList(reader));
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.renderer.RecommendationRenderer#parseRecommendationResult(java.io.Reader)
	 */
	@Override
	public recommender.impl.model.RecommendedTag parseRecommendationResult(Reader reader)
			throws BadRequestOrResponseException {
		RecommendedTag result = this.renderer.parseRecommendedTag(reader);
		return new recommender.impl.model.RecommendedTag(result.getName(), result.getScore(), result.getConfidence());
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.renderer.RecommendationRenderer#parseStat(java.io.Reader)
	 */
	@Override
	public String parseStat(Reader reader) throws BadRequestOrResponseException {
		return this.renderer.parseStat(reader);
	}
	
	public void setRenderer(Renderer renderer) {
		this.renderer = renderer;
	}
	
}
