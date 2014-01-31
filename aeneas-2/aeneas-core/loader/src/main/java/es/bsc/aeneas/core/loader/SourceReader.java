package es.bsc.aeneas.core.loader;

import es.bsc.aeneas.core.rosetta.Rosetta;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SourceReader implements Callable<Boolean>{
	/*
	 * Setter for the source
	 */
	public abstract void open(InputStreamReader sr);
        public void open(File file) {
        try {
            open(new InputStreamReader(new FileInputStream(file)));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SourceReader.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalArgumentException("File not found : "+file.getName(),ex);
        }
        }
	/*
	 * Sets the class where insert the read data. 
	 * 
	 * @param db the DBSetter abstract interface of a Database
	 */
	public abstract void setDBSetter(Rosetta db);
	/* Returns, if any ,the exception occurred during
	 * the reading. 
	 * Returns, if any ,the exception occurred during
	 * the reading. 
	 * @return null if there isn't exception, otherwise a SourceReadingException
	 */
	
	/*
	 * Returns the number of line read at the moment
	 * 
	 * @return number of line read
	 */
	public  abstract int lineRead();
	
}
