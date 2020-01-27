package server;

import exceptions.ExitProgram;
import exceptions.OffBoardException;
import game.ComputerPlayer;
import game.Game;
import game.HumanPlayer;
import game.Marble;
import game.Player;
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
    private ServerTUI view;
    private List<Lobby> lobbies;
    private List<Game> games;

    /**
     * String consisting of ";200;".
     */
    private String delimSuccess = ProtocolMessages.DELIMITER + ProtocolMessages.SUCCESS + ProtocolMessages.DELIMITER;

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
                    view.showMessage("New clientHandler [" + name + "] connected!");
                    ClientHandler handler = new ClientHandler(sock, this, name);
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
                view.showMessage("Attempting to open a socket at 127.0.0.1 on port " + port + "...");
                ssock = new ServerSocket(port, 0, InetAddress.getByName("127.0.0.1"));
                view.showMessage("Server started at port " + port);
            } catch (IOException e) {
                view.showMessage("ERROR: cannot create a socket at 127.0.0.1 and port " + port + ".");
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
            if (clientHandler.getName().equals(name)) {
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
    // TODO check parameters

    @Override
    public String getConnect(String name) {
        if (name == null) {
            return ProtocolMessages.CONNECT + ProtocolMessages.DELIMITER + ProtocolMessages.MALFORMED
                    + ProtocolMessages.DELIMITER;
        }
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.getName().equals(name)) {
                return ProtocolMessages.CONNECT + ProtocolMessages.DELIMITER + ProtocolMessages.UNAUTHORIZED
                        + ProtocolMessages.DELIMITER;
            }
        }
        clients.add(getClientHandler(name));
        return ProtocolMessages.CONNECT + delimSuccess;
    }

    @Override
    public String createLobby(String lobbyname, int size) {
        if (lobbyname == null) {
            return ProtocolMessages.CONNECT + ProtocolMessages.DELIMITER + ProtocolMessages.MALFORMED
                    + ProtocolMessages.DELIMITER;
        }
        for (Lobby lobby : lobbies) {
            if (lobby.getName().equals(lobbyname) || size < 2 || size > 4) {
                return ProtocolMessages.CREATE + ProtocolMessages.DELIMITER + ProtocolMessages.FORBIDDEN
                        + ProtocolMessages.DELIMITER;
            }
        }
        lobbies.add(new Lobby(lobbyname, size));
        return ProtocolMessages.CREATE + delimSuccess;
    }

    @Override
    public String getLobbyList() {
        String result = ProtocolMessages.LISTL + delimSuccess;
        for (Lobby lobby : lobbies) {
            if (lobby.isJoinable()) {
                result += lobby.getName() + ProtocolMessages.COMMA + lobby.getSize() + ProtocolMessages.COMMA
                        + lobby.getPlayers().size() + ProtocolMessages.DELIMITER;
            }
        }
        return result;
    }

    @Override
    public String joinLobby(String name, String lobbyname) {
        for (Lobby lobby : lobbies) {
            if (lobby.getName().equals(lobbyname) && lobby.isJoinable() && !isInLobby(name)) {
                lobby.join(name);
                getClientHandler(name).joinLobby();
                return ProtocolMessages.JOIN + delimSuccess;
            }
        }
        return ProtocolMessages.JOIN + ProtocolMessages.DELIMITER + ProtocolMessages.FORBIDDEN
                + ProtocolMessages.DELIMITER;
    }

    @Override
    public String leaveLobby(String name) {
        Lobby lobby = getLobby(name);
        if (lobby != null) {
            getClientHandler(name).leaveLobby();
            lobby.leave(name);
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
            getLobby(name).ready();
            return ProtocolMessages.READY + delimSuccess;
        }
        return ProtocolMessages.READY + ProtocolMessages.DELIMITER + ProtocolMessages.FORBIDDEN
                + ProtocolMessages.DELIMITER;
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
        games.add(createGame(lobby));
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
    public Game createGame(Lobby lobby) {
        String current;
        Player p1 = new HumanPlayer((current = lobby.getPlayers().get(0)), Marble.BLACK);
        if (current.equals("-BOT")) {
            p1 = new ComputerPlayer("BOT_P1", Marble.BLACK);
        }
        Player p2 = new HumanPlayer((current = lobby.getPlayers().get(1)), Marble.WHITE);
        if (current.equals("-BOT")) {
            p2 = new ComputerPlayer("BOT_P2", Marble.WHITE);
        }
        Game game = new Game(p1, p2);
        if (lobby.getSize() == 3) {
            p2.setMarble(Marble.BLUE);
            Player p3 = new HumanPlayer((current = lobby.getPlayers().get(2)), Marble.WHITE);
            if (current.equals("-BOT")) {
                p3 = new ComputerPlayer("BOT_P3", Marble.WHITE);
            }
            game = new Game(p1, p2, p3);
        } else if (lobby.getSize() == 4) {
            p1.setMarble(Marble.RED);
            p2.setMarble(Marble.BLACK);
            Player p3 = new HumanPlayer((current = lobby.getPlayers().get(2)), Marble.BLUE);
            if (current.equals("-BOT")) {
                p3 = new ComputerPlayer("BOT_P3", Marble.BLUE);
            }
            Player p4 = new HumanPlayer((current = lobby.getPlayers().get(3)), Marble.WHITE);
            if (current.equals("-BOT")) {
                p4 = new ComputerPlayer("BOT_P4", Marble.WHITE);
            }
            game = new Game(p1, p2, p3, p4);
        }
        return game;
    }

    @Override
    public String makeMove(String name, String move) {
        try {
            getGame(name).currentPlayer().setFields(getGame(name).getBoard(), move);
        } catch (OffBoardException e) {
            return ProtocolMessages.MOVE + ProtocolMessages.DELIMITER + ProtocolMessages.FORBIDDEN
                    + ProtocolMessages.DELIMITER;
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
        lobbies.remove(getLobby(game.currentPlayer().getName()));
        String winner;
        try {
            winner = game.getWinner().getName();
        } catch (NullPointerException e) {
            winner = "";
        }
        return ProtocolMessages.FINISH + ProtocolMessages.DELIMITER + winner + ProtocolMessages.DELIMITER;
    }

    @Override
    public String playerDefeat(String name) {
        return ProtocolMessages.DEFEAT + ProtocolMessages.DELIMITER + name + ProtocolMessages.DELIMITER;
    }

    @Override
    public String playerForfeit(String name) {
        // if (getClientHandler(name).)
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

    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    /**
     * Returns the game the player is currently in.
     * 
     * @param name of a player
     * @return game or null
     */
    public Game getGame(String name) {
        for (Game game : games) {
            for (Player player : game.getPlayers()) {
                if (player.getName().equals(name)) {
                    return game;
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
