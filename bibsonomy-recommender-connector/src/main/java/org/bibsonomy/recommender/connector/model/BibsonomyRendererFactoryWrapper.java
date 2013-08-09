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

import recommender.core.interfaces.model.TagRecommendationEntity;
import recommender.core.interfaces.renderer.RecommendationRenderer;
import recommender.core.rest.BadRequestOrResponseException;
import recommender.core.rest.ViewModel;

public class BibsonomyRendererFactoryWrapper implements RecommendationRenderer<TagRecommendationEntity, recommender.core.model.RecommendedTag> {

	private Renderer renderer;
	
	public BibsonomyRendererFactoryWrapper() {
		this.renderer = new RendererFactory(new UrlRenderer("/api/")).getRenderer(RenderingFormat.XML);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void serializeTagRecommendationEntity(Writer writer,
			TagRecommendationEntity post, ViewModel model) {
		if(post instanceof PostWrapper<?>) {
			this.renderer.serializePost(writer, ((PostWrapper) post).getPost(),
					convertRecommendationVMToBibSonomyVM(model));
		}
	}

	@Override
	public SortedSet<recommender.core.model.RecommendedTag> parseRecommendedTagList(Reader reader)
			throws BadRequestOrResponseException {
		return RecommendationUtilities.getRecommendedTagsFromBibRecTags(this.renderer.parseRecommendedTagList(reader));
	}

	@Override
	public recommender.core.model.RecommendedTag parseRecommendedTag(Reader reader)
			throws BadRequestOrResponseException {
		RecommendedTag result = this.renderer.parseRecommendedTag(reader);
		return new recommender.core.model.RecommendedTag(result.getName(), result.getScore(), result.getConfidence());
	}

	@Override
	public String parseStat(Reader reader) throws BadRequestOrResponseException {
		return this.renderer.parseStat(reader);
	}

	private org.bibsonomy.rest.ViewModel convertRecommendationVMToBibSonomyVM(ViewModel convertFrom) {
		org.bibsonomy.rest.ViewModel result = new org.bibsonomy.rest.ViewModel();
		result.setEndValue(convertFrom.getEndValue());
		if (convertFrom.getOrder() != null) {
			result.setOrder(convertFrom.getOrder().name());
		}
		result.setStartValue(convertFrom.getStartValue());
		result.setUrlToNextResources(convertFrom.getUrlToNextResources());
		
		return result;
	}
	
	public void setRenderer(Renderer renderer) {
		this.renderer = renderer;
	}
	
}
