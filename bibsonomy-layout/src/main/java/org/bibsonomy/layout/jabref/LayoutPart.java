package org.bibsonomy.layout.jabref;


/**
 * The parts a layout consists of: begin, item, end.
 * <ul>
 * <li>begin: prepended to the result</li>
 * <li>end: appended to the result</li>
 * <li>item: used to format one item</li> 
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public enum LayoutPart {
	/**
	 * 
	 */
	BEGIN("begin"), 
	/**
	 * 
	 */
	END("end"), 
	/**
	 * 
	 */
	ITEM("item");

	private static String[] allTypes = new String[] {"begin", "item", "end"};

	/**
	 * The name of a part.
	 */
	private String name;
	
	private LayoutPart(final String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static LayoutPart getLayoutType (String type) {
		if ("begin".equals(type)) return BEGIN;
		if ("end".equals(type)) return END;
		return ITEM;
	}

	private String getString (LayoutPart type) {
		if (type == BEGIN) return "begin";
		if (type == END) return "end";
		return "item";
	}

	public String toString() {
		return getString(this);
	}

	public static String[] getLayoutTypes () {
		return allTypes;
	}

}

