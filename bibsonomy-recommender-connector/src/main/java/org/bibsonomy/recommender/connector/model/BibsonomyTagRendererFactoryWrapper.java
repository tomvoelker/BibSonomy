/**
 * BibSonomy-Recommendation-Connector - Connector for the recommender framework for tag and resource recommendation
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.recommender.connector.model;

import java.io.Reader;
import java.io.Writer;
import java.util.SortedSet;

import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.recommender.connector.utilities.RecommendationUtilities;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.RendererFactory;
import org.bibsonomy.rest.renderer.RenderingFormat;

import recommender.core.error.BadRequestOrResponseException;
import recommender.core.interfaces.model.TagRecommendationEntity;
import recommender.core.interfaces.renderer.RecommendationRenderer;

/**
 * TODO: remove bibsonomy from name
 * This class provides access to the BibSonomy renderer for webservice tag recommendations.
 * 
 * @author lukas
 *
 */
public class BibsonomyTagRendererFactoryWrapper implements RecommendationRenderer<TagRecommendationEntity, recommender.impl.model.RecommendedTag> {

	private Renderer renderer;
	
	public BibsonomyTagRendererFactoryWrapper(final RendererFactory factory) {
		this.renderer = factory.getRenderer(RenderingFormat.XML);
	}
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.renderer.RecommendationRenderer#serializeRecommendationEntity(java.io.Writer, recommender.core.interfaces.model.RecommendationEntity)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void serializeRecommendationEntity(final Writer writer, final TagRecommendationEntity post) {
		if(post instanceof PostWrapper<?>) {
			this.renderer.serializePost(writer, ((PostWrapper) post).getPost(), new ViewModel());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.renderer.RecommendationRenderer#parseRecommendationResultList(java.io.Reader)
	 */
	@Override
	public SortedSet<recommender.impl.model.RecommendedTag> parseRecommendationResultList(final Reader reader)
			throws BadRequestOrResponseException {
		return RecommendationUtilities.getRecommendedTagsFromBibRecTags(this.renderer.parseRecommendedTagList(reader));
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.renderer.RecommendationRenderer#parseRecommendationResult(java.io.Reader)
	 */
	@Override
	public recommender.impl.model.RecommendedTag parseRecommendationResult(final Reader reader)
			throws BadRequestOrResponseException {
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
	
	public void setRenderer(final Renderer renderer) {
		this.renderer = renderer;
	}
}
