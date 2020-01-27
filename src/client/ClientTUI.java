package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import exceptions.ExitProgram;

public class ClientTUI implements ClientView{
	Client client;
	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public ClientTUI(Client cl) {
		this.client = cl;
	}

	public void start() {
		while(true) {
			handleUserInput(getString("command: "));
		}
		
	}

	public void handleUserInput(String input) {
		String[] clinput = input.split(";");
		String command = clinput[0];
		String temp1 = "", temp2 = "", temp3 = "";
		if (clinput.length > 1) {
			temp1 = clinput[1];
		}
		if (clinput.length > 2) {
			temp2 = clinput[2];
		}
		if (clinput.length > 3) {
			temp3 = clinput[3];
		}
		switch(command) {
		case"CREATE_LOBBY":
			if(temp1.equals("")) {
				showMessage("Please also enter a name and size");
				break;
			}
			if (!temp1.equals("") && temp2.equals("")) {
				showMessage("Please also enter a size");
				break;
			}
			else {
				try{
					int tempInt2 = Integer.parseInt(temp2);
					client.createLobby(temp1, tempInt2);
					break;
				} catch(NumberFormatException e) {
					showMessage("Please enter a valid size");
					break;
				}
			}
		case"LIST_LOBBY":
			client.getLobbyList();
			break;
		case"JOIN_LOBBY":
			if(temp1.equals("")) {
				showMessage("Please also enter a valid name");
				break;
				}
			else {
				client.joinLobby(temp1);
				break;
			}
		case"LEAVE_LOBBY":
				client.leaveLobby();
				break;
		case"READY_LOBBY":
				client.doReady();
				break;
		case"UNREADY_LOBBY":
				client.doUnready();
				break;
		case"CONNECT":
			if(temp1.equals("")) {
				showMessage("Please also enter a name");
				break;
			}
			else {
				client.connect(temp1);
				break;
			}
		case"MOVE":
			if(!temp3.equals("")) {
				String move = temp1 + temp2 + temp3;
				client.makeMove(move);
				break;
			}
			else {
				showMessage("Please enter a valid move");
				break;
			}
		case"FORFEIT":
			client.playerForfeit();
			break;
		case"LIST_PLAYERS":
			client.getServerList();
			break;
		case"CHALLENGE":
			if(temp1.equals("")) {
				showMessage("Please enter a valid player");
				break;
			}
			else{
				client.challengePlayer(temp1);
				break;
			}
		case"CHALLENGE_ACCEPT":
			if(temp1.equals("")) {
				showMessage("Please enter a valid challenger");
				break;
			}
			else {
				client.challengeAccept(temp1);
				break;
			}
		case"PM":
			if(temp1.equals("")) {
				showMessage("Please enter a valid player and message");
				break;
			}
			if(temp2.equals("")) {
				showMessage("Please enter a valid player and message");
				break;
			}
			else{
				client.sendPM(temp1, temp2);
				break;
			}
		case"LOBBY_MSG":
			if(temp1.equals("")) {
				showMessage("Please also enter a message");
				break;
			}
			else{
				client.sendLM(temp1);
				break;
			}
		case"LEADERBOARD":
			client.getLeaderboard();
			break;
		default:
			showMessage("Please enter a valid command");
			break;
		}
	}

	public void showMessage(String msg) {
		System.out.println(msg);		
	}

	public InetAddress getIP() {
		try {
			return InetAddress.getByName(getString("Please enter an IP"));
		}
		catch(UnknownHostException e){
			showMessage("Invalid IP, please try again");
			return getIP();
		}
	}

	public String getString(String question) {
		showMessage(question);
		String line = "";
		try {
			line = in.readLine();
			return line;
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return line;
	}

	public int getInt(String question) {
		int i = -1;
		try {
			i = Integer.parseInt(getString(question));
		}
		catch(NumberFormatException e) {
			e.printStackTrace();
		}
		return i;
	}

	@Override
	public Boolean getBoolean(String question) {
		String line;
		if((line = getString(question)).equals("y")) {
			return true;
		}
		if((line = getString(question)).equals("n")) {
			return false;
		}
		else {
			showMessage("Please enter y/n");
			return getBoolean(question);
		}
	}

	
	
}