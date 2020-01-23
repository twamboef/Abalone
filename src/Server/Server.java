package Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import Game.Game;
import Protocol.ProtocolMessages;
import Protocol.ServerProtocol;
import Exceptions.ExitProgram;

public class Server implements Runnable, ServerProtocol {
	private ServerSocket ssock;
	private List<ClientHandler> clients;
	private ServerTUI view;
	private List<Lobby> lobbies;
	/**
	 * String for laziness, consisting of ";200;"
	 */
	private String DELSUCCESS = ProtocolMessages.DELIMITER + ProtocolMessages.SUCCESS + ProtocolMessages.DELIMITER;
	
	public Server() {
		clients = new ArrayList<>();
		view = new ServerTUI();
	}
	
	@Override
	public void run() {
		boolean openNewSocket = true;
		while (openNewSocket) {
			try {
				setup();
				while (true) {
					Socket sock = ssock.accept();
					String name = view.getString("What is your name?");
					view.showMessage("New client [" + name + "] connected!");
					ClientHandler handler =	new ClientHandler(sock, this, name);
					new Thread(handler).start();
					clients.add(handler);
				}
			} catch (ExitProgram e1) {
				openNewSocket = false;
			} catch (IOException e) {
				System.out.println("A server IO error occurred: " + e.getMessage());
				if (!view.getBoolean("Do you want to open a new socket?")) {
					openNewSocket = false;
				}
			}
		}
		view.showMessage("See you later!");
	}
	
	public void setup() throws ExitProgram {
		ssock = null;
		while (ssock == null) {
			int port = view.getInt("What port would you like to use?");
			try { // try to open a new ServerSocket
				view.showMessage("Attempting to open a socket at 127.0.0.1 on port " + port + "...");
				ssock = new ServerSocket(port, 0, InetAddress.getByName("127.0.0.1"));
				view.showMessage("Server started at port " + port);
			} catch (IOException e) {
				view.showMessage("ERROR: could not create a socket at 127.0.0.1 and port " + port + ".");
				if (!view.getBoolean("Do you want to try again?")) {
					throw new ExitProgram("User indicated to exit the program.");
				}
			}
		}
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
			if (name == null) {
				return ProtocolMessages.CONNECT + ProtocolMessages.DELIMITER + ProtocolMessages.MALFORMED + ProtocolMessages.DELIMITER;
			}
			if (client.getName().equals(name)) {
				return ProtocolMessages.CONNECT + ProtocolMessages.DELIMITER + ProtocolMessages.UNAUTHORIZED + ProtocolMessages.DELIMITER;
			}
		}
		return ProtocolMessages.CONNECT + DELSUCCESS; 
	}

	@Override
	public String createLobby(String name, int size) {
		for (Lobby lobby : lobbies) {
			if (lobby.getName().equals(name) || size < 2 || size > 4) {
				return ProtocolMessages.CREATE + ProtocolMessages.DELIMITER + ProtocolMessages.FORBIDDEN + ProtocolMessages.DELIMITER;
			}
		}
		lobbies.add(new Lobby(name,size));
		return ProtocolMessages.CREATE + DELSUCCESS;
	}

	@Override
	public String getLobbyList() {
		String result = ProtocolMessages.LISTL + DELSUCCESS;
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
				return ProtocolMessages.JOIN + DELSUCCESS;
			}
		}
		return ProtocolMessages.JOIN + ProtocolMessages.DELIMITER + ProtocolMessages.FORBIDDEN + ProtocolMessages.DELIMITER;
	}

	@Override
	public String leaveLobby(String name) {
		for (Lobby lobby : lobbies) {
			for (String player : lobby.getPlayers()) {
				if (player.equals(name)) {
					lobby.leave(name);
					return ProtocolMessages.LEAVE + DELSUCCESS; 
				}
			}
		}
		return ProtocolMessages.LEAVE + ProtocolMessages.DELIMITER + ProtocolMessages.FORBIDDEN + ProtocolMessages.DELIMITER;
	}

	@Override
	public String doReady(String name) {
		ClientHandler client = getClient(name);
		if (client.isInLobby() && !client.isReady()) {
			client.ready();
			return ProtocolMessages.READY + DELSUCCESS;
		}
		return ProtocolMessages.READY + ProtocolMessages.DELIMITER + ProtocolMessages.FORBIDDEN + ProtocolMessages.DELIMITER;
	}

	@Override
	public String doUnready(String name) {
		ClientHandler client = getClient(name);
		if (client.isInLobby() && client.isReady()) {
			client.unready();
			return ProtocolMessages.UNREADY + DELSUCCESS;
		}
		return ProtocolMessages.UNREADY + ProtocolMessages.DELIMITER + ProtocolMessages.FORBIDDEN + ProtocolMessages.DELIMITER;
	}

	@Override
	public String lobbyChanged(Lobby lobby) {
		String result = ProtocolMessages.CHANGE + ProtocolMessages.DELIMITER;
		for (String p : lobby.getPlayers()) result += p + ProtocolMessages.DELIMITER;
		return result;
	}

	@Override
	public String startGame(Lobby lobby) {
		String result = ProtocolMessages.START + ProtocolMessages.DELIMITER;
		for (String p : lobby.getPlayers()) result += p + ProtocolMessages.DELIMITER;
		return result;
	}

	@Override
	public String makeMove() {
		return ProtocolMessages.MOVE + DELSUCCESS;
	}

	@Override
	public String sendMove(String name, String move) {
		return ProtocolMessages.MOVE + ProtocolMessages.DELIMITER + name + ProtocolMessages.DELIMITER + move + ProtocolMessages.DELIMITER;
	}
	//TODO from here and check parameters
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
	
	/**
	 * Start a new server
	 */
	public static void main(String[] args) {
		Server server = new Server();
		System.out.println("Starting Abalone server...");
		new Thread(server).start();
	}
}
