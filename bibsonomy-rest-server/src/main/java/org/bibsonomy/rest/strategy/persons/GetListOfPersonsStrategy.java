package org.bibsonomy.rest.strategy.persons;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.Writer;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.extra.AdditionalKey;
import org.bibsonomy.model.logic.query.PersonQuery;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.UrlBuilder;

/**
 * returns a list of persons
 * @author dzo
 */
public class GetListOfPersonsStrategy extends AbstractGetListStrategy<List<Person>> {

	/**
	 * extracts the additional key from the context
	 * @param context
	 * @return
	 */
	public static AdditionalKey extractAdditionalKey(final Context context) {
		final String additionalKeyStr = context.getStringAttribute(RESTConfig.PERSON_ADDITIONAL_KEY_PARAM, null);
		if (present(additionalKeyStr)) {
			return parseAdditionalKey(additionalKeyStr);
		}
		return null;
	}

	private static AdditionalKey parseAdditionalKey(final String additionalKeyStr) {
		final String[] split = additionalKeyStr.split(RESTConfig.PERSON_ADDITIONAL_KEY_PARAM_SEPARATOR);
		if (split.length != 2) {
			return null;
		}

		return new AdditionalKey(split[0], split[1]);
	}

	private final String userName;
	private final AdditionalKey additionalKey;

	/**
	 * @param context
	 */
	public GetListOfPersonsStrategy(final Context context) {
		super(context);
		this.userName = context.getStringAttribute(GroupingEntity.USER.toString().toLowerCase(), null);
		this.additionalKey = extractAdditionalKey(context);
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
