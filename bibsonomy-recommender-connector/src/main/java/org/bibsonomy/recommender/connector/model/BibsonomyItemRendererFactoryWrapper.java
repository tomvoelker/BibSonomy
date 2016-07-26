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

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bibsonomy.model.RecommendedPost;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.util.data.NoDataAccessor;
import org.bibsonomy.recommender.connector.database.ExtendedMainAccess;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.RendererFactory;
import org.bibsonomy.rest.renderer.RenderingFormat;

import recommender.core.error.BadRequestOrResponseException;
import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendationItem;
import recommender.core.interfaces.renderer.RecommendationRenderer;
import recommender.core.util.RecommendationResultComparator;
import recommender.impl.model.RecommendedItem;

/**
 * TODO: remove bibsonomy from name
 * This class allows to trigger remote item recommendations on external services 
 * and integrate the results into the multiplexing recommendation process.
 * 
 * @author lukas
 *
 * @param <T> The resource type to which recommendations should fit.
 * 			  Recommendations which do not match this type are ignored.
 */
public class BibsonomyItemRendererFactoryWrapper<T extends Resource> implements
		RecommendationRenderer<ItemRecommendationEntity, RecommendedItem> {

	private Renderer renderer;
	private ExtendedMainAccess dbAccess;
	
	public BibsonomyItemRendererFactoryWrapper(final RendererFactory factory) {
		this.renderer = factory.getRenderer(RenderingFormat.XML);
	}
	
	private Class<T> resourceType;
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.renderer.RecommendationRenderer#serializeRecommendationEntity(java.io.Writer, recommender.core.interfaces.model.RecommendationEntity)
	 */
	@Override
	public void serializeRecommendationEntity(final Writer writer,
			final ItemRecommendationEntity entity) {
		if(entity instanceof UserWrapper) {
			this.renderer.serializeUser(writer, ((UserWrapper) entity).getUser(), new org.bibsonomy.rest.ViewModel());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.renderer.RecommendationRenderer#parseRecommendationResultList(java.io.Reader)
	 */
	@Override
	public SortedSet<RecommendedItem> parseRecommendationResultList(
			final Reader reader) throws BadRequestOrResponseException {
		final List<RecommendedPost<? extends Resource>> posts = this.renderer.parseRecommendedItemList(reader, NoDataAccessor.getInstance());
		final SortedSet<RecommendedItem> items = new TreeSet<RecommendedItem>(new RecommendationResultComparator<RecommendedItem>());
		for(final RecommendedPost<? extends Resource> post : posts) {
			final RecommendedItem item = this.createRecommendedItem(post);
			if(present(item)) {
				items.add(this.createRecommendedItem(post));
			}
		}
		return items;
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.renderer.RecommendationRenderer#parseRecommendationResult(java.io.Reader)
	 */
	@Override
	public RecommendedItem parseRecommendationResult(final Reader reader)
			throws BadRequestOrResponseException {
		final RecommendedPost<? extends Resource> post = this.renderer.parseRecommendedItem(reader, NoDataAccessor.getInstance());
		return createRecommendedItem(post);
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.renderer.RecommendationRenderer#parseStat(java.io.Reader)
	 */
	@Override
	public String parseStat(final Reader reader) throws BadRequestOrResponseException {
		return this.renderer.parseStat(reader);
	}
	
	/**
	 * Creates a {@link RecommendedItem} out of a {@link RecommendedPost}.
	 * If the post's resource did not fit to the specified resourceType
	 * null is returned.
	 * 
	 * @param post the post to convert
	 * @return the {@link RecommendedItem} or null if the post had a wrong type
	 */
	@SuppressWarnings("unchecked")
	private RecommendedItem createRecommendedItem(final RecommendedPost<? extends Resource> post) {
		// ignore posts which do not belong to the specified resourceType 
		if(resourceType.isAssignableFrom(post.getPost().getResource().getClass())) {
			final RecommendedPost<T> casted = (RecommendedPost<T>) post;
			final RecommendationItem validatedPost = this.validatePost(casted);
			// was the post valid?
			if(present(validatedPost)) {
				final RecommendedItem item = new RecommendedItem(validatedPost);
				item.setScore(casted.getScore());
				item.setConfidence(casted.getConfidence());
				return item;
			}
		}
		return null;
	}
	
	/**
	 * Checks whether the post is contained in BibSonomy's database 
	 * and if the attributes of the post are consistent to BibSonomy's model. 
	 * 
	 * @param post the post to validate or null if the post is not valid
	 */
	private RecommendationItem validatePost(final RecommendedPost<T> post) {
		RecommendationItem validated = null;
		if(present(post.getPost()) && present(post.getPost().getResource())) {
			// first check if the post is saved in our database like we fetched it
			if(present(post.getPost().getResource().getIntraHash()) && present(post.getPost().getUser().getName())) {
				// username should be the id, because we don't provide real usernames for external services
				validated = this.dbAccess.getItemByUserIdWithHash(post.getPost().getResource().getIntraHash(), post.getPost().getUser().getName());
				if(present(validated)) {
					return validated;
				}
			}
			// we have no exact matching post in our database, so try to find a public resource with the same title
			if(present(post.getPost().getResource().getTitle())) {
				// FIXME: This is not fast enough! Index for title is not present in mysql. Maybe add one?
				//validated = this.dbAccess.getItemByTitle(post.getPost().getResource().getTitle());
			}
		}
		// return the validated item or null, if no valid item could be found
		return validated;
	}

	public void setResourceType(final Class<T> resourceType) {
		this.resourceType = resourceType;
	}
	
	public void setRenderer(final Renderer renderer) {
		this.renderer = renderer;
	}
	
	public void setDbAccess(final ExtendedMainAccess dbAccess) {
		this.dbAccess = dbAccess;
	}

}
