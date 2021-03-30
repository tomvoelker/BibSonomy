package org.bibsonomy.model.logic.query;

import org.bibsonomy.model.enums.PersonResourceRelationOrder;
import org.bibsonomy.model.enums.PersonResourceRelationType;

/**
 * A class for specifying queries that yield resource - person relations.
 *
 * @author ada
 */
public class ResourcePersonRelationQuery extends BasicPaginatedQuery {

	private boolean withPersons;
	private boolean withPosts;
	private boolean withPersonsOfPosts;

	private PersonResourceRelationType relationType;

	private String interhash;
	private Integer authorIndex;
	private String personId;
	private PersonResourceRelationOrder order;

	private boolean groupByInterhash;

	public ResourcePersonRelationQuery(int start, int end, boolean withPersons, boolean withPosts, boolean withPersonsOfPosts,
									 PersonResourceRelationType relationType,
									 String interhash,
									 Integer authorIndex,
									 String personId,
									 PersonResourceRelationOrder order,
									 boolean groupByInterhash) {

		super(start, end);
		this.withPersons = withPersons;
		this.withPosts = withPosts;
		this.withPersonsOfPosts = withPersonsOfPosts;
		this.relationType = relationType;
		this.interhash = interhash;
		this.authorIndex = authorIndex;
		this.personId = personId;
		this.order = order;
		this.groupByInterhash = groupByInterhash;
	}

	public boolean isWithPersons() {
		return withPersons;
	}

	public boolean isWithPosts() {
		return withPosts;
	}

	public boolean isWithPersonsOfPosts() {
		return withPersonsOfPosts;
	}

	public PersonResourceRelationType getRelationType() {
		return relationType;
	}

	public String getInterhash() {
		return interhash;
	}

	public Integer getAuthorIndex() {
		return authorIndex;
	}

	public String getPersonId() {
		return personId;
	}

	public PersonResourceRelationOrder getOrder() {
		return order;
	}

	public boolean isGroupByInterhash() {
		return groupByInterhash;
	}
}
