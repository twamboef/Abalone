package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import Protocol.ProtocolMessages;

public class ClientHandler implements Runnable {
	private BufferedReader in;
	private BufferedWriter out;
	private Socket sock;
	private Server server;
	private String name;
	private boolean connected = false;
	private boolean inLobby = false;
	private boolean ready = false;
	
	public ClientHandler(Socket sock, Server server, String name) {
		try {
			in = new BufferedReader(
					new InputStreamReader(sock.getInputStream()));
			out = new BufferedWriter(
					new OutputStreamWriter(sock.getOutputStream()));
			this.sock = sock;
			this.server = server;
			this.name = name;
		} catch (IOException e) {
			shutDown();
		}
	}
	
	public void run() {
		String message;
		try {
			message = in.readLine();
			while (message != null) {
				System.out.println("> [" + name + "] Incoming: " + message);
				handleCommand(message);
				out.newLine();
				out.flush();
				message = in.readLine();
			}
			shutDown();
		}
		catch (IOException e) {
			shutDown();
		}
	}
	
	private void handleCommand(String message) throws IOException {
		String[] splitmsg = message.split(ProtocolMessages.DELIMITER);
		String command = splitmsg[0];
		String parm1 = null, parm2 = null;
		if (splitmsg.length > 1) parm1 = splitmsg[1];
		if (splitmsg.length > 2) parm2 = splitmsg[2];
		if (!command.equals("CONNECT") && connected == false) {
			out.write(command + ProtocolMessages.DELIMITER + ProtocolMessages.UNAUTHORIZED + ProtocolMessages.DELIMITER);
			return;
		}
		switch (command) {
		case ProtocolMessages.CONNECT:
			String result = server.getConnect(parm1);
			if (result.split(ProtocolMessages.DELIMITER)[1].equals("200")) connected = true;
			out.write(result);
			break;
		case ProtocolMessages.CREATE:
			out.write(server.createLobby(parm1, Integer.parseInt(parm2)));
			break;
		case ProtocolMessages.LISTL:
			out.write(server.getLobbyList());
			break;
		case ProtocolMessages.JOIN:
			out.write(server.joinLobby(name, parm1));
			break;
		case ProtocolMessages.LEAVE:
			out.write(server.leaveLobby(name));
			break;
		case ProtocolMessages.READY:
			out.write(server.doReady(name));
			break;
		case ProtocolMessages.UNREADY:
			out.write(server.doUnready(name));
			break;
		case ProtocolMessages.MOVE:
			out.write(server.makeMove());
			List<ClientHandler> gameClients = new ArrayList<ClientHandler>();
			for (String p : server.getLobby(name).getPlayers()) {
				gameClients.add(server.getClient(p));
			}
			for (ClientHandler ch : gameClients) {
				ch.getWriter().write(server.sendMove(name, parm1));
			}
			break;
		case ProtocolMessages.FORFEIT:
			out.write(server.playerForfeit(name));
			break;
		case ProtocolMessages.LISTP:
			break;
		case ProtocolMessages.CHALL:
			break;
		case ProtocolMessages.CHALLACC:
			break;
		case ProtocolMessages.PM:
			break;
		case ProtocolMessages.LMSG:
			break;
		case ProtocolMessages.LEADERBOARD:
			break;
		default:
			out.write(command + ProtocolMessages.DELIMITER + ProtocolMessages.NOT_FOUND + ProtocolMessages.DELIMITER);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isInLobby() {
		return inLobby;
	}
	
	public boolean isReady() {
		return ready;
	}
	
	public void ready() {
		ready = true;
	}
	
	public void unready() {
		ready = false;
	}
	
	public void joinLobby() {
		inLobby = true;
	}
	
	public void leaveLobby() {
		inLobby = false;
	}
	
	public BufferedWriter getWriter() {
		return out;
	}
	
	private void shutDown() {
		System.out.println("> [" + name + "] Shutting down.");
		try {
			in.close();
			out.close();
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		server.removeClient(this);
	}
}
