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
	
	
	public static final String CONNECT = "CONNECT";
	public static final String CREATE = "CREATE_LOBBY";
	public static final String LISTL = "LIST_LOBBY";
	public static final String JOIN = "JOIN_LOBBY";
	public static final String LEAVE = "LEAVE_LOBBY";
	public static final String READY = "READY_LOBBY";
	public static final String UNREADY = "UNREADY_LOBBY";
	public static final String CHANGE = "LOBBY_CHANGED";
	public static final String START = "GAME_START";
	public static final String MOVE = "MOVE";
	public static final String FINISH = "GAME_FINISH";
	public static final String DEFEAT = "PLAYER_DEFEAT";
	public static final String FORFEIT = "FORFEIT";
	public static final String LISTP = "LIST_PLAYERS";
	public static final String CHALL = "CHALLENGE";
	public static final String CHALLACC = "CHALLENGE_ACCEPT";
	public static final String PM = "PM";
	public static final String LMSG = "LOBBY_MSG";
	public static final String PMRECV = "PM_RECV";
	public static final String LMSGRECV = "MSG_RECV";
	public static final String LEADERBOARD = "LEADERBOARD";
}
