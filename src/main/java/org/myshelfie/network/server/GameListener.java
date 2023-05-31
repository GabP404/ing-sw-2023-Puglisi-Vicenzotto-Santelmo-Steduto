package org.myshelfie.network.server;

import org.myshelfie.model.Game;
import org.myshelfie.network.Listener;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.messages.gameMessages.GameEvent;
import org.myshelfie.network.messages.gameMessages.EventWrapper;
import org.myshelfie.network.messages.gameMessages.GameView;

import java.net.Socket;
import java.rmi.RemoteException;

public class GameListener implements Listener<GameEvent> {
    private final Server server;
    private final Client client;
    private Game listenedGame;

    /**
     * This listener is responsible for reacting to changes in the model, thus it's concerned
     * in sending the modelView message via the {@link #update update()} method whenever a change occurs.
     * @param server The Server object that will send the message
     * @param client The Client that is interested in listening a Game
     * @param listenedGame The game that the client wants to listen to. (In view of implementing multi-game handling)
     */
    public GameListener(Server server, Client client, Game listenedGame) {
        this.server = server;
        this.client = client;
        this.listenedGame = listenedGame;
    }

    public void setListenedGame(Game listenedGame) {
        this.listenedGame = listenedGame;
    }

    /**
     * Send to the client the (immutable) game after a change.
     *
     * @param ev  The event that has been emitted
     */
    @Override
    public void update(GameEvent ev, Object... args) {
        System.out.print("Sending event: " + ev);
        System.out.println(" to client: " + client.getNickname());

        //Send the message to the client
        if (this.listenedGame == null)
            return; //The game hasn't been set yet

        //Create the message to be sent
        GameView message = new GameView(this.listenedGame);

        if (client.isRMI()) {
            try {
                client.updateRMI(message, ev);
            } catch (RemoteException e) {
                System.out.println(e.getMessage());
            }
        } else {
            EventWrapper ew = new EventWrapper(message, ev);
            Socket clientSocket = client.getClientSocket();
            server.sendTo(clientSocket, ew);
        }
    }

    public Client getClient() {
        return client;
    }
}
