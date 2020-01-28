package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

import exceptions.ProtocolException;
import exceptions.ServerUnavailableException;
import protocol.ProtocolMessages;

public class ClientTUI implements ClientView {
    Client client;
    private BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));
    public BufferedReader in;
    public BufferedWriter out;

    public ClientTUI(Client cl) throws IOException {
        this.client = cl;
        in = new BufferedReader(new InputStreamReader(client.serverSocket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(client.serverSocket.getOutputStream()));
    }

    public void start() throws ServerUnavailableException, ProtocolException, IOException {
        while (true) {
            String line = null;
            if (systemIn.ready()) {
                line = systemIn.readLine();
                out.write(line);
            }
            while (in.ready()) {
                handleServerMessage(in.readLine());
            }
            if (line != null) {
                showMessage("Please enter a command.");
            }
        }
    }

    public void handleServerMessage(String serverMessage) {
        String[] message = serverMessage.split(ProtocolMessages.DELIMITER);
        String command = message[0];
        String parm1 = message[1];
        switch (command) {
        case ProtocolMessages.CONNECT:
            showMessage("Successfully connected");
        }
    }

    public void handleUserInput(String input) throws ServerUnavailableException, ProtocolException {
        String[] clinput = input.split(ProtocolMessages.DELIMITER);
        String command = clinput[0];
        String temp1 = "";
        String temp2 = "";
        String temp3 = "";
        if (clinput.length > 1) {
            temp1 = clinput[1];
        }
        if (clinput.length > 2) {
            temp2 = clinput[2];
        }
        if (clinput.length > 3) {
            temp3 = clinput[3];
        }
        switch (command) {
        case "CREATE_LOBBY":
            if (temp1.equals("")) {
                showMessage("Please also enter a name and size");
                break;
            }
            if (!temp1.equals("") && temp2.equals("")) {
                showMessage("Please also enter a size");
                break;
            } else {
                try {
                    int tempInt2 = Integer.parseInt(temp2);
                    client.createLobby(temp1, tempInt2);
                    break;
                } catch (NumberFormatException e) {
                    showMessage("Please enter a valid size");
                    break;
                }
            }
        case "LIST_LOBBY":
            client.getLobbyList();
            break;
        case "JOIN_LOBBY":
            if (temp1.equals("")) {
                showMessage("Please also enter a valid name");
                break;
            } else {
                client.joinLobby(temp1);
                break;
            }
        case "LEAVE_LOBBY":
            client.leaveLobby();
            break;
        case "READY_LOBBY":
            client.doReady();
            break;
        case "UNREADY_LOBBY":
            client.doUnready();
            break;
        case "CONNECT":
            if (temp1.equals("")) {
                showMessage("Please also enter a name");
                break;
            } else {
                client.connect(temp1);
                break;
            }
        case "MOVE":
            if (!temp3.equals("")) {
                String move = temp1 + temp2 + temp3;
                client.makeMove(move);
                break;
            } else {
                showMessage("Please enter a valid move");
                break;
            }
        case "FORFEIT":
            client.playerForfeit();
            break;
        case "LIST_PLAYERS":
            client.getServerList();
            break;
        case "CHALLENGE":
            if (temp1.equals("")) {
                showMessage("Please enter a valid player");
                break;
            } else {
                client.challengePlayer(temp1);
                break;
            }
        case "CHALLENGE_ACCEPT":
            if (temp1.equals("")) {
                showMessage("Please enter a valid challenger");
                break;
            } else {
                client.challengeAccept(temp1);
                break;
            }
        case "PM":
            if (temp1.equals("")) {
                showMessage("Please enter a valid player and message");
                break;
            }
            if (temp2.equals("")) {
                showMessage("Please enter a valid player and message");
                break;
            } else {
                client.sendPM(temp1, temp2);
                break;
            }
        case "LOBBY_MSG":
            if (temp1.equals("")) {
                showMessage("Please also enter a message");
                break;
            } else {
                client.sendLM(temp1);
                break;
            }
        case "LEADERBOARD":
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
        } catch (UnknownHostException e) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    public int getInt(String question) {
        int i = -1;
        try {
            i = Integer.parseInt(getString(question));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return i;
    }

    @Override
    public Boolean getBoolean(String question) {
        String line;
        if ((line = getString(question)).equals("y")) {
            return true;
        }
        if (line.equals("n")) {
            return false;
        } else {
            showMessage("Please enter y/n");
            return getBoolean(question);
        }
    }

}