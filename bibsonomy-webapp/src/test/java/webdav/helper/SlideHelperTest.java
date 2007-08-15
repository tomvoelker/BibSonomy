package webdav.helper;

import org.apache.slide.content.NodeRevisionDescriptor;
import org.apache.slide.structure.ActionNode;
import org.apache.slide.structure.ObjectNode;
import org.apache.slide.structure.SubjectNode;

import junit.framework.TestCase;

public class SlideHelperTest extends TestCase {

	public void testGetNodeByType() {
		ObjectNode node = null;
		node = SlideHelper.getNodeByType("test", ActionNode.class);
		assertNotNull(node);
		assertEquals(node.getClass(), ActionNode.class);
		assertEquals(node.getUri(), "test");

		node = SlideHelper.getNodeByType("test", SubjectNode.class);
		assertNotNull(node);
		assertEquals(node.getClass(), SubjectNode.class);
		assertEquals(node.getUri(), "test");

		try {
			node = SlideHelper.getNodeByType("test", Class.class);
			fail("Should throw exception");
		} catch (final UnsupportedOperationException ex) {}
	}

	public void testGetFlyweightNodeRevisionDescriptor() {
		final NodeRevisionDescriptor desc = SlideHelper.getFlyweightNodeRevisionDescriptor();
		assertNotNull(desc);
		assertEquals(desc.getCreationDateAsDate(), desc.getLastModifiedAsDate());
		assertEquals(1, desc.getRevisionNumber().getMajor());
		assertEquals(0, desc.getRevisionNumber().getMinor());
	}
}