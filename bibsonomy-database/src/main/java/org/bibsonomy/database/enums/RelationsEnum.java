package org.bibsonomy.database.enums;

public enum RelationsEnum {
	REFERENCE(0), PART_OF(1);
	private int value;
	private final String [] keys = {"Reference", "Part of"};
	public int getIndex(String key) {
		int i= 0;
		for(String tKey : keys) {
			if(tKey.equals(key)) return i;
			i++;
		}
		return -1;
	}
	private RelationsEnum(int value) {
		this.setValue(value);	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
