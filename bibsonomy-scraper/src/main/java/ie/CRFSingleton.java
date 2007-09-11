package ie;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.naming.NamingException;

import edu.umass.cs.mallet.base.fst.CRF4;

/*
 * stores the Conditional Random Field for Information Extraction with mallet
 */
public class CRFSingleton {
		
	private static final String CRF_DAT = "crf.dat";
	private static CRF4 crf = null;
	
	public CRFSingleton() throws FileNotFoundException, IOException, NamingException, ClassNotFoundException {
		if(crf == null){
			// get path to crf file and read crf
			ObjectInputStream ois = new ObjectInputStream(this.getClass().getResourceAsStream(CRF_DAT));
			crf = (CRF4) ois.readObject();
			ois.close();
		}		
	}
		
	public CRF4 getCrf(){
		return crf;
	}

}


