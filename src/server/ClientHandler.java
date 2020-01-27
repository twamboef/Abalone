package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import protocol.ProtocolMessages;

public class ClientHandler implements Runnable {
    private BufferedReader in;
    private BufferedWriter out;
    private Socket sock;
    private Server server;
    private String name;
    private int points = 0;
    private boolean connected = false;
    private boolean inLobby = false;
    private boolean ready = false;

    /**
     * Constructor for the ClientHandler.
     * 
     * @param sock   socket
     * @param server server to connect to
     */
    public ClientHandler(Socket sock, Server server) {
        try {
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
            this.sock = sock;
            this.server = server;
        } catch (IOException e) {
            shutDown();
        }
    }

    /**
     * Keeps waiting for user input and handles it.
     */
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
        } catch (IOException e) {
            shutDown();
        }
    }

    private void handleCommand(String message) throws IOException {
        String[] splitmsg = message.split(ProtocolMessages.DELIMITER);
        final String command = splitmsg[0];
        String parm1 = null;
        String parm2 = null;
        String parm3 = null;
        if (splitmsg.length > 1) {
            parm1 = splitmsg[1];
        }
        if (splitmsg.length > 2) {
            parm2 = splitmsg[2];
        }
        if (splitmsg.length > 3) {
            parm3 = splitmsg[3];
        }
        if (connected == false && !command.equals("CONNECT")) {
            out.write(
                    command + ProtocolMessages.DELIMITER + ProtocolMessages.UNAUTHORIZED + ProtocolMessages.DELIMITER);
            return;
        }
        switch (command) {
            case ProtocolMessages.CONNECT:
                name = parm1;
                String result = server.getConnect(name);
                if (result.split(ProtocolMessages.DELIMITER)[1].equals("200")) {
                    connected = true;
                }
                out.write(result);
                break;
            case ProtocolMessages.CREATE:
                out.write(server.createLobby(parm1, name, parm2));
                break;
            case ProtocolMessages.LISTL:
                out.write(server.getLobbyList());
                break;
            case ProtocolMessages.JOIN:
                result = server.joinLobby(name, parm1);
                out.write(result);
                if (result.contains("200")) {
                    joinLobby();
                }
                break;
            case ProtocolMessages.LEAVE:
                result = server.leaveLobby(name);
                out.write(result);
                if (result.contains("200")) {
                    leaveLobby();
                }
                break;
            case ProtocolMessages.READY:
                out.write(server.doReady(name));
                break;
            case ProtocolMessages.UNREADY:
                out.write(server.doUnready(name));
                break;
            case ProtocolMessages.MOVE:
                String move;
                result = server.makeMove(name,
                        (move = parm1 + ProtocolMessages.DELIMITER + parm2 + ProtocolMessages.DELIMITER + parm3));
                out.write(result);
                if (result.contains("200")) {
                    writeToGameClients(server.sendMove(name, move));
                }
                break;
            case ProtocolMessages.FORFEIT:
                out.write(server.playerForfeit(name));
                server.getGame(name).playerForfeit(name);
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
                out.write(command + ProtocolMessages.DELIMITER 
                        + ProtocolMessages.NOT_FOUND + ProtocolMessages.DELIMITER);
                break;
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

//    // TODO UNDERLYING METHOD SHOULD BE IN CLIENT
//    /**
//     * Called by another ClientHandler, allowing this ClientHandler to change the
//     * board according to a move.
//     * 
//     * @param line from another player containing move, e.g. "MOVE;Twan;5,G;7,G;4;"
//     */
//    public void processMove(String line) {
//        String[] movesplit = line.split(ProtocolMessages.DELIMITER);
//        String move = movesplit[2] + ProtocolMessages.DELIMITER + movesplit[3] + ProtocolMessages.DELIMITER
//                + movesplit[4];
//        Game game = server.getGame(name);
//        try {
//            game.currentPlayer().setFields(game.getBoard(), move);
//        } catch (OffBoardException e) {
//            // Client should always send correct move
//        }
//    }
    
    private void writeToGameClients(String line) throws IOException {
        List<ClientHandler> clientHandlers = new ArrayList<ClientHandler>();
        for (String p : server.getLobby(name).getPlayers()) {
            if (!p.equals(name)) {
                clientHandlers.add(server.getClientHandler(p));
            }
        }
        for (ClientHandler ch : clientHandlers) {
            ch.writeLine(line);
        }
    }
    
    public void writeLine(String line) throws IOException {
        out.write(line);
    }
    
    public void addPoints(int amount) {
        points += amount;
    }
    
    public int getPoints() {
        return points;
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
