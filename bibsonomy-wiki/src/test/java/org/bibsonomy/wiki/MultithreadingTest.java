package org.bibsonomy.wiki;

import java.util.ArrayList;

import org.bibsonomy.model.User;
import org.bibsonomy.wiki.CVWikiModel;
import org.junit.Ignore;
import org.junit.Test;


public class MultithreadingTest extends Thread {
	
	private int count = 0;
	public static final int MAX_RUNS = 100000;
	public static final int MAX_THREADS = 50;
	
	public String realname; 
	
	@Override
	public void run() {
		run(realname, realname + "r");
	}
	
	public void run(String username, String realname) { 
		CVWikiModel model = new CVWikiModel();
		User user = new User(username);
		user.setRealname(realname);
		model.setRequestedUser(user);
		String expected = "\n<p>" + realname + "</p>";
        
		for (int i = 0; i < MAX_RUNS; i++) {
			count++;
			System.out.println(count);
			final String rendered = model.render("<name/>");
			org.junit.Assert.assertEquals("Rendered Realname was not the expected realname!", expected, rendered);
	    }
	//  the single tags are not thread-safe, since their constructor is called only once!  
	   
    }
	 
	@Test
	@Ignore
	 public void main() throws InterruptedException {
		 ArrayList<MultithreadingTest> list = new ArrayList<MultithreadingTest>();
		 
		 for (int i = 0; i < MAX_THREADS; i++) {
			 list.add(new MultithreadingTest());
			 list.get(list.size() - 1).realname = "" + i;
		 }
		 
		 for (MultithreadingTest t : list) {
			 t.start();
		 }
		 
		 for (MultithreadingTest t : list) {
			 t.join();
		 }
		 
		 // TODO: Let the Test die if something fails!
//		 if(count != MAX_RUNS * MAX_THREADS)
//			 org.junit.Assert.fail(count + " was not the right number!");
	 }
}
