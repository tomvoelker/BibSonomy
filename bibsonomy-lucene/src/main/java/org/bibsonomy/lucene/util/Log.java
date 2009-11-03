package org.bibsonomy.lucene.util;

public class Log {

	/** unique id */
	private int id;

	/** date of insertion of logdata */
	private String logdate;




	public String toString() {

		return "id:"+this.id + "\nlogdate:" +this.logdate;  
		
	}



	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}



	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}



	/**
	 * @return the logdate
	 */
	public String getLogdate() {
		return logdate;
	}



	/**
	 * @param logdate the logdate to set
	 */
	public void setLogdate(String logdate) {
		this.logdate = logdate;
	}


	
}
