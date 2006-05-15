/*
 * Created on 15.05.2006
 */
package org.bibsonomy.viewmodel;

import java.util.Iterator;

public class TestViewModel {
	private Iterator<String> items;

	public Iterator<String> getItems() {
		return items;
	}
	public void setItems(Iterator<String> items) {
		this.items = items;
	}
}
