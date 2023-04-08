package org.myshelfie.network.local;

import org.myshelfie.network.messages.gameMessages.GameEventType;
import org.myshelfie.network.messages.gameMessages.GameView;
import org.myshelfie.network.Client;
import org.myshelfie.network.Server;
import org.myshelfie.network.EventManager;
import org.myshelfie.network.listener.GameListener;
import org.myshelfie.view.CommandLineInterface;

public class ClientImpl implements Client, Runnable {
    // TODO: intialize the view (it needs to know the nickname)
    private CommandLineInterface view;
    private EventManager eventManager = new EventManager();

    /**
     * Create a new client and register it to the server, establish a listening relationship between the view and the server
     * @param server the server the client registers to
     */
    public ClientImpl(Server server) {
        server.register(this);
        // Suscribe to a GameListener in order to receive messages generated by a change on the model
        // and forward them to the client (view)
        eventManager.subscribe(GameEventType.class, new GameListener(server, this));
    }

    /**
     * Called by the server to propagate the model change to the view
     * @param game    The resulting model view
     * @param event   The causing event
     */
    @Override
    public void update(GameView game, GameEventType event) {
        view.update(game, event);
    }

    @Override
    public void run() {
        view.run();
    }
}
