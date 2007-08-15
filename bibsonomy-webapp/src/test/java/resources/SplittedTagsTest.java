package resources;

import java.util.LinkedList;

import org.junit.Test;

public class SplittedTagsTest {

	@Test
	public void testStringInput() {
		SplittedTags t = new SplittedTags("a b c", "", true);

		System.out.println(t.getQuery());
		
		LinkedList<String> test = new LinkedList<String>();
		
		test.add("a");
		test.add("b");
		test.add("c");
		
		assert t.equals(test);
		

	}	
}
