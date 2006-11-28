package org.bibsonomy.model;



/**
 * @author mgr
 *
 */
public class BibtexUrl{
	
private String hash;
private String type;
private int counter;



/*
 * get and set
 */
	
public String getHash() {
	return this.hash;
}

public void setHash(String hash) {
	this.hash = hash;
}


/*
 * get,set type of simhash
 */

public String getType() {
	return this.type;
}

public void setType(String type) {
	this.type = type;
}

public int getCounter() {
	return this.counter;
}

public void setCounter(int counter) {
	this.counter =counter;
}
	
	
	
	
}