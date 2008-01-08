package org.bibsonomy.common.enums;

/**
 * @author rja
 * @version $Id$
 */
public enum Role {
	
	/** When a new user registers, he has this role. */
	DEFAULT(1),
	/** Is allowed to use admin pages. */
	ADMIN(0);
	
	private static final Role[] map = new Role[]{ADMIN, DEFAULT};
	private final int role;
	
	private Role(final int role) {
		this.role = role;
	}
	
	/** Returns the numerical representation of this object.
	 * @return The numerical representation of the object.
	 */
	public int getRole() {
		return this.role;
	}
	
	/** Creates an instance of this class by its String representation.
	 * 
	 * @param role - a String representing the object. Must be an integer number.
	 * @return The corresponding object.
	 */
	public static Role getRole(final String role) {
		if (role == null) return DEFAULT;
		return getRole(Integer.parseInt(role));
	}
	
	/** Creates an instance of this class by its Integer representation.
	 * 
	 * @param role - an Integer representing the object.
	 * @return The corresponding object.
	 */
	public static Role getRole(final int role) {
		return map[role];
	}

}
