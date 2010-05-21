package org.bibsonomy.community.algorithm;

public class MockAlgorithm implements Algorithm {
	private String name;
	private String meta;

	public MockAlgorithm(String name, String meta) {
		this.name = name;
		this.meta = meta;
	}
	
	public String getMeta() {
		return this.meta;
	}

	public String getName() {
		return this.name;
	}

}
