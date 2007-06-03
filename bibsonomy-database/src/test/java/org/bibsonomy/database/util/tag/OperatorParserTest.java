/*
 * Created on 03.06.2007
 */
package org.bibsonomy.database.util.tag;

import java.util.HashMap;
import java.util.Map;

import org.bibsonomy.model.Tag;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class OperatorParserTest {
	private OperatorParser parser;
	
	@Before
	public void setUp() {
		this.parser = new OperatorParser();
		final Map<String, TagOperator> ops = new HashMap<String, TagOperator>();
		ConceptTagOperator leftToRightConceptOp = new ConceptTagOperator();
		leftToRightConceptOp.setLeftToRight(true);
		ConceptTagOperator rightToLeftConceptOp = new ConceptTagOperator();
		rightToLeftConceptOp.setLeftToRight(false);
		ops.put("->", leftToRightConceptOp);
		ops.put("<-", rightToLeftConceptOp);
		this.parser.setOperators(ops);
	}
	
	@Test
	public void testSimpleParse() {
		final Tag tag = parser.parse("hurz <- bla");
		Assert.assertEquals("hurz", tag.getName());
		Assert.assertNotNull(tag.getSubTags());
		Assert.assertEquals(1, tag.getSubTags().size());
		
		final Tag subTag = tag.getSubTags().get(0);
		Assert.assertEquals("bla", subTag.getName());
		Assert.assertNotNull(subTag.getSuperTags());
		Assert.assertEquals(1, subTag.getSuperTags().size());
		Assert.assertEquals(tag, subTag.getSuperTags().get(0));
	}
	
	@Test
	public void testChainParse() {
		final Tag tag = parser.parse(" hurz<-bla<- \t trallalla->X  ");
		Assert.assertEquals("hurz", tag.getName());
		Assert.assertNotNull(tag.getSubTags());
		Assert.assertEquals(1, tag.getSubTags().size());
		
		final Tag subTag = tag.getSubTags().get(0);
		Assert.assertEquals("bla", subTag.getName());
		Assert.assertNotNull(subTag.getSuperTags());
		Assert.assertEquals(1, subTag.getSuperTags().size());
		Assert.assertEquals(tag, subTag.getSuperTags().get(0));
		Assert.assertEquals(1, subTag.getSubTags().size());
		
		final Tag subSubTag = subTag.getSubTags().get(0);
		Assert.assertEquals("trallalla", subSubTag.getName());
		Assert.assertNotNull(subSubTag.getSuperTags());
		Assert.assertEquals(2, subSubTag.getSuperTags().size());
		Assert.assertEquals(subTag, subSubTag.getSuperTags().get(0));
		
		final Tag superSubSubTag = subSubTag.getSuperTags().get(1);
		Assert.assertEquals("trallalla", subSubTag.getName());
		Assert.assertNotNull(superSubSubTag.getSubTags());
		Assert.assertEquals(1, superSubSubTag.getSubTags().size());
		Assert.assertEquals(subSubTag, superSubSubTag.getSubTags().get(0));
	}
}
