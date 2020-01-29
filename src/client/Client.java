package client;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import game.ClientPlayer;
import game.Game;
import game.HumanPlayer;
import game.Marble;
import game.Player;
import game.ServerGame;
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
    private BufferedWriter out;
    private String name;
    public boolean connected = false;
    private Game currentGame = null;

    private ServerListener SL;
    private ClientTUI TUI;

    public Client() throws IOException {
        TUI = new ClientTUI(this);
    }

    public void start() throws ExitProgram, IOException {
        try {
            Thread t;
            TUI.showMessage("Starting Abalone client...");
            while (true) {
                t = null;
                createConnection();
                TUI.handleUserInput(ProtocolMessages.CONNECT + ProtocolMessages.DELIMITER + name
                        + ProtocolMessages.DELIMITER + ProtocolMessages.DELIMITER);
                
                t = new Thread(SL);
                t.start();
                synchronized(serverSocket) {
                    serverSocket.wait();
                }
                if (connected) {
                    TUI.showMessage("Successfully connected!");
                    break;
                }
                TUI.showMessage("Failed to connect, please try again\n");
            }
            TUI.start();
        } catch (ExitProgram e) {
            TUI.showMessage("Disconnected.");
            return;
        } catch (ServerUnavailableException e) {
        } catch (InterruptedException e) {
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
            name = TUI.getString("What is your name?");
            String host = TUI.getString("What IP would you like to connect to?");
            int port = TUI.getInt("What port would you like to use? ");
            // try to open a Socket to the server
            try {
                InetAddress addr = InetAddress.getByName(host);
                TUI.showMessage("Attempting to connect to " + addr + ":" + port + "...");
                serverSocket = new Socket(addr, port);
                out = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
                SL = new ServerListener(serverSocket, this, TUI);
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
        out = null;
    }

    public void sendMessage(String msg) throws ServerUnavailableException {
        if (out != null) {
            try {
                out.write(msg);
                out.newLine();
                out.flush();
            } catch (IOException e) {
                TUI.showMessage(e.getMessage());
                throw new ServerUnavailableException("Could not write to the server");
            }
        } else {
            throw new ServerUnavailableException("Could not write to the server");
        }
    }

    public void processMove(String line) {
        String[] movesplit = line.split(ProtocolMessages.DELIMITER);
        String move = movesplit[2] + ProtocolMessages.DELIMITER + movesplit[3] + ProtocolMessages.DELIMITER
                + movesplit[4];
        Game game = server.getGame(name);
        try {
            game.getCurrentPlayer().setFields(game.getBoard(), move);
        } catch (OffBoardException e) {
            // Client should always send correct move
        }
    }
    
    //TODO
    @Override
    public void connect(String name) throws ProtocolException, ServerUnavailableException {
        sendMessage(ProtocolMessages.CONNECT + ProtocolMessages.DELIMITER + name + ProtocolMessages.DELIMITER);
    }

    @Override
    public void createLobby(String lobbyname, int size) throws ServerUnavailableException {
        sendMessage(ProtocolMessages.CREATE + ProtocolMessages.DELIMITER + lobbyname + ProtocolMessages.DELIMITER + size
                + ProtocolMessages.DELIMITER);
    }

    @Override
    public void getLobbyList() throws ServerUnavailableException {
        sendMessage(ProtocolMessages.LISTL + ProtocolMessages.DELIMITER);
    }

    @Override
    public void joinLobby(String lobby) throws ServerUnavailableException {
        sendMessage(ProtocolMessages.JOIN + ProtocolMessages.DELIMITER + lobby + ProtocolMessages.DELIMITER);
    }

    @Override
    public void leaveLobby() throws ServerUnavailableException {
        sendMessage(ProtocolMessages.LEAVE + ProtocolMessages.DELIMITER);
    }

    public void doReady() throws ServerUnavailableException {
        sendMessage(ProtocolMessages.READY + ProtocolMessages.DELIMITER);
    }

    @Override
    public void doUnready() throws ServerUnavailableException {
        sendMessage(ProtocolMessages.UNREADY + ProtocolMessages.DELIMITER);
    }

    @Override
    public void makeMove(String move) throws ServerUnavailableException {
        sendMessage(ProtocolMessages.MOVE + ProtocolMessages.DELIMITER + move + ProtocolMessages.DELIMITER);
    }

    @Override
    public void playerForfeit() throws ServerUnavailableException {
        sendMessage(ProtocolMessages.FORFEIT + ProtocolMessages.DELIMITER);
    }

    @Override
    public void getServerList() throws ServerUnavailableException {
        sendMessage(ProtocolMessages.LISTP + ProtocolMessages.DELIMITER);
    }

    @Override
    public void challengePlayer(String target) throws ServerUnavailableException {
        sendMessage(ProtocolMessages.CHALL + ProtocolMessages.DELIMITER + target + ProtocolMessages.DELIMITER);
    }

    @Override
    public void challengeAccept(String challenger) throws ServerUnavailableException {
        sendMessage(ProtocolMessages.CHALLACC + ProtocolMessages.DELIMITER + challenger + ProtocolMessages.DELIMITER);
    }

    @Override
    public void sendPM(String receiver, String message) throws ServerUnavailableException {
        sendMessage(ProtocolMessages.PM + ProtocolMessages.DELIMITER + receiver + ProtocolMessages.DELIMITER + message);
    }

    @Override
    public void sendLM(String message) throws ServerUnavailableException {
        sendMessage(ProtocolMessages.LMSG + ProtocolMessages.DELIMITER + message + ProtocolMessages.DELIMITER);
    }

    @Override
    public void getLeaderboard() throws ServerUnavailableException {
        sendMessage(ProtocolMessages.LEADERBOARD + ProtocolMessages.DELIMITER);
    }
    
    public void createGame(String line) {
        String[] sline = line.split(";");
        int playerAmount = sline.length - 1;
        Player p1 = new ClientPlayer(sline[1], Marble.BLACK);
        Player p2 = new ClientPlayer(sline[2], Marble.WHITE);
        currentGame = new ServerGame(p1, p2);
        if (playerAmount == 3) {
            p2.setMarble(Marble.BLUE);
            Player p3 = new ClientPlayer(sline[3], Marble.WHITE);
            currentGame = new ServerGame(p1, p2, p3);
        } else if (playerAmount == 4) {
            Player p3 = new ClientPlayer(sline[3], Marble.BLUE);
            Player p4 = new ClientPlayer(sline[4], Marble.RED);
            currentGame = new ServerGame(p1, p2, p3, p4);
        }
    }
    
    public void clearGame() {
        currentGame = null;
    }
    
    public Game getGame() {
        return currentGame;
    }
    
    public String getName() {
        return name;
    }

    public void shutDown() {
        try {
            out.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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