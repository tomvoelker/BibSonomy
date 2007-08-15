/*
 * Created on 02.04.2006
 */
package recommender.model;

import java.util.Map;

public class RecommendedTag implements Map.Entry<Integer,Double> {
	private double score;
	private String name;
	private int id;
	
	public RecommendedTag(int id, String name, double score) {
		this.id = id;
		this.name = name;
		this.score = score;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}

	public Integer getKey() {
		return id;
	}
	public Double getValue() {
		return score;
	}

	public Double setValue(Double score) {
		Double oldValue = this.score;
		this.score = score;
		return oldValue;
	}
	
	public String toString() {
		return getClass().getName() + "[" + id + "," + name + "," + score + "]";
	}
	
}
