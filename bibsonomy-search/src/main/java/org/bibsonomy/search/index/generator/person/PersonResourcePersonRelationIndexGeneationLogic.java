package org.bibsonomy.search.index.generator.person;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.search.index.generator.OneToManyIndexGenerationLogic;
import org.bibsonomy.search.management.database.params.SearchParam;

import java.util.List;

/**
 * generation logic for a person index with person and person resource relation entities
 *
 * @author dzo
 */
public class PersonResourcePersonRelationIndexGeneationLogic extends PersonIndexGenerationLogic implements OneToManyIndexGenerationLogic<Person, ResourcePersonRelation> {

	@Override
	public List<ResourcePersonRelation> getToManyEntities(int lastContentId, int limit) {
		try (final DBSession session = this.openSession()) {
			final SearchParam param = buildParam(lastContentId, limit);
			return this.queryForList("getResourceRelations", param, ResourcePersonRelation.class, session);
		}
	}

	@Override
	public int getNumberOfToManyEntities() {
		try (final DBSession session = this.openSession()) {
			return this.queryForObject("getPersonRelationsCount", Integer.class, session);
		}
	}
}
