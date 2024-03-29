package org.myshelfie.network.server;

import org.myshelfie.network.client.ClientRMIInterface;
import org.myshelfie.network.messages.commandMessages.CommandMessageWrapper;
import org.myshelfie.network.messages.commandMessages.CreateGameMessage;
import org.myshelfie.network.messages.commandMessages.HeartBeatMessage;
import org.myshelfie.network.messages.commandMessages.JoinGameMessage;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The RMI interface implemented by the server that is used by the client to communicate with the server using
 * remote method invocation.
 */
public interface ServerRMIInterface extends Remote {
    /**
     * Update of the server after a client has made a choice
     * @param client  the client that generated the event
     * @param msg wrapped message received from the client
     */
    void update(ClientRMIInterface client, CommandMessageWrapper msg) throws RemoteException;

    /**
     * Update of the server inside the pre-game (connection / lobby creation) phase
     * @param client
     * @param msg
     * @return
     * @throws RemoteException
     */
    Object updatePreGame(ClientRMIInterface client, CommandMessageWrapper msg) throws RemoteException;

    /**
     * Creates a new game on the server and returns its identifier
     *
     * @param message Message containing the game settings
     * @return ID of the game
     * @throws RemoteException if it's impossible to create the game
     */
    boolean createGame(CreateGameMessage message) throws RemoteException;

    /**
     * Joins an existing game on the server
     *
     * @param message Message containing the game identifier
     * @return ID of the game
     * @throws RemoteException if the game is already full or if the game doesn't exist
     */
    boolean joinGame(JoinGameMessage message) throws RemoteException;

    /**
     * Send a heartbeat to the server to keep the connection alive
     * @throws RemoteException
     */
    void heartbeat(ClientRMIInterface clientRMIInterface, HeartBeatMessage msg) throws RemoteException;
}
