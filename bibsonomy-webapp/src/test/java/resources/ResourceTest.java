package resources;

import org.junit.Test;

public class ResourceTest {

	@Test
	public void testClone() {
		Resource r = new Bookmark();
		
		r.addTag("foo");
		
		
		try {
			Resource clone = (Resource)r.clone();

			/* tag sets contain equal tags but are not the same */
			assert r.getTags().equals(clone.getTags());
			assert r.getTags() != clone.getTags();
			
		} catch (CloneNotSupportedException e) {
			assert false;
		}
	}	
}
