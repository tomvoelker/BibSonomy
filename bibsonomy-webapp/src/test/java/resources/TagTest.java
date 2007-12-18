package resources;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

/**
 * @author Robert Jaeschke
 * @author Anton Wilhelm (awil)
 *
 */
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
		
		assertEquals(t.getTagrelations(), testSet2);
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

		assertEquals(t.getTagrelations(), testSet2);

		// check for:users
		HashSet<String> testSet3 = new HashSet<String>();
		testSet3.add("klaus");
		testSet3.add("manni");
		assertEquals(t.getForUsers(), testSet3);

		// adding user names to tag set
		t.addForTag("klaus");
		t.addForTag("manni");
		testSet.add("for:klaus");
		testSet.add("for:manni");
		assertEquals(t.getTags(), testSet);
	}
	
	/*
	 * test character: < >  -
	 */
	@Test
	public void setTag5Works() {
		Tag t = new Tag();
		t.setTags("<tag> -tag- foo-bar -foo-bar-");
		TreeSet<String> testSet = new TreeSet<String>();
		testSet.add("<tag>");
		testSet.add("-tag-");
		testSet.add("foo-bar");
		testSet.add("-foo-bar-");
		assertEquals(t.getTags(), testSet);
	}
	
	/*
	 * test crazy tagrelations
	 */
	@Test
	public void setTag6Works() {
		Tag t = new Tag();
		// i = ignored characters, p = parsed characters
		//          i     i   i i   i    i   i   i    i    p     p    p     p
		t.setTags("->-> <-<- -> <- -><- <-> ->- -<- <--> <---> <----> <<- ->> foobar");
		TreeSet<String> testSet = new TreeSet<String>();
		testSet.add("foobar");
		testSet.add("-");
		testSet.add("--");
		testSet.add(">");
		testSet.add("<");
		
		assertEquals(t.getTags(), testSet);
		
	}
	
	/*
	 * test special characters
	 * only 'foobar' is a tag
	 */
	@Test
	public void setTag7Works() {
		Tag t = new Tag();
		t.setTags("-?)´ß`-.<-,c#c.,--y.-<<x#x- >$=( %&=->- -< foobar");
		TreeSet<String> testSet = new TreeSet<String>();
		testSet.add("-?)´ß`-.");
		testSet.add(",c#c.,--y.-<<x#x-");
		testSet.add(">$=(");
		testSet.add("%&=");
		testSet.add("-");
		testSet.add("-<");
		testSet.add("foobar");
		assertEquals(t.getTags(), testSet);
	}
	
	/*
	 * test russian / cyrillic font
	 */
	@Test
	public void setTag8Works() {
		Tag t = new Tag();
		t.setTags("Экс-премьер Пакистана Беназир Бхутто ӃӄӅӆӇӈӉӊӋӌӍӎӐӑӒӓӔӕӖӗӘәӚӛӜӝӞӟӠӡӢӣӤӥӦӧӨөӪӫӬӭӮӯӰӱӲӳӴӵ");
		TreeSet<String> testSet = new TreeSet<String>();
		testSet.add("Экс-премьер");
		testSet.add("Пакистана");
		testSet.add("Беназир");
		testSet.add("Бхутто");
		testSet.add("ӃӄӅӆӇӈӉӊӋӌӍӎӐӑӒӓӔӕӖӗӘәӚӛӜӝӞӟӠӡӢӣӤӥӦӧӨөӪӫӬӭӮӯӰӱӲӳӴӵ");
		assertEquals(t.getTags(), testSet);
	}
	
	/*
	 * test japanese writing system
	 */
	@Test
	public void setTag9Works() {
		Tag t = new Tag();
		t.setTags("よう光接続サービスをはじめ続々登場!動画などのコンテンツゼン");
		TreeSet<String> testSet = new TreeSet<String>();
		testSet.add("よう光接続サービスをはじめ続々登場!動画などのコンテンツゼン");
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
	 * test tagrelation
	 */
	@Test
	public void setTagRel1Works() {
		Tag t = new Tag();
		t.setTags("auto->vw");
		Tag t2 = new Tag();
		t2.setTags("vw<-auto");
		TagRelation r = new TagRelation ("auto", "vw");
		
		HashSet<TagRelation> testSet = new HashSet<TagRelation>();
		testSet.add(r);
		assertEquals(t.getTagrelations(), testSet);
		assertEquals(t2.getTagrelations(), testSet);
	}
	
	/*
	 * testing adding of relations
	 */
	@Test
	public void addTagRel2Works() {
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
		
		assertTrue(t.getTags().isEmpty()); // Why this work?
		assertEquals(t.getTagrelations(), testSet);
	}
}