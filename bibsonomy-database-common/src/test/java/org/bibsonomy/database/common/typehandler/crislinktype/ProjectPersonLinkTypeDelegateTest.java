package org.bibsonomy.database.common.typehandler.crislinktype;

import static org.junit.Assert.assertNotNull;

import org.bibsonomy.model.cris.ProjectPersonLinkType;
import org.junit.Test;

/**
 * tests for {@link ProjectPersonLinkTypeDelegate}
 * @author dzo
 */
public class ProjectPersonLinkTypeDelegateTest {

	/**
	 * ensures that all velues of {@link ProjectPersonLinkType} values are covered by the typehandercallback
	 */
	@Test
	public void testLookupMap() {
		for (ProjectPersonLinkType type : ProjectPersonLinkType.values()) {
			assertNotNull(ProjectPersonLinkTypeDelegate.LOOKUP_MAP.get(type));
		}
	}
}