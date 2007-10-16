/*
 * Created on 08.10.2007
 */
package org.bibsonomy.webapp.util.spring.factorybeans;

public class Holder<T> {
	private T obj;

	public T getObj() {
		return this.obj;
	}

	public void setObj(T obj) {
		this.obj = obj;
	}
}
