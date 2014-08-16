package org.bibsonomy.recommender.connector.model;

import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.SortedSet;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.util.data.NoDataAccessor;
import org.bibsonomy.recommender.connector.utilities.RecommendationUtilities;
import org.bibsonomy.recommender.tag.model.RecommendedTag;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.RendererFactory;
import org.bibsonomy.rest.renderer.RenderingFormat;

import recommender.core.error.BadRequestOrResponseException;
import recommender.core.interfaces.renderer.RecommendationRenderer;

/**
 * TODO: remove bibsonomy from name
 * This class provides access to the BibSonomy renderer for webservice tag recommendations.
 * 
 * @author lukas
 *
 */
public class BibsonomyTagRendererFactoryWrapper implements RecommendationRenderer<Post<? extends Resource>, recommender.impl.model.RecommendedTag> {
	private static final RenderingFormat RENDERING_FORMAT = RenderingFormat.XML;
	
	private Renderer renderer;
	
	public BibsonomyTagRendererFactoryWrapper(final RendererFactory factory) {
		this.renderer = factory.getRenderer(RENDERING_FORMAT);
	}
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.renderer.RecommendationRenderer#serializeRecommendationEntity(java.io.Writer, recommender.core.interfaces.model.RecommendationEntity)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void serializeRecommendationEntity(final Writer writer, final TagRecommendationEntity post) {
		if (post instanceof PostWrapper<?>) {
			this.renderer.serializePost(writer, ((PostWrapper) post).getPost(), new ViewModel());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.renderer.RecommendationRenderer#parseRecommendationResultList(java.io.Reader)
	 */
	@Override
	public SortedSet<RecommendedTag> parseRecommendationResultList(final Reader reader) throws BadRequestOrResponseException {
		return RecommendationUtilities.getRecommendedTagsFromBibRecTags(this.renderer.parseRecommendedTagList(reader));
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.renderer.RecommendationRenderer#parseRecommendationResult(java.io.Reader)
	 */
	@Override
	public recommender.impl.model.RecommendedTag parseRecommendationResult(final Reader reader) throws BadRequestOrResponseException {
		final RecommendedTag result = this.renderer.parseRecommendedTag(reader);
		return new recommender.impl.model.RecommendedTag(result.getName(), result.getScore(), result.getConfidence());
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.renderer.RecommendationRenderer#parseStat(java.io.Reader)
	 */
	@Override
	public String parseStat(final Reader reader) throws BadRequestOrResponseException {
		return this.renderer.parseStat(reader);
	}

	/* (non-Javadoc)
	 * @see recommender.core.interfaces.renderer.RecommendationRenderer#getContentType()
	 */
	@Override
	public String getContentType() {
		return RENDERING_FORMAT.getMimeType();
	}
	
	/* (non-Javadoc)
	 * @see recommender.core.interfaces.renderer.RecommendationRenderer#parseRecommendationEntity(java.io.Reader)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public TagRecommendationEntity parseRecommendationEntity(Reader reader) {
		final Post<? extends Resource> post = this.renderer.parsePost(reader, new NoDataAccessor());
		return new PostWrapper<Resource>((Post<Resource>) post);
	}
	
	/* (non-Javadoc)
	 * @see recommender.core.interfaces.renderer.RecommendationRenderer#serializeOK(java.io.Writer)
	 */
	@Override
	public void serializeOK(Writer writer) {
		this.renderer.serializeOK(writer);
	}
	
	/* (non-Javadoc)
	 * @see recommender.core.interfaces.renderer.RecommendationRenderer#serializeRecommendationResultList(java.io.Writer, java.util.SortedSet)
	 */
	@Override
	public void serializeRecommendationResultList(Writer writer, SortedSet<recommender.impl.model.RecommendedTag> recommendations) {
		Collection<RecommendedTag> tags = new LinkedList<RecommendedTag>();
		
		for (recommender.impl.model.RecommendedTag recommendedTag : recommendations) {
			tags.add(new RecommendedTag(recommendedTag.getName(), recommendedTag.getScore(), recommendedTag.getConfidence()));
		}
		this.renderer.serializeRecommendedTags(writer, tags);
	}
}
