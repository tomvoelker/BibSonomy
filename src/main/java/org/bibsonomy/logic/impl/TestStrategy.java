/*
 * Created on 15.05.2006
 */
package org.bibsonomy.logic.impl;

import java.util.Arrays;

import org.bibsonomy.db.commands.tags.GetTagNames;
import org.bibsonomy.logic.Strategy;
import org.bibsonomy.web.model.TestViewModel;

import de.innofinity.dbcmd.core.DBConnection;

public class TestStrategy implements Strategy<TestViewModel>{
	private final DBConnection con;
	
	public TestStrategy(DBConnection con) {
		this.con = con;
	}
	
	public TestViewModel perform() {
		TestViewModel m = new TestViewModel();
		final Iterable<Integer> tagIds = Arrays.asList(new Integer[] {1,2,3});
		m.setItems( con.execute(new GetTagNames(tagIds)) );
		return m;
	}

}
