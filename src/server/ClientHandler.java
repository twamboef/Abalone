package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import game.Board;
import game.Player;
import protocol.ProtocolMessages;

public class ClientHandler implements Runnable {
    private BufferedReader in;
    private BufferedWriter out;
    private Socket sock;
    private Server server;
    private int points = 0;
    private String name;
    private boolean connected = false;
    private boolean inLobby = false;
    private boolean ready = false;
    public String challengedBy = "";

    /**
     * Constructor for the ClientHandler.
     * 
     * @param sock   socket
     * @param server server to connect to
     */
    public ClientHandler(Socket sock, Server server, String name) {
        try {
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
            this.name = name;
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
                System.out.println("> [" + name + "] " + message);
                handleCommand(message);
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
            writeLine(
                    command + ProtocolMessages.DELIMITER + ProtocolMessages.UNAUTHORIZED + ProtocolMessages.DELIMITER);
            return;
        }
        switch (command) {
            case ProtocolMessages.CONNECT:
                String result;
                if ((result = server.getConnect(parm1)).contains("200")) {
                    connected = true;
                    name = parm1;
                }
                writeLine(result);
                break;
            case ProtocolMessages.CREATE:
                writeLine(server.createLobby(parm1, name, parm2));
                break;
            case ProtocolMessages.LISTL:
                writeLine(server.getLobbyList());
                break;
            case ProtocolMessages.JOIN:
                writeLine(result = server.joinLobby(name, parm1));
                if (result.contains("200")) {
                    joinLobby();
                    Lobby lobby = server.getLobby(name);
                    ClientHandler ch;
                    for (String player : lobby.getPlayers()) {
                        if ((ch = server.getClientHandler(player)) != this) {
                            ch.writeLine(server.lobbyChanged(lobby));
                        }
                    }
                }
                break;
            case ProtocolMessages.LEAVE:
                writeLine(result = server.leaveLobby(name));
                if (result.contains("200")) {
                    leaveLobby();
                }
                break;
            case ProtocolMessages.READY:
                writeLine(result = server.doReady(name));
                if (result.contains("200")) {
                    Lobby lobby = server.getLobby(name);
                    for (String p : lobby.getPlayers()) {
                        server.getClientHandler(p).writeLine(server.readyChange(lobby));
                    }
                    if (!lobby.isReady()) {
                        break;
                    }
                    try {
                        for (String p : lobby.getPlayers()) {
                            server.getClientHandler(p).writeLine(server.startGame(lobby));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case ProtocolMessages.UNREADY:
                writeLine(result = server.doUnready(name));
                if (result.contains("200")) {
                    Lobby lobby = server.getLobby(name);
                    for (String p : lobby.getPlayers()) {
                        server.getClientHandler(p).writeLine(server.readyChange(lobby));
                    }
                }
                break;
            case ProtocolMessages.MOVE:
                Player playr = server.getGame(name).getCurrentPlayer();
                Board brd = server.getGame(name).getBoard();
                String move = playr.makeGoodFormat(brd, parm1) + ProtocolMessages.DELIMITER + playr.makeGoodFormat(brd, parm2)
                + ProtocolMessages.DELIMITER + parm3;
                writeLine(result = server.makeMove(name, move));
                String line = server.sendMove(name, move);
                writeLine(line);
                if (result.contains("200")) {
                    writeToGameClients(line);
                }
                break;
            case ProtocolMessages.FORFEIT:
                writeLine(server.playerForfeit(name));
                server.getGame(name).playerForfeit(name);
                break;
            case ProtocolMessages.LISTP:
                writeLine(server.getServerList());
                break;
            case ProtocolMessages.CHALL:
                writeLine(result = server.challengePlayer(name, parm1));
                if (result.contains("200")) {
                    server.getClientHandler(parm1).writeLine(server.sendChallenge(name));
                }
                break;
            case ProtocolMessages.CHALLACC:
                writeLine(result = server.challengeAccept(name, parm1));
                if (result.contains("200")) {
                    server.getClientHandler(parm1).writeLine(server.sendChallengeAccept(name));
                }
                break;
            case ProtocolMessages.PM:
                writeLine(result = server.sendPM(parm1, parm2));
                if (result.contains("200")) {
                    server.getClientHandler(parm1).writeLine(server.receivePM(name, parm2));
                }
                break;
            case ProtocolMessages.LMSG:
                writeLine(result = server.sendLM(name, parm1));
                if (result.contains("200")) {
                    for (String p : server.getLobby(name).getPlayers()) {
                        server.getClientHandler(p).writeLine(server.receiveLM(name, parm1));
                    }
                }
                break;
            case ProtocolMessages.LEADERBOARD:
                writeLine(server.getLeaderboard());
                break;
            default:
                writeLine(command + ProtocolMessages.DELIMITER 
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
        System.out.println("--> Sent to " + name + ": "  + line);
        out.write(line);
        out.newLine();
        out.flush();
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
        server.leaveLobby(name);
        if (server.getGame(name) != null) {
            server.getGame(name).playerForfeit(name);
        }
        server.removeClient(this);
    }
}
