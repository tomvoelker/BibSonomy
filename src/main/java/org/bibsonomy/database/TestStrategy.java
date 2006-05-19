/*
 * Created on 15.05.2006
 */
package org.bibsonomy.database;

import java.util.Arrays;
import java.util.Iterator;

import org.bibsonomy.viewmodel.TestViewModel;

public class TestStrategy implements Strategy<TestViewModel>{

	public TestViewModel perform() {
		TestViewModel m = new TestViewModel();
		final Iterator<String> it = Arrays.asList(new String[] {"a","b","c"}).iterator();
		m.setItems(it);
		return m;
	}

}
