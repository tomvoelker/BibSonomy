package org.bibsonomy.database.util.tag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bibsonomy.model.Tag;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jens Illig
 * @version $Id$
 */
public class OperatorParserTest {

	private OperatorParser parser;

	@Before
	public void setUp() {
		this.parser = new OperatorParser();
		final Map<String, TagOperator> ops = new HashMap<String, TagOperator>();
		final ConceptTagOperator leftToRightConceptOp = new ConceptTagOperator();
		leftToRightConceptOp.setLeftToRight(true);
		final ConceptTagOperator rightToLeftConceptOp = new ConceptTagOperator();
		rightToLeftConceptOp.setLeftToRight(false);
		ops.put("->", leftToRightConceptOp);
		ops.put("<-", rightToLeftConceptOp);
		this.parser.setOperators(ops);
	}

	@Test
	public void testSimpleParse() {
		final Tag tag = this.parser.parse("hurz <- bla");
		assertEquals("hurz", tag.getName());
		assertNotNull(tag.getSubTags());
		assertEquals(1, tag.getSubTags().size());

		final Tag subTag = tag.getSubTags().get(0);
		assertEquals("bla", subTag.getName());
		assertNotNull(subTag.getSuperTags());
		assertEquals(1, subTag.getSuperTags().size());
		assertEquals(tag, subTag.getSuperTags().get(0));
	}

	@Test
	public void testChainParse() {
		final Tag tag = this.parser.parse(" hurz<-bla<- \t trallalla->X  ");
		assertEquals("hurz", tag.getName());
		assertNotNull(tag.getSubTags());
		assertEquals(1, tag.getSubTags().size());

		final Tag subTag = tag.getSubTags().get(0);
		assertEquals("bla", subTag.getName());
		assertNotNull(subTag.getSuperTags());
		assertEquals(1, subTag.getSuperTags().size());
		assertEquals(tag, subTag.getSuperTags().get(0));
		assertEquals(1, subTag.getSubTags().size());

		final Tag subSubTag = subTag.getSubTags().get(0);
		assertEquals("trallalla", subSubTag.getName());
		assertNotNull(subSubTag.getSuperTags());
		assertEquals(2, subSubTag.getSuperTags().size());
		assertEquals(subTag, subSubTag.getSuperTags().get(0));

		final Tag superSubSubTag = subSubTag.getSuperTags().get(1);
		assertEquals("trallalla", subSubTag.getName());
		assertNotNull(superSubSubTag.getSubTags());
		assertEquals(1, superSubSubTag.getSubTags().size());
		assertEquals(subSubTag, superSubSubTag.getSubTags().get(0));
	}

	@Test
	public void withoutOperators() {
		final Map<String, TagOperator> ops = Collections.<String, TagOperator> emptyMap();
		try {
			this.parser.setOperators(ops);
			fail("Should throw exception");
		} catch (Exception ex) {
		}

		try {
			this.parser.setOperators(null);
			fail("Should throw exception");
		} catch (Exception ex) {
		}
	}
}