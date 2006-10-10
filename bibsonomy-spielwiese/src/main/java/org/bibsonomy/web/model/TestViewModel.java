/*
 * Created on 15.05.2006
 */
package org.bibsonomy.web.model;

import java.util.Iterator;

import org.bibsonomy.db.model.Tag;

public class TestViewModel {
	private Iterator<Tag> items;

	public Iterator<Tag> getItems() {
		return items;
	}
	public void setItems(Iterator<Tag> items) {
		this.items = items;
	}
}