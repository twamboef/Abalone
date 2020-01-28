package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import game.Game;
import exceptions.ExitProgram;
import exceptions.OffBoardException;
import exceptions.ProtocolException;
import exceptions.ServerUnavailableException;
import protocol.ClientProtocol;
import protocol.ProtocolMessages;
import server.Server;

public class Client implements ClientProtocol {

    public Socket serverSocket;
    private Server server;
    private String name;

    ClientTUI TUI;

    public Client() {
        TUI = new ClientTUI(this);
    }

    public void start() throws ExitProgram, IOException {
        try {
            createConnection();
            TUI.handleUserInput(ProtocolMessages.CONNECT + ProtocolMessages.DELIMITER
                    + name + ProtocolMessages.DELIMITER + ProtocolMessages.DELIMITER);
            if (!TUI.in.readLine().contains("200")) {
                throw new ServerUnavailableException("Could not connect to server"
                        + " (maybe name " + name + " is already in use?)");
            }
            TUI.start();
        } catch (ExitProgram e) {
            TUI.showMessage("Disconnected.");
            return;
        } catch (ServerUnavailableException e) {
        } catch (ProtocolException e) {
        }
        if (TUI.getBoolean("ERROR: server connection broke. Try again? (y/n)")) {

        } else {
            throw new ExitProgram("User indicated to exit.");
        }
    }

    /**
     * Creates a connection to the server. Requests the IP and port to connect to at
     * the view (TUI).
     * 
     * The method continues to ask for an IP and port and attempts to connect until
     * a connection is established or until the user indicates to exit the program.
     * 
     * @throws ExitProgram if a connection is not established and the user indicates
     *                     to want to exit the program.
     * @ensures serverSock contains a valid socket connection to a server
     */
    public void createConnection() throws ExitProgram {
        clearConnection();
        while (serverSocket == null) {
            TUI.showMessage("Starting Abalone client...");
            name = TUI.getString("What is your name?");
            String host = TUI.getString("What IP would you like to connect to?");
            int port = TUI.getInt("What port would you like to use? ");
            // try to open a Socket to the server
            try {
                InetAddress addr = InetAddress.getByName(host);
                TUI.showMessage("Attempting to connect to " + addr + ":" + port + "...");
                serverSocket = new Socket(addr, port);
            } catch (IOException e) {
                TUI.showMessage("ERROR: could not create a socket on " + host + " and port " + port + ".");
                if (!TUI.getBoolean("Try again? (y/n)")) {
                    throw new ExitProgram("User indicated to exit.");
                }
            }
        }
    }

    /**
     * Resets the serverSocket and In- and OutputStreams to null.
     * 
     * Always make sure to close current connections via shutdown() before calling
     * this method!
     */
    public void clearConnection() {
        serverSocket = null;
        TUI.in = null;
        TUI.out = null;
    }

    public void sendMessage(String msg) throws ServerUnavailableException {
        if (TUI.out != null) {
            try {
                TUI.out.write(msg);
                TUI.out.newLine();
                TUI.out.flush();
            } catch (IOException e) {
                TUI.showMessage(e.getMessage());
                throw new ServerUnavailableException("Could not write to the server");
            }
        } else {
            throw new ServerUnavailableException("Could not write to the server");
        }
    }

    public String readFromServer() {
        String answer = "";
        if (TUI.in != null) {
            try {
                answer = TUI.in.readLine();
                if (answer == null) {
                    TUI.showMessage("Could not read from server");
                } else {
                    answer = "> [Server] " + answer;
                }
            } catch (IOException e) {
                TUI.showMessage("Could not read from server");
            }
        }
        return answer;
    }

    public void processMove(String line) {
        String[] movesplit = line.split(ProtocolMessages.DELIMITER);
        String move = movesplit[2] + ProtocolMessages.DELIMITER + movesplit[3] + ProtocolMessages.DELIMITER
                + movesplit[4];
        Game game = server.getGame(name);
        try {
            game.currentPlayer().setFields(game.getBoard(), move);
        } catch (OffBoardException e) {
            // Client should always send correct move
        }
    }

    @Override
    public void connect(String name) throws ProtocolException, ServerUnavailableException {
        sendMessage(ProtocolMessages.CONNECT + ProtocolMessages.DELIMITER + name + ProtocolMessages.DELIMITER);
        String line = readFromServer();
        if (!line.contains(ProtocolMessages.CONNECT) || !line.contains("200")) {
            throw new ProtocolException("Server didn't allow to connect");
        }
        TUI.showMessage(line);
    }

    @Override
    public void createLobby(String lobbyname, int size) throws ServerUnavailableException {
        sendMessage(ProtocolMessages.CREATE + ProtocolMessages.DELIMITER + lobbyname + ProtocolMessages.DELIMITER + size
                + ProtocolMessages.DELIMITER);
        TUI.showMessage(readFromServer());
    }

    @Override
    public void getLobbyList() throws ServerUnavailableException {
        sendMessage(ProtocolMessages.LISTL + ProtocolMessages.DELIMITER);
        TUI.showMessage(readFromServer());
    }

    @Override
    public void joinLobby(String lobby) throws ServerUnavailableException {
        sendMessage(ProtocolMessages.JOIN + ProtocolMessages.DELIMITER + lobby + ProtocolMessages.DELIMITER);
        TUI.showMessage(readFromServer());
    }

    @Override
    public void leaveLobby() throws ServerUnavailableException {
        sendMessage(ProtocolMessages.LEAVE + ProtocolMessages.DELIMITER);
        TUI.showMessage(readFromServer());
    }

    public void doReady() throws ServerUnavailableException {
        sendMessage(ProtocolMessages.READY + ProtocolMessages.DELIMITER);
        TUI.showMessage(readFromServer());
    }

    @Override
    public void doUnready() throws ServerUnavailableException {
        sendMessage(ProtocolMessages.UNREADY + ProtocolMessages.DELIMITER);
        TUI.showMessage(readFromServer());
    }

    @Override
    public void makeMove(String move) throws ServerUnavailableException {
        sendMessage(ProtocolMessages.MOVE + ProtocolMessages.DELIMITER + move + ProtocolMessages.DELIMITER);
        TUI.showMessage(readFromServer());
    }

    @Override
    public void playerForfeit() throws ServerUnavailableException {
        sendMessage(ProtocolMessages.FORFEIT + ProtocolMessages.DELIMITER);
        TUI.showMessage(readFromServer());
    }

    @Override
    public void getServerList() throws ServerUnavailableException {
        sendMessage(ProtocolMessages.LISTP + ProtocolMessages.DELIMITER);
        TUI.showMessage(readFromServer());
    }

    @Override
    public void challengePlayer(String target) throws ServerUnavailableException {
        sendMessage(ProtocolMessages.CHALL + ProtocolMessages.DELIMITER + target + ProtocolMessages.DELIMITER);
        TUI.showMessage(readFromServer());
    }

    @Override
    public void challengeAccept(String challenger) throws ServerUnavailableException {
        sendMessage(ProtocolMessages.CHALLACC + ProtocolMessages.DELIMITER + challenger + ProtocolMessages.DELIMITER);
        TUI.showMessage(readFromServer());
    }

    @Override
    public void sendPM(String receiver, String message) throws ServerUnavailableException {
        sendMessage(ProtocolMessages.PM + ProtocolMessages.DELIMITER + receiver + ProtocolMessages.DELIMITER + message);
        TUI.showMessage(readFromServer());
    }

    @Override
    public void sendLM(String message) throws ServerUnavailableException {
        sendMessage(ProtocolMessages.LMSG + ProtocolMessages.DELIMITER + message + ProtocolMessages.DELIMITER);
        TUI.showMessage(readFromServer());
    }

    @Override
    public void getLeaderboard() throws ServerUnavailableException {
        sendMessage(ProtocolMessages.LEADERBOARD + ProtocolMessages.DELIMITER);
        TUI.showMessage(readFromServer());
    }

    /**
     * This method starts a new Client.
     * 
     * @param args
     * @throws IOException
     * @throws ExitProgram
     */
    public static void main(String[] args) throws ExitProgram, IOException {
        (new Client()).start();
    }

}