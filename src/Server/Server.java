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
	
	public ClientHandler getClient(String name) {
		for (ClientHandler client : clients) {
			if (client.getName().equals(name)) return client;
		}
		return null;
	}
	
	public Lobby getLobby(String name) {
		for (Lobby lobby : lobbies) {
			for (String player : lobby.getPlayers()) {
				if (player.equals(name)) return lobby;
			}
		}
		return null;
	}
	
	private boolean isInLobby(String name) {
		return getClient(name).isInLobby();
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
			if (lobby.getName().equals(lobbyname) && lobby.isJoinable() && !isInLobby(name)) {
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
		ClientHandler client = getClient(name);
		if (client.isInLobby() && !client.isReady()) {
			client.ready();
			return "READY_LOBBY" + ProtocolMessages.DELIMITER + ProtocolMessages.SUCCESS + ProtocolMessages.DELIMITER;
		}
		return "READY_LOBBY" + ProtocolMessages.DELIMITER + ProtocolMessages.FORBIDDEN + ProtocolMessages.DELIMITER;
	}

	@Override
	public String doUnready(String name) {
		ClientHandler client = getClient(name);
		if (client.isInLobby() && client.isReady()) {
			client.unready();
			return "UNREADY_LOBBY" + ProtocolMessages.DELIMITER + ProtocolMessages.SUCCESS + ProtocolMessages.DELIMITER;
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
	//TODO vanaf hier
	@Override
	public String makeMove(String name, String move) {
		return "MOVE" + ProtocolMessages.DELIMITER + name + ProtocolMessages.DELIMITER + move + ProtocolMessages.DELIMITER;
	}

	@Override
	public String gameFinish(Game game) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String playerDefeat(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String playerForfeit(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServerList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String challengePlayer(String challenger, String target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String challengeAccept(String accepter, String challenger) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sendPM(String sender, String receiver, String message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String receivePM(String receiver, String sender, String message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sendLM(String sender, String message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String receiveLM(String sender, String message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLeaderboard() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void removeClient(ClientHandler client) {
		clients.remove(client);
	}
}
