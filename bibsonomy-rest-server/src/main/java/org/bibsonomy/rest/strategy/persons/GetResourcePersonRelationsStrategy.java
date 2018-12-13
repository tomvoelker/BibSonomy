package org.bibsonomy.rest.strategy.persons;

import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.logic.querybuilder.ResourcePersonRelationQueryBuilder;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.UrlBuilder;

import java.io.Writer;
import java.util.List;

/**
 * strategy to get a list of resource person relations
 *
 * @author dzo, pda
 */
public class GetResourcePersonRelationsStrategy extends AbstractGetListStrategy<List<ResourcePersonRelation>> {

	private String personId;

	/**
	 * default constructor
	 * @param context
	 * @param personId
	 */
	public GetResourcePersonRelationsStrategy(Context context, String personId) {
		super(context);
		this.personId = personId;
	}

	@Override
	protected void render(final Writer writer, final List<ResourcePersonRelation> resultList) {
		this.getRenderer().serializeResourcePersonRelations(writer, resultList);
	}

	@Override
	protected List<ResourcePersonRelation> getList() {
		return this.getLogic().getResourceRelations(new ResourcePersonRelationQueryBuilder().byPersonId(this.personId).withPosts(true).withPersonsOfPosts(true).groupByInterhash(true).orderBy(ResourcePersonRelationQueryBuilder.Order.PublicationYear));
	}

	@Override
	protected UrlBuilder getLinkPrefix() {
		return this.getUrlRenderer().createUrlBuilderForResourcePersonRelations(this.personId);
	}
}
