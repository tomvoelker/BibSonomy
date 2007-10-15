package resources;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

public class TagTest {

	@Test
	public void isEmpty() {
		Tag t = new Tag();
		assertTrue(t.getTagrelations().isEmpty());
		assertTrue(t.getTags().isEmpty());
		assertTrue(t.getForUsers().isEmpty());
		assertTrue(t.getTagString() == null || t.getTagString().trim().equals(""));
	}

	/*
	 * tests, if adding two tags works
	 */
	@Test
	public void addTagWorks() {

		TreeSet<String> testSet = new TreeSet<String>();
		testSet.add("foo");
		testSet.add("bar");

		Tag t = new Tag();
		t.addTag("foo");
		t.addTag("bar");

		Set<String> realSet = t.getTags();

		assertEquals(realSet, testSet);
		assertTrue(t.getTagrelations().isEmpty());
		assertTrue(t.getForUsers().isEmpty());
	}

	/*
	 * testing simple tag string parsing
	 */
	@Test
	public void setTag1Works() {
		TreeSet<String> testSet = new TreeSet<String>();
		testSet.add("foo");
		testSet.add("bar");

		Tag t = new Tag();
		t.setTags("foo bar");
		Set<String> realSet = t.getTags();

		assertEquals(realSet, testSet);
		assertTrue(t.getTagrelations().isEmpty());
		assertTrue(t.getForUsers().isEmpty());
	}

	/*
	 * testing difficult tag string parsing
	 */
	@Test
	public void setTag4Works() {

		// generate tag object and give it the string to parse
		Tag t = new Tag();
		t.setTags("<-foo<-bar-> eins<- zwei foo bar -> for:klaus->for:manni foo->bar for:klaus->bar");
	
		// check tags by constructing similar tag set
		TreeSet<String> testSet = new TreeSet<String>();
		testSet.add("foo");
		testSet.add("bar");
		testSet.add("eins");
		testSet.add("zwei");
		assertEquals(t.getTags(), testSet);

		// check tagrelations
		HashSet<TagRelation> testSet2 = new HashSet<TagRelation>();
		testSet2.add(new TagRelation("bar", "foo"));
		testSet2.add(new TagRelation("foo", "bar"));

		assertEquals(testSet2, t.getTagrelations());

		// check for:users
		HashSet<String> testSet3 = new HashSet<String>();
		testSet3.add("klaus");
		testSet3.add("manni");
		assertEquals(testSet3, t.getForUsers());

		// adding user names to tag set
		t.addForTag("klaus");
		t.addForTag("manni");
		testSet.add("for:klaus");
		testSet.add("for:manni");
		assertEquals(t.getTags(), testSet);
	}

	/*
	 * testing tagging of tags
	 */
	@Test
	public void setTag3Works() {
		Tag t = new Tag();
		t.setTags("for:klaus foo");
		t.addLower("bar");
	
		// check tags
		TreeSet<String> testSet = new TreeSet<String>();
		testSet.add("foo");
		assertEquals(t.getTags(), testSet);
		
		// check relations
		HashSet<TagRelation> testSet2 = new HashSet<TagRelation>();
		testSet2.add(new TagRelation("bar", "foo"));
		assertEquals(t.getTagrelations(), testSet2);
		
		// check users
		HashSet<String> testSet3 = new HashSet<String>();
		testSet3.add("klaus");
		assertEquals(t.getForUsers(), testSet3);
	
		// adding user names to tag set
		testSet.add("for:klaus");
		t.addForTag("klaus");
		assertEquals(t.getTags(), testSet);
	}
	
	/*
	 * test cloning of tag object
	 */
	@Test
	public void cloneWorks() {
		Tag t = new Tag();
		t.setTags("for:klaus foo");
		t.addLower("bar");
	
		Tag neu = null;
		try {
			neu = (Tag)t.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println("EXCEPTION: " + e);
		}
		
		// check tags
		TreeSet<String> testSet = new TreeSet<String>();
		testSet.add("foo");
		assertEquals(neu.getTags(), testSet);
		
		// check relations
		assertTrue(neu.getTagrelations().isEmpty());
		
		// check users
		assertTrue(neu.getForUsers().isEmpty());
	
	}
	
	/*
	 * testing adding of relations
	 */
	@Test
	public void addTagRelWorks() {
		Tag t = new Tag();
		t.addTagRelation("foo", "bar");
		t.addTagRelation("foo+bar", "bar");
		t.addTagRelation("foo bar", "bar");
		t.addTagRelation("foo->bar", "bar");
		t.addTag("a b");
		
		TagRelation r = new TagRelation ("foo", "bar");
		TagRelation u = new TagRelation ("foo+bar", "bar");
		HashSet<TagRelation> testSet = new HashSet<TagRelation>();
		testSet.add(r);
		testSet.add(u);
		
		assertTrue(t.getTags().isEmpty());
		assertEquals(t.getTagrelations(), testSet);
		
	}
	
	/*
	 * Christophs Example of a broken parsing
	 */
	@Test
	public void setTag2Works (){
		Tag t = new Tag();
		t.setTags("graphtheory evolution graph graphgenerator network researcher<-dorogovtsev brocken->berg");
		
		// check tags by constructing similar tag set
		TreeSet<String> testSet = new TreeSet<String>();
		testSet.add("graphtheory");
		testSet.add("evolution");
		testSet.add("graph");
		testSet.add("network");
		testSet.add("researcher");
		testSet.add("dorogovtsev");
		testSet.add("graphgenerator");
		testSet.add("brocken");
		testSet.add("berg");
		assertEquals(t.getTags(), testSet);

		// check tagrelations
		HashSet<TagRelation> testSet2 = new HashSet<TagRelation>();
		testSet2.add(new TagRelation("dorogovtsev", "researcher"));
		testSet2.add(new TagRelation("brocken", "berg"));

		System.out.println(testSet2);
		System.out.println(t.getTagrelations());
		
		assertEquals(t.getTagrelations(), testSet2);
	}
}
