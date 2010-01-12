/**
 *  
 *  BibSonomy-Model - Java- and JAXB-Model.
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.model.util;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.TreeSet;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.util.tagparser.TagString3Lexer;
import org.bibsonomy.model.util.tagparser.TagString3Parser;
import org.junit.Test;

/**
 * @author Robert Jaeschke
 * @author Anton Wilhelm (awil)
 */
public class TagParserTest {
	private Set<Tag> parse(final String tagString) {
		Set<Tag> tags = new TreeSet<Tag>();
		
		if (tagString != null) {
			CommonTokenStream tokens = new CommonTokenStream();
			tokens.setTokenSource(new TagString3Lexer(new ANTLRStringStream(tagString)));
			TagString3Parser parser = new TagString3Parser(tokens, tags);
			try {
				parser.tagstring();
            } catch (RecognitionException e) {
                System.out.println(e);
                e.printStackTrace();
            }
		}		
		return tags;
	}
	
	private boolean checkForSuperRelation(Set<Tag> tags, Tag tagToCheck) {
		boolean foundSuperTag = false;
		for (final Tag tag : tags) {
			if (tag.equals(tagToCheck)) {
				assertEquals(tag.getSuperTags(), tagToCheck.getSuperTags());
				foundSuperTag = true;
			}

		}
		return foundSuperTag;
	}
	
	private boolean checkForSubRelation(Set<Tag> tags, Tag tagToCheck) {
		boolean foundSubTag = false;
		for (Tag tag : tags) {
			if (tag.equals(tagToCheck)) {
				assertEquals(tag.getSubTags(), tagToCheck.getSubTags());
				foundSubTag = true;
			}

		}
		return foundSubTag;
	}
	
	/**
	 * Test empty tag
	 */
	@Test
	public void isEmpty() {
		final Tag t = new Tag();
//		assertTrue(t.getTagrelations().isEmpty());
		assertTrue(t.getSubTags().isEmpty());
		assertTrue(t.getSuperTags().isEmpty());
		assertTrue(t.getGlobalcount()==0);
		assertTrue(t.getUsercount()==0);
//		assertTrue(t.getForUsers().isEmpty());
		assertTrue(t.getName() == null || t.getName().trim().equals(""));
	}

	/**
	 * tests, if adding two tags works
	 * testing simple tag string parsing
	 */
	@Test
	public void addTagWorks() {
		Set<Tag> tags = parse("foo bar");

		// check tags by constructing similar tag set
		TreeSet<Tag> testSet = new TreeSet<Tag>();
		testSet.add(new Tag("foo"));
		testSet.add(new Tag("bar"));
		
		
		assertEquals(testSet, tags);
//		assertTrue(t.getTagrelations().isEmpty());
//		assertTrue(t.getForUsers().isEmpty());
	}

	/**
	 * <pre>
	 * should construct the following set of tags:
	 * [berg, brocken, dorogovtsev, evolution, graph, graphgenerator, graphtheory, matterhorn, network, researcher, welt, zugspitze]
	 * SUPERTAG <- SUBTAG
	 * with these relations:
	 * world:       superTags []           subTags [berg]
	 * brocken:     superTags [mountain]   subTags []
	 * zugspitze:   superTags [mountain]   subTags []
	 * dorogovtsev: superTags [researcher] subTags []
	 * mountain:    superTags [world]      subTags [brocken, matterhorn, zugspitze]
	 * researcher:  superTags []           subTags [dorogovtsev]
	 * matterhorn:  superTags [mountain]   subTags []
	 * 
	 * remark: empty brackets stand for no tag
	 * </pre>
	 */
	@Test
	public void setTag2Works () {
		Set<Tag> tags = parse("graphtheory evolution graphtheory graph graphgenerator graphtheory network researcher<-dorogovtsev brocken->mountain zugspitze->mountain mountain<-matterhorn world<-mountain");
		
		// check tags by constructing similar tag set
		TreeSet<Tag> testSet = new TreeSet<Tag>();
		testSet.add(new Tag("graphtheory"));
		testSet.add(new Tag("evolution"));
		testSet.add(new Tag("graph"));
		testSet.add(new Tag("network"));
		testSet.add(new Tag("researcher"));
		testSet.add(new Tag("dorogovtsev"));
		testSet.add(new Tag("graphgenerator"));
		testSet.add(new Tag("brocken"));
		testSet.add(new Tag("mountain"));
		testSet.add(new Tag("world"));
		testSet.add(new Tag("zugspitze"));
		testSet.add(new Tag("matterhorn"));
		
		assertEquals(testSet, tags);
		
		// check tagrelations
		Tag t1 = new Tag("dorogovtsev");
		t1.addSuperTag(new Tag("researcher"));
		
		Tag t2 = new Tag("mountain");
		t2.addSuperTag(new Tag("world"));
		t2.addSubTag(new Tag("brocken"));
		t2.addSubTag(new Tag("zugspitze"));
		t2.addSubTag(new Tag("matterhorn"));
		
		assertTrue(tags.contains(t1));
		assertTrue(tags.contains(t2));
	
		assertTrue(checkForSuperRelation(tags, t1));
		assertTrue(checkForSuperRelation(tags, t2));
		assertTrue(checkForSubRelation(tags, t2));
	}
	
	/**
	 * testing tagging of tags
	 */
	@Test
	public void setTag3Works() {
		Set<Tag> tags = parse("for:klaus foo<-bar");
		
		// check tags by constructing similar tag set
		TreeSet<Tag> testSet = new TreeSet<Tag>();
		testSet.add(new Tag("foo"));
		testSet.add(new Tag("bar"));
		testSet.add(new Tag("for:klaus"));
	
		assertEquals(testSet, tags);
		
		// check tagrelations
		Tag t1 = new Tag("bar");
		t1.addSuperTag(new Tag("foo"));
	
		assertTrue(tags.contains(t1));
		assertTrue(checkForSuperRelation(tags, t1));
		
//		// check users
//		Set<String> testSet3 = new HashSet<String>();
//		testSet3.add("klaus");
//		assertEquals(testSet3, t.getForUsers());
//	
//		// adding user names to tag set
//		testSet.add("for:klaus");
//		t.addForTag("klaus");
//		assertEquals(testSet, t.getTags());
	}
	
	/**
	 * testing difficult tag string parsing
	 * 
	 * should:
	 * getTagrelations: [bar<-foo, foo<-bar]
	 * t.getTags():     [bar, eins, foo, for:klaus, for:manni, zwei]
	 * t.getForUsers(): [klaus, manni]
	 * 
	 * with these relations:
	 * foo:       superTags []           subTags [bar]
	 * 
	 * 
	 * could:
	 * [bar, eins, foo, for:klaus, for:manni, zwei]
	 */
	@Test
	public void setTag4Works() {
		// generate tag object and give it the string to parse
		Set<Tag> tags = parse("<-foo<-bar-> eins<- zwei foo bar -> for:klaus->for:manni foo->bar for:klaus->bar");

		// check tags by constructing similar tag set
		TreeSet<Tag> testSet = new TreeSet<Tag>();
		testSet.add(new Tag("foo"));
		testSet.add(new Tag("bar"));
		testSet.add(new Tag("eins"));
		testSet.add(new Tag("zwei"));
		testSet.add(new Tag("for:klaus"));
		testSet.add(new Tag("for:manni"));
		
		assertEquals(testSet, tags);
		
		// check tagrelations
		Tag tagBar = new Tag("bar");
		Tag tagFoo = new Tag("foo");
		tagFoo.addSuperTag(tagBar);
		tagBar.addSubTag(tagFoo);
		tagBar.addSubTag(new Tag("for:klaus"));
		
		assertTrue(checkForSuperRelation(tags, tagFoo));
		assertTrue(checkForSubRelation(tags, tagBar));
		

//		// check for:users
//		Set<String> testSet3 = new HashSet<String>();
//		testSet3.add("klaus");
//		testSet3.add("manni");
//		assertEquals(testSet3, t.getForUsers());
//
//		// adding user names to tag set
//		t.addForTag("klaus");
//		t.addForTag("manni");
//		testSet.add("for:klaus");
//		testSet.add("for:manni");
//		assertEquals(testSet, t.getTags());
	}
	
	/**
	 * test character: < >  -
	 */
	@Test
	public void setTag5Works() {
		// generate tag object and give it the string to parse
		Set<Tag> tags = parse("<tag> -tag- foo-bar -foo-bar-");

		// check tags by constructing similar tag set
		TreeSet<Tag> testSet = new TreeSet<Tag>();
		testSet.add(new Tag("<tag>"));
		testSet.add(new Tag("-tag-"));
		testSet.add(new Tag("foo-bar"));
		testSet.add(new Tag("-foo-bar-"));
		
		assertEquals(testSet, tags);
	}
	
	/**
	 * test crazy tagrelations
	 */
	@Test
	public void setTag6Works() {
		// generate tag object and give it the string to parse
		// i = ignored characters, p = parsed characters
		//                      i     i   i i   i    i   i   i    i    p     p    p     p
		Set<Tag> tags = parse("->-> <-<- -> <- -><- <-> ->- -<- <--> <---> <----> <<- ->> foobar");

		// check tags by constructing similar tag set
		Set<Tag> testSet = new TreeSet<Tag>();
		testSet.add(new Tag("foobar"));
		testSet.add(new Tag("-"));
		testSet.add(new Tag("--"));
		testSet.add(new Tag(">"));
		testSet.add(new Tag("<"));
		
		assertEquals(testSet, tags);
	}
	
	/**
	 * test special characters
	 * only 'foobar' is a tag
	 */
	@Test
	public void setTag7Works() {
		// generate tag object and give it the string to parse
		Set<Tag> tags = parse("-?)´ß`-.<-,c#c.,--y.-<<x#x- >$=( %&=->- -< foobar");

		// check tags by constructing similar tag set
		Set<Tag> testSet = new TreeSet<Tag>();
		testSet.add(new Tag("-?)´ß`-."));
		testSet.add(new Tag(",c#c.,--y.-<<x#x-"));
		testSet.add(new Tag(">$=("));
		testSet.add(new Tag("%&="));
		testSet.add(new Tag("-"));
		testSet.add(new Tag("-<"));
		testSet.add(new Tag("foobar"));
		
		assertEquals(testSet, tags);
	}
	
	/**
	 * test russian / cyrillic font
	 */
	@Test
	public void setTag8Works() {
		// generate tag object and give it the string to parse
		Set<Tag> tags = parse("Экс-премьер Пакистана Беназир Бхутто ӃӄӅӆӇӈӉӊӋӌӍӎӐӑӒӓӔӕӖӗӘәӚӛӜӝӞӟӠӡӢӣӤӥӦӧӨөӪӫӬӭӮӯӰӱӲӳӴӵ");

		// check tags by constructing similar tag set
		TreeSet<Tag> testSet = new TreeSet<Tag>();
		testSet.add(new Tag("Экс-премьер"));
		testSet.add(new Tag("Пакистана"));
		testSet.add(new Tag("Беназир"));
		testSet.add(new Tag("Бхутто"));
		testSet.add(new Tag("ӃӄӅӆӇӈӉӊӋӌӍӎӐӑӒӓӔӕӖӗӘәӚӛӜӝӞӟӠӡӢӣӤӥӦӧӨөӪӫӬӭӮӯӰӱӲӳӴӵ"));
		
		assertEquals(testSet, tags);	
	}
	
	/**
	 * test japanese writing system
	 */
	@Test
	public void setTag9Works() {
		// generate tag object and give it the string to parse
		Set<Tag> tags = parse("よう光接続サービスをはじめ続々登場!動画などのコンテンツゼン");

		// check tags by constructing similar tag set
		TreeSet<Tag> testSet = new TreeSet<Tag>();
		testSet.add(new Tag("よう光接続サービスをはじめ続々登場!動画などのコンテンツゼン"));
		
		assertEquals(testSet, tags);	
	}
	
	/**
	 * test cloning of tag object
	 */
	@Test
	public void cloneWorks() {
		// check tagrelations
		Tag tagBar = new Tag("bar");
		Tag tagFoo = new Tag("foo");
		tagFoo.addSuperTag(tagBar);
		tagBar.addSubTag(tagFoo);
		
		Tag tagClone = null;
		
		//clone
		try {
			tagClone = tagBar.clone();
		} catch (CloneNotSupportedException ex) {
			ex.printStackTrace();
		}
		
		assertEquals(tagBar, tagClone);
		assertEquals(tagBar.getSubTags(), tagClone.getSubTags());
	}
	
	/**
	 * test tagrelation
	 */
	@Test
	public void setTagRel1Works() {
		// generate tag object and give it the string to parse
		Set<Tag> tags = parse("auto<-vw vw->auto");
		
		// check tagrelations
		Set<Tag> testSet = new TreeSet<Tag>();
		Tag tagCar = new Tag("auto");
		Tag tagVW = new Tag("vw");
		tagVW.addSuperTag(tagCar);
		tagCar.addSubTag(tagVW);
		
		testSet.add(tagCar);
		testSet.add(tagVW);
		
		assertEquals(testSet, tags);
		
		// check tagrelations
		assertTrue(checkForSuperRelation(tags, tagCar));
		assertTrue(checkForSubRelation(tags, tagVW));
	}
	
	/**
	 * testing adding of relations
	 */
	@Test
	public void addTagRel2Works() {
		// generate tag object and give it the string to parse
		Set<Tag> tags = parse("bar<-foo bar<-foo+bar");
		
		// check tagrelations
		Tag tagBar = new Tag("bar");
		Tag tagFoo = new Tag("foo");
		Tag tagFooBar = new Tag("foo+bar");
		
		tagFoo.addSuperTag(tagBar);
		tagBar.addSubTag(tagFoo);
		tagBar.addSubTag(tagFooBar);
		tagFooBar.addSuperTag(tagBar);

		
		Set<Tag> testSet = new TreeSet<Tag>();
		testSet.add(tagBar);
		testSet.add(tagFoo);
		testSet.add(tagFooBar);
		
		assertEquals(testSet, tags);
		
		assertTrue(checkForSuperRelation(tags, tagFoo));
		assertTrue(checkForSubRelation(tags, tagBar));
		assertTrue(checkForSuperRelation(tags, tagFooBar));
	}
}