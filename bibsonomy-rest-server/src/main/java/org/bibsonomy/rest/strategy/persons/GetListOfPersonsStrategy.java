package org.bibsonomy.rest.strategy.persons;

import java.io.Writer;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.extra.AdditionalKey;
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
	private final AdditionalKey additionalKey;

	/**
	 * @param context
	 */
	public GetListOfPersonsStrategy(final Context context) {
		super(context);
		this.userName = context.getStringAttribute(GroupingEntity.USER.toString().toLowerCase(), null);
		this.additionalKey = null;
	}

	/**
	 * Strategy constructor for retrieving persons with an additional person key.
	 *
	 * @param context
	 * @param keyName the additional key name
	 * @param keyValue the additional key value
	 */
	public GetListOfPersonsStrategy(final Context context, final String keyName, final String keyValue) {
		super(context);
		this.userName = null;
		this.additionalKey = new AdditionalKey(keyName, keyValue);
	}

	@Override
	protected void render(final Writer writer, final List<Person> persons) {
		this.getRenderer().serializePersons(writer, persons, this.getView());
	}

	@Override
	protected List<Person> getList() {
		final PersonQuery query = new PersonQuery();
		query.setStart(this.getView().getStartValue());
		query.setEnd(this.getView().getEndValue());
		query.setUserName(this.userName);
		query.setAdditionalKey(this.additionalKey);
		return this.getLogic().getPersons(query);
	}

	@Override
	protected UrlBuilder getLinkPrefix() {
		return this.getUrlRenderer().createUrlBuilderForPersons();
	}
}
