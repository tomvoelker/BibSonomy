package org.bibsonomy.database.managers.chain.concept;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.chain.Chain;
import org.bibsonomy.database.managers.chain.concept.get.GetAllConcepts;
import org.bibsonomy.database.managers.chain.concept.get.GetAllConceptsForUser;
import org.bibsonomy.database.managers.chain.concept.get.GetPickedConceptsForUser;
import org.bibsonomy.database.params.TagRelationParam;
import org.bibsonomy.model.Tag;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class ConceptChainTest extends AbstractDatabaseManagerTest {
	protected static Chain<List<Tag>, TagRelationParam> conceptChain;
	
	/**
	 * sets up the chain
	 */
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUpChain() {
		conceptChain = (Chain<List<Tag>, TagRelationParam>) testDatabaseContext.getBean("conceptChain");
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
		assertEquals(GetAllConcepts.class, conceptChain.getChainElement(this.tagRelationParam).getClass());
	}

	/**
	 * tests getAllConceptsForUser
	 */
	@Test
	public void getAllConceptsForUser() {
		this.tagRelationParam.setGrouping(GroupingEntity.USER);
		this.tagRelationParam.setConceptStatus(ConceptStatus.ALL);
		this.tagRelationParam.setRequestedUserName("testuser1");
		assertEquals(GetAllConceptsForUser.class, conceptChain.getChainElement(this.tagRelationParam).getClass());
	}

	/**
	 * tests getPickedConceptsForUser
	 */
	@Test
	public void getPickedConceptsForUser() {
		this.tagRelationParam.setGrouping(GroupingEntity.USER);
		this.tagRelationParam.setConceptStatus(ConceptStatus.PICKED);
		this.tagRelationParam.setRequestedUserName("testuser1");
		assertEquals(GetPickedConceptsForUser.class, conceptChain.getChainElement(this.tagRelationParam).getClass());
	}
}