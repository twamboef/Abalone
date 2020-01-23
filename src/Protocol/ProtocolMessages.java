package Protocol;

public class ProtocolMessages {
	/**
	 * Delimiter used to separate arguments sent over the network.
	 */
	public static final String DELIMITER = ";";
	/**
	 * Delimiter for CSV (comma separated values)
	 */
	public static final String COMMA = ",";
	
	/**
	 * Sent as last line in a multi-line response to indicate the end of the text.
	 */
	public static final String EOT = "--EOT--";
	
	
	public static final int SUCCESS = 200;
	public static final int MALFORMED = 400;
	public static final int UNAUTHORIZED = 401;
	public static final int FORBIDDEN = 403;
	public static final int NOT_FOUND = 404;
}
