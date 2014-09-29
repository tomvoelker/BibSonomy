package org.bibsonomy.es;

/**
 * Experimental post
 *
 * @author lka
 */
public class ExpBibPost {

	private String title;
	private String description;
	private String author;
	private Tenure tenure;
//	private LocalDate startDate;
//	private LocalDate endDate;

	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	
	/**
	 * @return the tenure
	 */
	public Tenure getTenure() {
		return this.tenure;
	}

	/**
	 * @param tenure the tenure to set
	 */
	public void setTenure(Tenure tenure) {
		this.tenure = tenure;
	}



	/**
	 * @return the author
	 */
	public String getAuthor() {
		return this.author;
	}

	/**
	 * @param author the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}



	public static enum Tenure {
		PARTTIME,
		FULLTIME;
	}
	
}
