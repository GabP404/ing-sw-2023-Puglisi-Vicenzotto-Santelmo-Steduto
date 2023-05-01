package org.myshelfie.network.client;

import org.myshelfie.network.Listener;
import org.myshelfie.network.messages.commandMessages.*;
import org.myshelfie.network.server.Server;
import org.myshelfie.view.CommandLineInterface;

public class UserInputListener implements Listener<UserInputEvent> {
    private final Server server;
    private final Client client;

    public UserInputListener(Server server, Client client) {
        this.server = server;
        this.client = client;
    }

    /**
     * This method is called when user input is received from the CLI.
     * Creates the appropriate message, wraps it and sends it to the server.
     *
     * @param ev  The event that was fired. NOTE: This must be an element of an enumeration!
     * @param arg Message to be sent to the server
     */
    @Override
    public void update(UserInputEvent ev, Object arg) {
        CommandLineInterface cli = client.getCLI();
        // TODO: define how to precisely retrieve the data from the cli
        CommandMessage m = null;
        /*
        m = switch (ev) {
            case SELECTED_TILES -> new PickedTilesCommandMessage(cli.getSelectedTiles());
            case SELECTED_BOOKSHELF_COLUMN -> new SelectedColumnMessage(cli.getSelectedColumn());
            case SELECTED_HAND_TILE -> new SelectedTileFromHandCommandMessage(cli.getSelectedTileFromHand());
            default ->
                    null;
        };*/
        // send the message to the server
        String serverResponse = server.update(client, new CommandMessageWrapper(m, ev));
        // call CLI update method to show the error message
        if (!serverResponse.equals("ok")) {
            cli.update(serverResponse);
        }
    }
}
