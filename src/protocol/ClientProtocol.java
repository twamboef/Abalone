package protocol;

import exceptions.ProtocolException;
import exceptions.ServerUnavailableException;

public interface ClientProtocol {

    public abstract void connect(String name) throws ProtocolException, ServerUnavailableException;

    public abstract void createLobby(String lobbyname, int size) throws ServerUnavailableException;

    public abstract void getLobbyList() throws ServerUnavailableException;

    public abstract void joinLobby(String lobby) throws ServerUnavailableException;

    public abstract void leaveLobby() throws ServerUnavailableException;

    public abstract void doReady() throws ServerUnavailableException;

    public abstract void doUnready() throws ServerUnavailableException;

    public abstract void makeMove(String move) throws ServerUnavailableException;
  
    public abstract void playerForfeit() throws ServerUnavailableException;

    public abstract void getServerList() throws ServerUnavailableException;

    public abstract void challengePlayer(String target) throws ServerUnavailableException;

    public abstract void challengeAccept(String challenger) throws ServerUnavailableException;

    public abstract void sendPM(String receiver, String message) throws ServerUnavailableException;

    public abstract void sendLM(String message) throws ServerUnavailableException;

    public abstract void getLeaderboard() throws ServerUnavailableException;
}
