package helpers;

public class ModifyGroupId {

	/*const to set/clear first bit of an integer*/
	public static final int CONST_SET_1ST_BIT    = 0x80000000; //use logical OR  (|) to set second bit
	public static final int CONST_CLEAR_1ST_BIT  = 0x7FFFFFFF; //use logical AND (&) to clear second bit
	
	public static int getGroupId(int groupid, boolean isSpammer) {
		if (isSpammer) {
			return groupid | CONST_SET_1ST_BIT;			
		} else {
			// NOTE: "return groupid" is not enough, since we want to use that to unflag spammers posts, as well 
			return groupid & CONST_CLEAR_1ST_BIT;
		}
	}
}
