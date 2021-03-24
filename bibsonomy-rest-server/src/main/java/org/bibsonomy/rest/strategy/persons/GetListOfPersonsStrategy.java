package org.bibsonomy.rest.strategy.persons;

import java.io.Writer;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.logic.query.PersonQuery;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.UrlBuilder;

/**
 * returns a list of persons
 * @author dzo
 */
public class GetListOfPersonsStrategy extends AbstractGetListStrategy<List<Person>> {

	private final String userName;

	/**
	 * @param context
	 */
	public GetListOfPersonsStrategy(Context context) {
		super(context);
		this.userName = context.getStringAttribute(GroupingEntity.USER.toString().toLowerCase(), null);
	}

	@Override
	protected void render(Writer writer, List<Person> persons) {
		this.getRenderer().serializePersons(writer, persons, this.getView());
	}

	@Override
	protected List<Person> getList() {
		final PersonQuery query = new PersonQuery();
		query.setStart(this.getView().getStartValue());
		query.setEnd(this.getView().getEndValue());
		query.setUserName(this.userName);
		return this.getLogic().getPersons(query);
	}

	@Override
	protected UrlBuilder getLinkPrefix() {
		return this.getUrlRenderer().createUrlBuilderForPersons();
	}
}
