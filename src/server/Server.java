package server;

import exceptions.ExitProgram;
import exceptions.OffBoardException;
import game.ClientPlayer;
import game.Game;
import game.Marble;
import game.Player;
import game.ServerGame;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import protocol.ProtocolMessages;
import protocol.ServerProtocol;

public class Server implements Runnable, ServerProtocol {
    private ServerSocket ssock;
    private List<ClientHandler> clients;
    private List<Lobby> lobbies;
    private List<ServerGame> games;
    private ServerTUI view;
    private int next_client_no;

    /**
     * String consisting of ";200;".
     */
    private String delimSuccess = ProtocolMessages.DELIMITER + ProtocolMessages.SUCCESS + ProtocolMessages.DELIMITER;

    /**
     * Constructor for this class.
     */
    public Server() {
        clients = new ArrayList<>();
        lobbies = new ArrayList<>();
        games = new ArrayList<>();
        view = new ServerTUI();
        next_client_no = 1;
    }

    @Override
    public void run() {
        boolean openNewSocket = true;
        while (openNewSocket) {
            try {
                setup();
                while (true) {
                    Socket sock = ssock.accept();
                    String name = "NewClient " 
                            + String.format("%02d", next_client_no++);
                    view.showMessage("[" + name + "] connected!");
                    ClientHandler handler = new ClientHandler(sock, this, name);
                    new Thread(handler).start();
                    clients.add(handler);
                }
            } catch (ExitProgram e) {
                openNewSocket = false;
            } catch (IOException e) {
                System.out.println("A server IO error occurred: " + e.getMessage());
                if (!view.getBoolean("Do you want to open a new socket?")) {
                    openNewSocket = false;
                }
            }
        }
        view.showMessage("Already miss you!");
    }

    /**
     * Sets up the server.
     * 
     * @throws ExitProgram when user indicates to exit the program.
     */
    public void setup() throws ExitProgram {
        ssock = null;
        while (ssock == null) {
            int port = view.getInt("What port would you like to use?");
            try { // try to open a new ServerSocket
                view.showMessage("Attempting to open a socket on port " + port + "...");
                ssock = new ServerSocket(port, 0, InetAddress.getByName("0.0.0.0"));
                view.showMessage("Server started at port " + port);
            } catch (IOException e) {
                view.showMessage("ERROR: cannot create a socket on port " + port + ".");
                if (!view.getBoolean("Do you want to try again?")) {
                    throw new ExitProgram("User indicated to exit the program.");
                }
            }
        }
    }

    /**
     * Returns the clientHandler using the name.
     * 
     * @requires only one clientHandler exists with this name
     * @param name of a clientHandler
     * @return clientHandler if found, null if not found
     */
    public ClientHandler getClientHandler(String name) {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler != null && clientHandler.getName().equals(name)) {
                return clientHandler;
            }
        }
        return null;
    }

    /**
     * Returns the lobby a clientHandler is in.
     * 
     * @param name of a clientHandler
     * @return lobby of clientHandler if in one, or null if not
     */
    public Lobby getLobby(String name) {
        for (Lobby lobby : lobbies) {
            for (String player : lobby.getPlayers()) {
                if (player.equals(name)) {
                    return lobby;
                }
            }
        }
        return null;
    }

    private boolean isInLobby(String name) {
        return getClientHandler(name).isInLobby();
    }

    @Override
    public String getConnect(String name) {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler != null && clientHandler.getName() != null && clientHandler.getName().equals(name)) {
                return ProtocolMessages.CONNECT + ProtocolMessages.DELIMITER + ProtocolMessages.FORBIDDEN
                        + ProtocolMessages.DELIMITER;
            }
        }
        clients.add(getClientHandler(name));
        return ProtocolMessages.CONNECT + delimSuccess + ProtocolMessages.DELIMITER;
    }

    @Override
    public String createLobby(String lobbyname, String player, String lobbysize) {
        int size;
        try {
            size = Integer.parseInt(lobbysize);
        } catch (NumberFormatException e) {
            return ProtocolMessages.CREATE + ProtocolMessages.DELIMITER + ProtocolMessages.MALFORMED
                    + ProtocolMessages.DELIMITER;
        }
        if (lobbyname == null || lobbyname.equals("")) {
            return ProtocolMessages.CREATE + ProtocolMessages.DELIMITER + ProtocolMessages.MALFORMED
                    + ProtocolMessages.DELIMITER;
        }
        if (size < 2 || size > 4) {
            return ProtocolMessages.CREATE + ProtocolMessages.DELIMITER + ProtocolMessages.FORBIDDEN
                    + ProtocolMessages.DELIMITER;
        }
        for (Lobby lobby : lobbies) {
            if (lobby.getName().equals(lobbyname)) {
                return ProtocolMessages.CREATE + ProtocolMessages.DELIMITER + ProtocolMessages.FORBIDDEN
                        + ProtocolMessages.DELIMITER;
            }
        }
        Lobby lobby = new Lobby(lobbyname, size);
        lobby.join(player);
        getClientHandler(player).joinLobby();
        lobbies.add(lobby);
        return ProtocolMessages.CREATE + delimSuccess;
    }

    @Override
    public String getLobbyList() {
        String result = ProtocolMessages.LISTL + delimSuccess;
        for (Lobby lobby : lobbies) {
            if (lobby.isJoinable()) {
                result += lobby.getName() + ProtocolMessages.SEPERATOR + lobby.getSize() + ProtocolMessages.SEPERATOR
                        + lobby.getPlayers().size() + ProtocolMessages.DELIMITER;
            }
        }
        return result;
    }

    @Override
    public String joinLobby(String name, String lobbyname) {
        if (lobbyname == null || lobbyname.equals("")) {
            return ProtocolMessages.JOIN + ProtocolMessages.DELIMITER + ProtocolMessages.MALFORMED
                    + ProtocolMessages.DELIMITER;
        }
        for (Lobby lobby : lobbies) {
            if (lobby.getName().equals(lobbyname) && lobby.isJoinable() && !isInLobby(name)) {
                lobby.join(name);
                getClientHandler(name).joinLobby();
                String result = ProtocolMessages.JOIN + delimSuccess;
                for (String p : lobby.getPlayers()) {
                    result += p + ProtocolMessages.DELIMITER;
                }
                return result;
            }
        }
        return ProtocolMessages.JOIN + ProtocolMessages.DELIMITER + ProtocolMessages.FORBIDDEN
                + ProtocolMessages.DELIMITER;
    }

    @Override
    public String leaveLobby(String name) {
        Lobby lobby = getLobby(name);
        if (lobby != null) {
            lobby.leave(name);
            getClientHandler(name).leaveLobby();
            if (lobby.getPlayers().size() == 0) {
                lobbies.remove(lobby);
            }
            return ProtocolMessages.LEAVE + delimSuccess;
        }
        return ProtocolMessages.LEAVE + ProtocolMessages.DELIMITER + ProtocolMessages.FORBIDDEN
                + ProtocolMessages.DELIMITER;
    }

    @Override
    public String doReady(String name) {
        ClientHandler clientHandler = getClientHandler(name);
        if (clientHandler.isInLobby() && !clientHandler.isReady()) {
            clientHandler.ready();
            Lobby lobby = getLobby(name);
            lobby.ready();
            return ProtocolMessages.READY + delimSuccess;
        }
        return ProtocolMessages.READY + ProtocolMessages.DELIMITER + ProtocolMessages.FORBIDDEN
                + ProtocolMessages.DELIMITER;
    }
    
    public String readyChange(Lobby lobby) {
        return ProtocolMessages.READY + ProtocolMessages.DELIMITER 
        + lobby.getReadyAmount() + ProtocolMessages.DELIMITER;
    }

    @Override
    public String doUnready(String name) {
        ClientHandler clientHandler = getClientHandler(name);
        if (clientHandler.isInLobby() && clientHandler.isReady()) {
            clientHandler.unready();
            getLobby(name).unready();
            return ProtocolMessages.UNREADY + delimSuccess;
        }
        return ProtocolMessages.UNREADY + ProtocolMessages.DELIMITER + ProtocolMessages.FORBIDDEN
                + ProtocolMessages.DELIMITER;
    }

    @Override
    public String lobbyChanged(Lobby lobby) {
        String result = ProtocolMessages.CHANGE + ProtocolMessages.DELIMITER;
        for (String p : lobby.getPlayers()) {
            result += p + ProtocolMessages.DELIMITER;
        }
        return result;
    }

    @Override
    public String startGame(Lobby lobby) {
        ServerGame game = createGame(lobby);
        new Thread(game).start();
        games.add(game);
        for (String p : lobby.getPlayers()) {
            getClientHandler(p).unready();
        }
        String result = ProtocolMessages.START + ProtocolMessages.DELIMITER;
        for (String p : lobby.getPlayers()) {
            result += p + ProtocolMessages.DELIMITER;
        }
        return result;
    }

    /**
     * Creates a new game to be added to the list of games (or sent to clients).
     * 
     * @param lobby with players for the game
     * @requires lobby.getSize() == lobby.getPlayers().length
     * @return newly made game
     */
    private ServerGame createGame(Lobby lobby) {
        Player p1 = new ClientPlayer(lobby.getPlayers().get(0), Marble.BLACK);
        Player p2 = new ClientPlayer(lobby.getPlayers().get(1), Marble.WHITE);
        ServerGame game = new ServerGame(p1, p2);
        if (lobby.getSize() == 3) {
            p2.setMarble(Marble.BLUE);
            Player p3 = new ClientPlayer(lobby.getPlayers().get(2), Marble.WHITE);
            game = new ServerGame(p1, p2, p3);
        } else if (lobby.getSize() == 4) {
            Player p3 = new ClientPlayer(lobby.getPlayers().get(2), Marble.BLUE);
            Player p4 = new ClientPlayer(lobby.getPlayers().get(3), Marble.RED);
            game = new ServerGame(p1, p2, p3, p4);
        }
        return game;
    }

    @Override
    public String makeMove(String name, String move) {
        try {
            view.showMessage(getGame(name).getCurrentPlayer().getName());
            if (getGame(name) == null || !getGame(name).getCurrentPlayer().getName().equals(name) || !getGame(name).getBoard().isValidMove(getGame(name).getCurrentPlayer(), 
                    getGame(name).getCurrentPlayer().makeLeadingFirst(getGame(name).getBoard(), move))) {
                return ProtocolMessages.MOVE + ProtocolMessages.DELIMITER + ProtocolMessages.FORBIDDEN
                        + ProtocolMessages.DELIMITER;
            }
            ClientPlayer p = (ClientPlayer) getGame(name).getCurrentPlayer();
            p.makeMove(getGame(name).getBoard(), move);
            view.showMessage(getGame(name).getBoard().toString());
            synchronized (getGame(name).moveHappened) {
                getGame(name).moveHappened.notifyAll();
            }
        } catch (OffBoardException e) {
            e.printStackTrace();
            // Can't happen because isValidMove is first called
        }
        return ProtocolMessages.MOVE + delimSuccess;
    }

    @Override
    public String sendMove(String name, String move) {
        return ProtocolMessages.MOVE + ProtocolMessages.DELIMITER + name + ProtocolMessages.DELIMITER + move
                + ProtocolMessages.DELIMITER;
    }

    @Override
    public String gameFinish(Game game) {
        lobbies.remove(getLobby(game.getCurrentPlayer().getName()));
        String winner;
        try {
            winner = game.getWinner().getName();
        } catch (NullPointerException e) {
            winner = "";
        }
        if (!winner.equals("")) {
            getClientHandler(winner).addPoints(3);
            if (game.getPlayerAmount() == 4) {
                String teamMate = null;
                for (int i = 0; i < 4; i++) {
                    if (game.getPlayers()[i].getName().equals(winner)) {
                        if (i <= 1) {
                            teamMate = game.getPlayers()[i + 2].getName();
                        } else {
                            teamMate = game.getPlayers()[i - 2].getName();
                        }
                    }
                }
                getClientHandler(teamMate).addPoints(3);
            }
        } else {
            for (Player p : game.getPlayers()) {
                getClientHandler(p.getName()).addPoints(1);
            }
        }
        games.remove(game);
        return ProtocolMessages.FINISH + ProtocolMessages.DELIMITER + winner + ProtocolMessages.DELIMITER;
    }

    @Override
    public String playerDefeat(String name) {
        return ProtocolMessages.DEFEAT + ProtocolMessages.DELIMITER + name + ProtocolMessages.DELIMITER;
    }

    @Override
    public String playerForfeit(String name) {
        if (getGame(name) != null) {
            getGame(name).playerForfeit(name);
            return ProtocolMessages.FORFEIT + delimSuccess;
        }
        return ProtocolMessages.FORFEIT + ProtocolMessages.DELIMITER 
                + ProtocolMessages.FORBIDDEN + ProtocolMessages.DELIMITER;
    }

    @Override
    public String getServerList() {
        String result = ProtocolMessages.LISTP + delimSuccess;
        for (ClientHandler ch : clients) {
            if (ch != null) {
                result += ch.getName() + ProtocolMessages.DELIMITER;
            }
        }
        return result;
    }

    @Override
    public String challengePlayer(String challenger, String target) {
        if (challenger.equals(target) || getClientHandler(target) == null || getLobby(challenger) != null 
                || getLobby(target) != null || getGame(challenger) != null || getGame(target) != null) {
            return ProtocolMessages.CHALL + ProtocolMessages.DELIMITER 
                    + ProtocolMessages.FORBIDDEN + ProtocolMessages.DELIMITER;
        }
        getClientHandler(target).challengedBy = challenger;
        return ProtocolMessages.CHALL + delimSuccess;
    }

    public String sendChallenge(String challenger) {
        return ProtocolMessages.CHALL + ProtocolMessages.DELIMITER 
                + challenger + ProtocolMessages.DELIMITER;
    }
    
    @Override
    public String challengeAccept(String accepter, String challenger) {
        if (getClientHandler(challenger) == null) {
            getClientHandler(accepter).challengedBy = "";
        }
        if (!getClientHandler(accepter).challengedBy.equals(challenger)
                || getLobby(challenger) != null || getLobby(accepter) != null
                || getGame(challenger) != null || getGame(accepter) != null) {   
            return ProtocolMessages.CHALLACC + ProtocolMessages.DELIMITER
                    + ProtocolMessages.FORBIDDEN + ProtocolMessages.DELIMITER;
        }
        Lobby lobby = new Lobby("challenge-" + challenger.toUpperCase() + "v" + accepter.toUpperCase(),2);
        lobby.join(challenger);
        lobby.join(accepter);
        lobbies.add(lobby);
        getClientHandler(challenger).joinLobby();
        getClientHandler(accepter).joinLobby();
        return ProtocolMessages.CHALLACC + delimSuccess;
    }

    public String sendChallengeAccept(String accepter) {
        return ProtocolMessages.CHALLACC + ProtocolMessages.DELIMITER
                + accepter + ProtocolMessages.DELIMITER;
    }
    
    @Override
    public String sendPM(String receiver, String message) {
        if (message.equals("")) {
            return ProtocolMessages.PM + ProtocolMessages.DELIMITER 
                    + ProtocolMessages.MALFORMED + ProtocolMessages.DELIMITER;
        }
        if (getClientHandler(receiver) == null) {
            return ProtocolMessages.PM + ProtocolMessages.DELIMITER 
                    + ProtocolMessages.FORBIDDEN + ProtocolMessages.DELIMITER;
        }
        return ProtocolMessages.PM + delimSuccess;
    }

    @Override
    public String receivePM(String sender, String message) {
        return ProtocolMessages.PMRECV + ProtocolMessages.DELIMITER + sender 
                + ProtocolMessages.DELIMITER + message + ProtocolMessages.DELIMITER;
    }

    @Override
    public String sendLM(String name, String message) {
        if (getLobby(name) == null) {
            return ProtocolMessages.LMSG + ProtocolMessages.DELIMITER 
                    + ProtocolMessages.FORBIDDEN + ProtocolMessages.DELIMITER; 
        }
        if (message.equals("")) {
            return ProtocolMessages.LMSG + ProtocolMessages.DELIMITER 
            + ProtocolMessages.MALFORMED + ProtocolMessages.DELIMITER;
        }
        return ProtocolMessages.LMSG + delimSuccess;
    }

    @Override
    public String receiveLM(String sender, String message) {
        return ProtocolMessages.LMSGRECV + ProtocolMessages.DELIMITER + sender
                + ProtocolMessages.DELIMITER + message + ProtocolMessages.DELIMITER;
    }

    @Override
    public String getLeaderboard() {
        String result = ProtocolMessages.LEADERBOARD + delimSuccess;
        for (ClientHandler p : clients) {
            if (p != null) {
                    result += p.getName() + ProtocolMessages.SEPERATOR 
                    + p.getPoints() + ProtocolMessages.DELIMITER;
            }
        }
        return result;
    }

    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    /**
     * Returns the game the player is currently in.
     * 
     * @param name of a player
     * @return game or null
     */
    public ServerGame getGame(String name) {
        for (Game game : games) {
            for (Player player : game.getPlayers()) {
                if (player.getName().equals(name)) {
                    return (ServerGame) game;
                }
            }
        }
        return null;
    }

    /**
     * Start a new server.
     */
    public static void main(String[] args) {
        Server server = new Server();
        System.out.println("Starting Abalone server...");
        new Thread(server).start();
    }
}
