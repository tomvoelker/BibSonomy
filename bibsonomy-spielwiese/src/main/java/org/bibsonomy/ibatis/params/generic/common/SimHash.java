package org.bibsonomy.ibatis.params.generic.common;

import org.bibsonomy.ibatis.enums.ConstantID;

public class SimHash {

	private ConstantID simHash;

	public SimHash() {
		this.simHash = ConstantID.SIM_HASH;
	}

	public int getSimHash() {
		return this.simHash.getId();
	}

	public void setSimHash(ConstantID simHash) {
		this.simHash = simHash;
	}
}