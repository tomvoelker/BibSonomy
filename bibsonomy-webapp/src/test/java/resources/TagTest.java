package resources;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

public class TagTest {

	@Test
	public void isEmpty() {
		Tag t = new Tag();
		assert t.getTagrelations().isEmpty();
		assert t.getTags().isEmpty();
		assert t.getForUsers().isEmpty();
		assert t.getTagString() == null || t.getTagString().trim().equals("");
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

		assert testSet.equals(realSet);
		assert t.getTagrelations().isEmpty();
		assert t.getForUsers().isEmpty();
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

		assert testSet.equals(realSet);
		assert t.getTagrelations().isEmpty();
		assert t.getForUsers().isEmpty();
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
		assert t.getTags().equals(testSet);

		// check tagrelations
		HashSet<TagRelation> testSet2 = new HashSet<TagRelation>();
		testSet2.add(new TagRelation("bar", "foo"));
		testSet2.add(new TagRelation("foo", "bar"));

		assert t.getTagrelations().equals(testSet2);

		// check for:users
		HashSet<String> testSet3 = new HashSet<String>();
		testSet3.add("klaus");
		testSet3.add("manni");
		assert t.getForUsers().equals(testSet3);

		// adding user names to tag set
		t.addForTag("klaus");
		t.addForTag("manni");
		testSet.add("for:klaus");
		testSet.add("for:manni");
		assert t.getTags().equals(testSet);
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
		assert t.getTags().equals(testSet);
		
		// check relations
		HashSet<TagRelation> testSet2 = new HashSet<TagRelation>();
		testSet2.add(new TagRelation("bar", "foo"));
		assert t.getTagrelations().equals(testSet2);
		
		// check users
		HashSet<String> testSet3 = new HashSet<String>();
		testSet3.add("klaus");
		assert t.getForUsers().equals(testSet3);
	
		// adding user names to tag set
		testSet.add("for:klaus");
		t.addForTag("klaus");
		assert t.getTags().equals(testSet);
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
		assert neu.getTags().equals(testSet);
		
		// check relations
		assert neu.getTagrelations().isEmpty();
		
		// check users
		assert neu.getForUsers().isEmpty();
	
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
		
		assert t.getTags().isEmpty();
		assert t.getTagrelations().equals(testSet);
		
	}
	
	/*
	 * Christophs Example of a broken parsing
	 */
	@Test
	public void setTag2Works (){
		Tag t = new Tag();
		t.setTags("graphtheory evolution graph graphgenerator network researcher<-dorogovtsev");
		
		// check tags by constructing similar tag set
		TreeSet<String> testSet = new TreeSet<String>();
		testSet.add("graphtheory");
		testSet.add("evolution");
		testSet.add("graph");
		testSet.add("network");
		testSet.add("researcher");
		testSet.add("dorogovtsev");
		testSet.add("graphgenerator");
		assert t.getTags().equals(testSet);

		// check tagrelations
		HashSet<TagRelation> testSet2 = new HashSet<TagRelation>();
		testSet2.add(new TagRelation("dorogovtsev", "researcher"));

		System.out.println(testSet2);
		System.out.println(t.getTagrelations());
		
		assert t.getTagrelations().equals(testSet2);
	}
}
