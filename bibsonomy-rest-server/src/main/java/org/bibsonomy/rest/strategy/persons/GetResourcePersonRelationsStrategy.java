package org.bibsonomy.rest.strategy.persons;

import java.io.Writer;
import java.util.List;

import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonResourceRelationOrder;
import org.bibsonomy.model.logic.querybuilder.ResourcePersonRelationQueryBuilder;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.UrlBuilder;

/**
 * strategy to get a list of resource person relations
 *
 * @author dzo, pda
 */
public class GetResourcePersonRelationsStrategy extends AbstractGetListStrategy<List<ResourcePersonRelation>> {

	private final String personId;

	/**
	 * default constructor
	 * @param context
	 * @param personId
	 */
	public GetResourcePersonRelationsStrategy(final Context context, final String personId) {
		super(context);
		this.personId = personId;
	}

	@Override
	protected void render(final Writer writer, final List<ResourcePersonRelation> resultList) {
		this.getRenderer().serializeResourcePersonRelations(writer, resultList);
	}

	@Override
	protected List<ResourcePersonRelation> getList() {
		final ResourcePersonRelationQueryBuilder queryBuilder = new ResourcePersonRelationQueryBuilder()
						.byPersonId(this.personId)
						.withPosts(true)
						.withPersonsOfPosts(true)
						.groupByInterhash(true)
						.orderBy(PersonResourceRelationOrder.PublicationYear);
		return this.getLogic().getResourceRelations(queryBuilder.build());
	}

	@Override
	protected UrlBuilder getLinkPrefix() {
		return this.getUrlRenderer().createUrlBuilderForResourcePersonRelations(this.personId);
	}
}
