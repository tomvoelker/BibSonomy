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
	EMBEDDEDBEGIN("embeddedbegin"),
	/**
	 * 
	 */
	END("end"),
	/**
	 * 
	 */
	EMBEDDEDEND("embeddedend"),
	/**
	 * 
	 */
	ITEM("item");

	public static LayoutPart[] layoutParts = new LayoutPart[]{BEGIN, END, ITEM, EMBEDDEDBEGIN, EMBEDDEDEND};
	
	private static String[] allTypes = new String[] {BEGIN.name, END.name, ITEM.name, EMBEDDEDBEGIN.name, EMBEDDEDEND.name};

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
	
	public static LayoutPart getLayoutType (final String typeString) {
		for (final LayoutPart part: layoutParts) {
			if (part.getName().equals(typeString)) return part;
		}
		return ITEM;
	}

	public String toString() {
		return name;
	}

	public static String[] getLayoutTypes () {
		return allTypes;
	}

}

