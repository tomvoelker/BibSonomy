package org.bibsonomy.model.logic.query;

import org.bibsonomy.model.enums.PersonResourceRelationOrder;
import org.bibsonomy.model.enums.PersonResourceRelationType;

/**
 * A class for specifying queries that yield resource - person relations.
 *
 * @author ada
 */
public class ResourcePersonRelationQuery extends BasicPaginatedQuery {

	/**
	 * A builder for constructing queries.
	 */
	public static class ResourcePersonRelationQueryBuilder {

		private boolean withPersons;
		private boolean withPosts;
		private boolean withPersonsOfPosts;

		private PersonResourceRelationType relationType;

		private String interhash;
		private Integer authorIndex;
		private String personId;
		private PersonResourceRelationOrder order;

		private boolean groupByInterhash;

		private int start;
		private int end;


		public ResourcePersonRelationQuery build() {
			return new ResourcePersonRelationQuery(start, end, withPersons, withPosts, withPersonsOfPosts,
							relationType, interhash, authorIndex, personId, order, groupByInterhash);
		}

		public ResourcePersonRelationQueryBuilder setWithPersons(boolean withPersons) {
			this.withPersons = withPersons;
			return this;
		}

		public ResourcePersonRelationQueryBuilder setWithPosts(boolean withPosts) {
			this.withPosts = withPosts;
			return this;
		}

		public ResourcePersonRelationQueryBuilder setWithPersonsOfPosts(boolean withPersonsOfPosts) {
			this.withPersonsOfPosts = withPersonsOfPosts;
			return this;
		}

		public ResourcePersonRelationQueryBuilder setRelationType(PersonResourceRelationType relationType) {
			this.relationType = relationType;
			return this;
		}

		public ResourcePersonRelationQueryBuilder setInterhash(String interhash) {
			this.interhash = interhash;
			return this;
		}

		public ResourcePersonRelationQueryBuilder setAuthorIndex(Integer authorIndex) {
			this.authorIndex = authorIndex;
			return this;
		}

		public ResourcePersonRelationQueryBuilder setPersonId(String personId) {
			this.personId = personId;
			return this;
		}

		public ResourcePersonRelationQueryBuilder setOrder(PersonResourceRelationOrder order) {
			this.order = order;
			return this;
		}

		public ResourcePersonRelationQueryBuilder setGroupByInterhash(boolean groupByInterhash) {
			this.groupByInterhash = groupByInterhash;
			return this;
		}

		public ResourcePersonRelationQueryBuilder setStart(int start) {
			this.start = start;
			return this;
		}

		public ResourcePersonRelationQueryBuilder setEnd(int end) {
			this.end = end;
			return this;
		}
	}

	private boolean withPersons;
	private boolean withPosts;
	private boolean withPersonsOfPosts;

	private PersonResourceRelationType relationType;

	private String interhash;
	private Integer authorIndex;
	private String personId;
	private PersonResourceRelationOrder order;

	private boolean groupByInterhash;

	public ResourcePersonRelationQuery(int start,
																		 int end,
																		 boolean withPersons,
																		 boolean withPosts,
																		 boolean withPersonsOfPosts,
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
