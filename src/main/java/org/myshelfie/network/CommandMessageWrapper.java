package org.myshelfie.network;

/**
 *  this class wraps the message sent by the client or the server. By doing so when a client or server receives
 *  a message it can read the type and then decide what to do with the payload
 */

public class CommandMessageWrapper {

    private CommandMessageType type;
    private String message;

    public CommandMessageWrapper(CommandMessage m, CommandMessageType t) {
        type = t;
        message = ""; //TODO JSON ser
    }

    public CommandMessageType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
