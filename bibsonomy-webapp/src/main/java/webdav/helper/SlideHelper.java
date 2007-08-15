package webdav.helper;

import java.util.Calendar;

import org.apache.slide.content.NodeRevisionDescriptor;
import org.apache.slide.content.NodeRevisionDescriptors;
import org.apache.slide.content.NodeRevisionNumber;
import org.apache.slide.structure.ActionNode;
import org.apache.slide.structure.ObjectNode;
import org.apache.slide.structure.SubjectNode;

public class SlideHelper {

	public static ObjectNode getNodeByType(final String uri, final Class clazz) {
		ObjectNode rVal = null;
		if (clazz.equals(ActionNode.class)) {
			rVal = new ActionNode(uri);
		} else if (clazz.equals(SubjectNode.class)) {
			rVal = new SubjectNode(uri);
		} else {
			throw new UnsupportedOperationException("Class ("+clazz.getSimpleName()+") not supported");
		}
		return rVal;
	}

	public static NodeRevisionDescriptor getFlyweightNodeRevisionDescriptor() {
		final NodeRevisionDescriptor descriptor = new NodeRevisionDescriptor(new NodeRevisionNumber(1, 0), NodeRevisionDescriptors.MAIN_BRANCH);
		descriptor.setCreationDate(Calendar.getInstance().getTime());
		descriptor.setLastModified(descriptor.getCreationDateAsDate());
		descriptor.setModificationDate(descriptor.getCreationDateAsDate());
		return descriptor;
	}
}