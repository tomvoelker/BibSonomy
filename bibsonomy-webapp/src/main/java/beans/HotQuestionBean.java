package beans;

import java.util.Hashtable;
import java.util.LinkedList;
import java.io.Serializable;
import resources.Question;

public class HotQuestionBean implements Serializable {
	
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3257850961094522929L;
	
	private Hashtable errors;
	
	private Question hotquestion;
	
	private LinkedList answer;
	
	public String getErrorMsg(String s) {
		String errorMsg =(String)errors.get(s.trim());
		return (errorMsg == null) ? "":errorMsg;
	}
	
	public HotQuestionBean() {
		this.hotquestion = null;
		this.answer = new LinkedList();
		
	}

	public int getAnswerCount(){
		return answer.size();
	}
	
	public void addAnswer(Question q){
		answer.add(q);
	}
	
	public void setAnswer(LinkedList q){
		answer=q;
	}
	
	public LinkedList getAnswer(){
		return answer;
	}

	public Question getHotquestion() {
		return hotquestion;
	}

	public void setHotquestion(Question hotquestion) {
		this.hotquestion = hotquestion;
	}

		
}// end class