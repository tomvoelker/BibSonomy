package org.bibsonomy.database.managers.chain.concept;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.AbstractChainTest;
import org.bibsonomy.database.managers.chain.concept.get.GetAllConcepts;
import org.bibsonomy.database.managers.chain.concept.get.GetAllConceptsForUser;
import org.bibsonomy.database.managers.chain.concept.get.GetPickedConceptsForUser;
import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class ConceptChainTest extends AbstractChainTest {

	/**
	 * tests getAllConcepts
	 */
	@Test
	public void getAllConcepts() {
		this.tagRelationParam.setGrouping(GroupingEntity.ALL);
		this.tagRelationParam.setConceptStatus(ConceptStatus.ALL);
		this.conceptChain.getFirstElement().perform(this.tagRelationParam, this.dbSession, this.chainStatus);
		assertEquals(GetAllConcepts.class, this.chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getAllConceptsForUser
	 */
	@Test
	public void getAllConceptsForUser() {
		this.tagRelationParam.setGrouping(GroupingEntity.USER);
		this.tagRelationParam.setConceptStatus(ConceptStatus.ALL);
		this.tagRelationParam.setRequestedUserName("testuser1");
		this.conceptChain.getFirstElement().perform(this.tagRelationParam, this.dbSession, this.chainStatus);
		assertEquals(GetAllConceptsForUser.class, this.chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getPickedConceptsForUser
	 */
	@Test
	public void getPickedConceptsForUser() {
		this.tagRelationParam.setGrouping(GroupingEntity.USER);
		this.tagRelationParam.setConceptStatus(ConceptStatus.PICKED);
		this.tagRelationParam.setRequestedUserName("testuser1");
		this.conceptChain.getFirstElement().perform(this.tagRelationParam, this.dbSession, this.chainStatus);
		assertEquals(GetPickedConceptsForUser.class, this.chainStatus.getChainElement().getClass());
	}
}