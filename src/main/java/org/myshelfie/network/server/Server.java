package org.myshelfie.network.server;

import org.myshelfie.controller.GameController;
import org.myshelfie.controller.InvalidCommand;
import org.myshelfie.controller.WrongTurnException;
import org.myshelfie.model.Game;
import org.myshelfie.model.WrongArgumentException;
import org.myshelfie.network.EventManager;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.client.ClientRMIInterface;
import org.myshelfie.network.messages.commandMessages.*;
import org.myshelfie.network.messages.gameMessages.GameEvent;
import org.myshelfie.network.messages.gameMessages.EventWrapper;

import java.io.*;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;


public class Server extends UnicastRemoteObject implements ServerRMIInterface {
    private List<Client> clients;
    private GameController controller;
    public static EventManager eventManager = new EventManager();
    private Game game;
    private String RMI_SERVER_NAME = "MinecraftServer";
    private ServerSocket serverSocket;

    /**
     * Overloaded constructor used for testing since it allows to initialize the Game object outside
     * @param game Already initialized model
     */
    public Server(Game game) throws RemoteException {
        super();
        this.game = game;
        this.clients = new ArrayList<>();
        this.controller = new GameController();
    }

    /**
     * Getter for the model that the server is using. This method is used in order to allow GameListener to send
     * the updated modelView everytime a change occurs in the model.
     * NOTE: this method will need to be parametric when we'll handle multiple games.
     * @return The model used by the server
     */
    Game getGame() {
        return game;
    }

    /**
     * Register a client to the server
     * @param client the client to register
     */
    public void register(Client client) throws IllegalArgumentException {
        //Throws an exception if there is already a client with the same nickname
        if (this.clients.stream().anyMatch(c -> c.getNickname().equals(client.getNickname()))) {
            throw new IllegalArgumentException("Nickname already taken");
        }
        this.clients.add(client);
        // Subscribe a new GameListener that will be notified when a change in the model occurs.
        // After being notified the Listener will send a message to the client containing the event and the ModelView obj
        eventManager.subscribe(GameEvent.class, new GameListener(this, client, this.getGame()));
    }

    /**
     * Register a client to the server using RMI
     * @param rmiClientInterface the client to register (as an RMI interface)
     */
    @Override
    public void register(ClientRMIInterface rmiClientInterface) {
        try {
            Client client = new Client(rmiClientInterface);
            this.register(client);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Unregister a client from the server
     * @param client the client to unregister
     */
    public void unregister(Client client) {
        this.clients.remove(client);
        // eventManager.unsubscribe(GameEvent.class, new GameListener(this, client));
        //The above is not needed I think
    }

    /**
     * Update of the server after a client send a message. This method forwards the message produced by the View (which is
     * observed by the client) to the controller, specifying the client that generated the event.
     * @param client  the client that generated the event
     * @param msg wrapped message received from the client
     */
    @Override
    public void update(Client client, CommandMessageWrapper msg) {
        if (!clients.contains(client)) {
            throw new IllegalArgumentException("Client not registered");
        }
        // TODO: understand how to use information about the client that sent the message

        // unwrap the message
        UserInputEvent messageType = msg.getType();
        CommandMessage messageCommand = msg.getMessage();
        // call the update on the controller
        this.controller.executeCommand(messageCommand, messageType);
    }

    // Method to start the server
    public void startServer(Object lock) {
        try {
            // Start the RMI server
            startRMIServer();
            // Start the socket server
            startSocketServer(lock);
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // Method to start the RMI server
    public void startRMIServer() throws RemoteException, MalformedURLException {
        try {
            // Create the RMI registry
            LocateRegistry.createRegistry(1099);
        } catch (ExportException e) {
            // If the registry already exists, it will throw an exception.
            // In this case, we get the registry and continue
            LocateRegistry.getRegistry(1099);
        }
        // Bind the server object to the registry
        Naming.rebind("//localhost/" + RMI_SERVER_NAME, this);
        System.out.println("Server started with RMI.");
    }

    /**
     * Start the socket server
     * @param lock the lock to notify when the server is started
     */
    public void startSocketServer(Object lock) {
        try {
            synchronized (lock) {
                serverSocket = new ServerSocket(1234);
                // Create a new server socket
                System.out.println("Server started with sockets.");
                lock.notifyAll();
            }

            // Loop to handle multiple connections
            while (true) {
                Socket clientSocket = null;
                try {
                    // Accept a new client connection
                    clientSocket = serverSocket.accept();
                    System.out.println("Accepted new socket connection.");
                } catch (SocketException e) {
                    System.out.println("Socket closed.");
                    return;
                }

                //Create and register a new client
                Client client = new Client();
                client.setClientSocket(clientSocket);
                // Create and start a new client handler thread
                Thread clientHandler = new SocketClientHandler(clientSocket, client);
                clientHandler.start();

            }
        } catch (IOException e) {
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Stop the socket server
     */
    public void stopSocketServer() {
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop the RMI server
     */
    public void stopRMIServer() {
        try {
            Naming.unbind("//localhost/" + RMI_SERVER_NAME);
        } catch (RemoteException | MalformedURLException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop the server (both RMI and socket)
     */
    public void stopServer() {
        stopSocketServer();
        stopRMIServer();
    }

    /**
     * Method to send a message to a client
     * @param clientSocket
     */
    public void sendTo(Socket clientSocket, Serializable message) {
        ObjectOutputStream output;
        try {
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            output.writeObject(message);
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Inner class to handle client connections
    class SocketClientHandler extends Thread {
        private Socket clientSocket;
        private Client client;

        // Socket client handler constructor
        public SocketClientHandler(Socket socket, Client client) {
            this.clientSocket = socket;
            this.client = client;
        }

        // Thread function that will handle the client requests
        public void run() {
            try {
                // Create a new input stream to read serialized objects from the client socket
                ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());

                //Get client nickname
                boolean inputValid = false;
                do {
                    String nickname = input.readObject().toString();
                    client.setNickname(nickname);
                    try {
                        Server.this.register(client);
                        inputValid = true;
                    } catch (IllegalArgumentException e) {
                        sendTo(clientSocket, "Nickname already taken!");
                    }
                } while (!inputValid);

                // Send list of games
                sendTo(clientSocket, Server.this.getGames());

                // Get CREATE or JOIN game message
                inputValid = false;
                String gameID = null;
                do {
                    CommandMessageWrapper message = (CommandMessageWrapper) input.readObject();
                    try {
                        if (message.getType() == UserInputEvent.CREATE_GAME) {
                            gameID = Server.this.createGame((CreateGameMessage) message.getMessage());
                            inputValid = true;
                        } else if (message.getType() == UserInputEvent.JOIN_GAME) {
                            gameID = Server.this.joinGame((JoinGameMessage) message.getMessage());
                            inputValid = true;
                        } else {
                            throw new IllegalArgumentException("Invalid message type");
                        }
                    } catch (IllegalArgumentException e) {
                        sendTo(clientSocket, e.getMessage());
                    }
                } while (!inputValid);

                // Send game UUID to client
                sendTo(clientSocket, gameID);

                // Loop to handle multiple client requests
                while (true) {
                    // Read a request from the client, sent as a serialized CommandMessageWrapper
                    CommandMessageWrapper request = (CommandMessageWrapper) input.readObject();
                    // If the request is null, the client has disconnected
                    if (request == null) {
                        //TODO handle disconnection
                        System.out.println("Client disconnected.");
                        break;
                    }

                    // Handle the request
                    Server.this.update(this.client, request);
                }

                // Close the client socket and unregister the client
                clientSocket.close();
                Server.this.clients.remove(client);
            } catch (IOException e) {
                System.err.println("Exception: " + e.getMessage());
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List getGames() throws RemoteException {
        return this.controller.getGames();
    }

    public String createGame(CreateGameMessage message) throws RemoteException {
        try {
            return this.controller.createGame(message);
        } catch (IllegalArgumentException e) {
            throw new RemoteException(e.getMessage());
        }
    }

    public String joinGame(JoinGameMessage message) throws RemoteException {
        try {
            return this.controller.joinGame(message);
        } catch (IllegalArgumentException e) {
            throw new RemoteException(e.getMessage());
        }
    }
}

