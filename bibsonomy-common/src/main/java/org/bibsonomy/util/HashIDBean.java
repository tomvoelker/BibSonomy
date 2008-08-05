package org.bibsonomy.util;

import org.bibsonomy.common.enums.HashID;

/**
 * Bean to bring the values of the intra and inter hash id to all .jspx files.
 * 
 * To change intra and inter hash id's you have to change the HashID 
 * enum in bibsonomy.common.enums.HashID.
 * 
 * @author daill
 * @version $Id$
 */
public class HashIDBean {
	/**
	 * @return intra hash
	 */
	public HashID getIntra(){
		return HashID.INTRA_HASH;
	}
	
	
	/**
	 * @return inter hash
	 */
	public HashID getInter(){
		return HashID.INTER_HASH;
	}
}
