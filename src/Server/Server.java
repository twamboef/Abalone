package Server;

import java.net.ServerSocket;
import java.util.List;

import Game.Game;
import Game.Player;
import Protocol.ProtocolMessages;
import Protocol.ServerProtocol;

public class Server implements Runnable, ServerProtocol {
	
	private ServerSocket ssock;
	
	private List<ClientHandler> clients;
	
	private ServerTUI view;
	
	private List<Lobby> lobbies;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub

	}
	
	private boolean isConnected(String name) {
		for (ClientHandler client : clients) {
			if (client.getName() == name) return true;
		}
		return false;
	}

	@Override
	public String getConnect(String name) {
		for (ClientHandler client : clients) {
			if (name == null) return "CONNECT" + ProtocolMessages.DELIMITER + ProtocolMessages.MALFORMED + ProtocolMessages.DELIMITER;
			if (client.getName().equals(name)) return "CONNECT" + ProtocolMessages.DELIMITER + ProtocolMessages.UNAUTHORIZED + ProtocolMessages.DELIMITER;
		}
		return "CONNECT" + ProtocolMessages.DELIMITER + ProtocolMessages.SUCCESS + ProtocolMessages.DELIMITER; 
	}

	@Override
	public String createLobby(String name, int size) {
		if (!isConnected(name))
		for (Lobby lobby : lobbies) {
			if (lobby.getName().equals(name) || size < 2 || size > 4) return "CREATE_LOBBY" + ProtocolMessages.DELIMITER + ProtocolMessages.FORBIDDEN + ProtocolMessages.DELIMITER;
		}
		lobbies.add(new Lobby(name,size));
		return "CREATE_LOBBY" + ProtocolMessages.DELIMITER + ProtocolMessages.SUCCESS + ProtocolMessages.DELIMITER;
	}

	@Override
	public String getLobbyList() {
		String result = "LIST_LOBBY" + ProtocolMessages.DELIMITER + ProtocolMessages.SUCCESS + ProtocolMessages.SUCCESS;
		for (Lobby lobby : lobbies) {
			if (lobby.isJoinable()) result += lobby.getName() + ProtocolMessages.COMMA + lobby.getSize() + ProtocolMessages.COMMA + lobby.getPlayers().size() + ProtocolMessages.DELIMITER;
		}
		return result;
	}

	@Override
	public String joinLobby(String name, String lobbyname) {
		
		for (Lobby lobby : lobbies) {
			if (lobby.getName().equals(lobbyname) && lobby.isJoinable()) {
				lobby.join(name);
				return "JOIN_LOBBY" + ProtocolMessages.DELIMITER + ProtocolMessages.SUCCESS + ProtocolMessages.DELIMITER;
			}
		}
		return "JOIN_LOBBY" + ProtocolMessages.DELIMITER + ProtocolMessages.FORBIDDEN + ProtocolMessages.DELIMITER;
	}

	@Override
	public String leaveLobby(String name) {
		for (Lobby lobby : lobbies) {
			for (String player : lobby.getPlayers()) {
				if (player.equals(name)) {
					lobby.leave(name);
					return "LEAVE_LOBBY" + ProtocolMessages.DELIMITER + ProtocolMessages.SUCCESS + ProtocolMessages.DELIMITER; 
				}
			}
		}
		return "LEAVE_LOBBY" + ProtocolMessages.DELIMITER + ProtocolMessages.FORBIDDEN + ProtocolMessages.DELIMITER;
	}

	@Override
	public String doReady(String name) {
		for (ClientHandler client : clients) {
			if (client.getName() == name && client.isInLobby() && !client.isReady()) {
				return "READY_LOBBY" + ProtocolMessages.DELIMITER + ProtocolMessages.SUCCESS + ProtocolMessages.DELIMITER;
			}
		}
		return "READY_LOBBY" + ProtocolMessages.DELIMITER + ProtocolMessages.FORBIDDEN + ProtocolMessages.DELIMITER;
	}

	@Override
	public String doUnready(String name) {
		for (ClientHandler client : clients) {
			if (client.getName() == name && client.isInLobby() && client.isReady()) {
				return "UNREADY_LOBBY" + ProtocolMessages.DELIMITER + ProtocolMessages.SUCCESS + ProtocolMessages.DELIMITER;
			}
		}
		return "UNREADY_LOBBY" + ProtocolMessages.DELIMITER + ProtocolMessages.FORBIDDEN + ProtocolMessages.DELIMITER;
	}

	@Override
	public String lobbyChanged(Lobby lobby) {
		String result = "LOBBY_CHANGE" + ProtocolMessages.DELIMITER;
		for (String p : lobby.getPlayers()) result += p + ProtocolMessages.DELIMITER;
		return result;
	}

	@Override
	public String startGame(Lobby lobby) {
		String result = "GAME_START" + ProtocolMessages.DELIMITER;
		for (String p : lobby.getPlayers()) result += p + ProtocolMessages.DELIMITER;
		return result;
	}

	@Override
	public String makeMove(Game game, String move) {
		return "MOVE" + ProtocolMessages.DELIMITER + move + ProtocolMessages.DELIMITER;
	}
}
