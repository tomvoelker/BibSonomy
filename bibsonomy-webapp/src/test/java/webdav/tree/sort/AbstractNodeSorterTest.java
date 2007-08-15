package webdav.tree.sort;

import java.util.ArrayList;
import java.util.List;

public class AbstractNodeSorterTest {

	public static List<String> getNodes() {
		final List<String> rVal = new ArrayList<String>();
		rVal.add("abc-1");
		rVal.add("acb-2");
		rVal.add("bcd-1");
		rVal.add("cde-1");
		rVal.add("xyz-1");
		rVal.add("xzy-2");
		rVal.add("xyx-3");
		return rVal;
	}
}