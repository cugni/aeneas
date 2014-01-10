package es.bsc.aeneas.loader.exceptions;

public class SourceReadingException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SourceReadingException(String s){
		super(s);
	}

	public SourceReadingException(String string, Exception e) {
		super(string ,e);
	}
}
