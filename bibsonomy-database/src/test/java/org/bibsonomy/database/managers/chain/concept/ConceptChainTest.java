package org.bibsonomy.database.managers.chain.concept;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.AbstractChainTest;
import org.bibsonomy.database.managers.chain.concept.get.GetAllConcepts;
import org.bibsonomy.database.managers.chain.concept.get.GetAllConceptsForUser;
import org.bibsonomy.database.managers.chain.concept.get.GetPickedConceptsForUser;
import org.bibsonomy.database.params.TagRelationParam;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class ConceptChainTest extends AbstractChainTest {
	protected static ConceptChain conceptChain;
	
	/**
	 * sets up the chain
	 */
	@BeforeClass
	public static void setUpChain() {
		conceptChain = new ConceptChain();
	}
	
	
	private TagRelationParam tagRelationParam;
	
	/**
	 * 	creates a new tag relation param
	 */
	@Before
	public void createParam() {
		this.tagRelationParam = ParamUtils.getDefaultTagRelationParam();
	}

	/**
	 * tests getAllConcepts
	 */
	@Test
	public void getAllConcepts() {
		this.tagRelationParam.setGrouping(GroupingEntity.ALL);
		this.tagRelationParam.setConceptStatus(ConceptStatus.ALL);
		conceptChain.getFirstElement().perform(this.tagRelationParam, this.dbSession, chainStatus);
		assertEquals(GetAllConcepts.class, chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getAllConceptsForUser
	 */
	@Test
	public void getAllConceptsForUser() {
		this.tagRelationParam.setGrouping(GroupingEntity.USER);
		this.tagRelationParam.setConceptStatus(ConceptStatus.ALL);
		this.tagRelationParam.setRequestedUserName("testuser1");
		conceptChain.getFirstElement().perform(this.tagRelationParam, this.dbSession, chainStatus);
		assertEquals(GetAllConceptsForUser.class, chainStatus.getChainElement().getClass());
	}

	/**
	 * tests getPickedConceptsForUser
	 */
	@Test
	public void getPickedConceptsForUser() {
		this.tagRelationParam.setGrouping(GroupingEntity.USER);
		this.tagRelationParam.setConceptStatus(ConceptStatus.PICKED);
		this.tagRelationParam.setRequestedUserName("testuser1");
		conceptChain.getFirstElement().perform(this.tagRelationParam, this.dbSession, chainStatus);
		assertEquals(GetPickedConceptsForUser.class, chainStatus.getChainElement().getClass());
	}
}