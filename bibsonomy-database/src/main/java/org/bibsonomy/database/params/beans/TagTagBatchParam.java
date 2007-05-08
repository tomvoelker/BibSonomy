/*
 * Created on 07.05.2007
 */
package org.bibsonomy.database.params.beans;

public class TagTagBatchParam {
	/** space separated list of tags to be worked off */
	private String tagList;
	
	public static enum Job {
		// BE CAREFULL, order is important!
		DECREMENT,
		INCREMENT
	}
	/** kind of batch job */
	private Job job;
	private Integer contentId;
	
	public Integer getContentId() {
		return this.contentId;
	}
	public void setContentId(Integer contentId) {
		this.contentId = contentId;
	}
	
	public Job getJob() {
		return this.job;
	}
	public void setJob(Job job) {
		this.job = job;
	}
	
	protected Integer getJobInteger() {
		if (job == null) {
			return null;
		} else {
			return job.ordinal();
		}
	}
	
	protected void setJobInteger(final Integer jobInteger) {
		if (jobInteger == null) {
			this.job = null;
		} else {
			try {
				this.job = Job.values()[jobInteger];
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new RuntimeException("unknown jobtype " + jobInteger, e);
			}
		}
	}
	
	public String getTagList() {
		return this.tagList;
	}
	public void setTagList(String tagList) {
		this.tagList = tagList;
	}
}
