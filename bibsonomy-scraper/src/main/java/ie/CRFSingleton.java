package ie;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import edu.umass.cs.mallet.base.fst.CRF4;

/*
 * stores the Conditional Random Field for Information Extraction with mallet
 */
public class CRFSingleton {
		
	private static CRF4 crf = null;
	
	public CRFSingleton() throws FileNotFoundException, IOException, NamingException, ClassNotFoundException {
		if(crf == null){
			// get path to crf file and read crf
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(((String) ((Context) new InitialContext().lookup("java:/comp/env")).lookup("ieScraperCrfPath"))));
			crf = (CRF4) ois.readObject();
			ois.close();
		}		
	}
		
	public CRF4 getCrf(){
		return crf;
	}

}


