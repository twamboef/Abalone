package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import game.Game;
import exceptions.OffBoardException;
import protocol.ClientProtocol;
import protocol.ProtocolMessages;
import server.Server;

public class Client implements ClientProtocol{
	
	private Socket serverSocket;
	private BufferedReader in;
	private BufferedWriter out;
	private Server server;
	private String name;
	
	ClientTUI client;
	
	public Client() {
		client = new ClientTUI(this);
	}
	
	public void sendMessage(String msg) {
		if(out!=null) {
			try {
				out.write(msg);
				out.newLine();
				out.flush();	
			}
			catch(IOException e) {
				client.showMessage("Could not write to the server");
			}
		}
		else {
			client.showMessage("Could not write to the server");
		}
	}
	
	public String readFromServer() {
		String answer = "";
		if(in!=null) {
			try {
				answer = in.readLine();
				if(answer == null) {
					client.showMessage("Could not read from server");
				}
				else {
					answer = "> " + answer;
				}
			}
			catch(IOException e){
				client.showMessage("Could not read from server");
			}
		}
		return answer;
	}
	
	public void processMove(String line) {
		String[] movesplit = line.split(ProtocolMessages.DELIMITER);
		String move = movesplit[2] + ProtocolMessages.DELIMITER + movesplit[3] 
				+ ProtocolMessages.DELIMITER + movesplit[4];
		Game game = server.getGame(name);
		try {
			game.currentPlayer().setFields(game.getBoard(), move);
		} catch (OffBoardException e) {
			//Client should always send correct move
		}
	}

	@Override
	public void connect(String name) {
		sendMessage(ProtocolMessages.CONNECT + ProtocolMessages.DELIMITER + name + ProtocolMessages.DELIMITER);
		client.showMessage(readFromServer());
	}

	@Override
	public void createLobby(String lobbyname, int size) {
		sendMessage(ProtocolMessages.CREATE + ProtocolMessages.DELIMITER + name + ProtocolMessages.DELIMITER + size + ProtocolMessages.DELIMITER);
		client.showMessage(readFromServer());
	}

	@Override
	public void getLobbyList() {
		sendMessage(ProtocolMessages.LISTL + ProtocolMessages.DELIMITER);
		client.showMessage(readFromServer());		
	}

	@Override
	public void joinLobby(String lobby) {
		sendMessage(ProtocolMessages.JOIN + ProtocolMessages.DELIMITER + lobby + ProtocolMessages.DELIMITER);
		client.showMessage(readFromServer());
	}

	@Override
	public void leaveLobby() {
		sendMessage(ProtocolMessages.LEAVE + ProtocolMessages.DELIMITER);
		client.showMessage(readFromServer());		
	}
	
	public void doReady() {
		sendMessage(ProtocolMessages.READY + ProtocolMessages.DELIMITER);
		client.showMessage(readFromServer());
	}

	@Override
	public void doUnready() {
		sendMessage(ProtocolMessages.UNREADY + ProtocolMessages.DELIMITER);
		client.showMessage(readFromServer());
	}

	@Override
	public void makeMove(String move) {
		sendMessage(ProtocolMessages.MOVE + ProtocolMessages.DELIMITER + move + ProtocolMessages.DELIMITER);
		client.showMessage(readFromServer());
	}

	@Override
	public void playerForfeit() {
		sendMessage(ProtocolMessages.FORFEIT + ProtocolMessages.DELIMITER);
		client.showMessage(readFromServer());
	}

	@Override
	public void getServerList() {
		sendMessage(ProtocolMessages.LISTP + ProtocolMessages.DELIMITER);
		client.showMessage(readFromServer());
	}

	@Override
	public void challengePlayer(String target) {
		sendMessage(ProtocolMessages.CHALL + ProtocolMessages.DELIMITER + target + ProtocolMessages.DELIMITER);
		client.showMessage(readFromServer());
	}

	@Override
	public void challengeAccept(String challenger) {
		sendMessage(ProtocolMessages.CHALLACC + ProtocolMessages.DELIMITER + challenger + ProtocolMessages.DELIMITER);
		client.showMessage(readFromServer());
	}

	@Override
	public void sendPM(String receiver, String message) {
		sendMessage(ProtocolMessages.PM + ProtocolMessages.DELIMITER + receiver + ProtocolMessages.DELIMITER + message);
		client.showMessage(readFromServer());
	}

	@Override
	public void sendLM(String message) {
		sendMessage(ProtocolMessages.LMSG + ProtocolMessages.DELIMITER + message + ProtocolMessages.DELIMITER);
		client.showMessage(readFromServer());
	}

	@Override
	public void getLeaderboard() {
		sendMessage(ProtocolMessages.LEADERBOARD + ProtocolMessages.DELIMITER);
		client.showMessage(readFromServer());
	}

	
	
}