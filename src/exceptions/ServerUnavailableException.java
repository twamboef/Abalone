package exceptions;

public class ServerUnavailableException extends Exception {

	private static final long serialVersionUID = 3194494346431589825L;

	public ServerUnavailableException(String msg) {
		super(msg);
	}

}
