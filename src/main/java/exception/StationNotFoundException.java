package exception;

public class StationNotFoundException extends Exception {

	/**
	* 
	*/
	private static final long serialVersionUID = 608446512511844669L;

	public StationNotFoundException() {
		super();
	}

	// Constructor that accepts a message
	public StationNotFoundException(String message) {
		super(message);
	}
}
