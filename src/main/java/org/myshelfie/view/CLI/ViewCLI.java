package org.myshelfie.view.CLI;

import org.myshelfie.controller.GameController;
import org.myshelfie.model.Board;
import org.myshelfie.model.Bookshelf;
import org.myshelfie.model.LocatedTile;
import org.myshelfie.model.ModelState;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.client.UserInputEvent;
import org.myshelfie.network.messages.gameMessages.GameEvent;
import org.myshelfie.network.messages.gameMessages.GameView;
import org.myshelfie.view.View;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.myshelfie.view.PrinterCLI.*;

public class ViewCLI implements View{
    public static final int inputOffsetX = 0;
    public static final int inputOffsetY = 29;

    private Client client = null;

    private List<LocatedTile> selectedTiles;    // tiles selected from the board
    private int selectedColumn;
    private int selectedHandIndex;
    private String nickname;

    private GameView game;

    private boolean reconnecting = false;
    private boolean showingHelp = false;

    private Scanner scanner = new Scanner(System.in);

    private List<GameController.GameDefinition> availableGames;

    //Thread used to ask the nickname
    Thread threadNick = new Thread(() -> {
        clear();
        try {
            while (true) {
                printTitle();
                print("Insert a Nickname ", 0, 20, false);
                setCursor(0,22);
                nickname = scanner.nextLine();
                if(validateString(nickname) && nickname.length() < 15)
                {
                    print("CONNECTING TO SERVER WITH NICKNAME "+ nickname,0,25,false);
                    this.client.eventManager.notify(UserInputEvent.NICKNAME, nickname);
                    try {
                        TimeUnit.MILLISECONDS.sleep(10000);
                    } catch ( InterruptedException e) {
                        Thread.currentThread().interrupt(); // restore interrupted status
                        break;
                    }
                    //send information to server
                    clear();
                    print("Try again ", 0, 25, false);
                }
                else{
                    clear();
                    print("the nickname cannot contain a symbols, spaces, and must be shorter than 15 characters ", 0, 25, false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    });

    //game creation/lobby selection
    Thread threadChoice = new Thread(() -> {
        clear();
        String choice;
        String gameName;
        do{
            choice = null;
            gameName = null;
            do {
                clear();
                printTitle();
                print("Do you want to create or join a game? [create/join]", 0, 20, false);
                setCursor(0, 22);
                choice = scanner.nextLine();
            }while(!choice.equalsIgnoreCase("create") && !choice.equalsIgnoreCase("join"));

            if(choice.equalsIgnoreCase("create"))
            {
                try {
                    clear();
                    do {
                        printTitle();
                        print("Insert a Game name, player number and \"simple\" at the end for simplified version   |   --back to go back", 0, 20, false);
                        setCursor(0,22);
                        gameName = scanner.nextLine();
                        if (!gameName.equalsIgnoreCase("--back")) {

                            String[] parts = gameName.split(" ");
                            boolean hasSimpleRules = false;
                            if(parts.length >= 2)
                            {
                                try{
                                    int playerNum = Integer.parseInt(parts[1]);
                                    if(playerNum >= 2 && playerNum <= 4)
                                    {
                                        if(validateString(parts[0]))
                                        {
                                            if(parts.length > 2 && parts[2].equalsIgnoreCase("simple"))
                                                hasSimpleRules = true;
                                            print("Creating game: "+ parts[0],0,25,true);
                                            this.client.eventManager.notify(UserInputEvent.CREATE_GAME, parts[0], playerNum, hasSimpleRules);
                                            try {
                                                TimeUnit.MILLISECONDS.sleep(10000);
                                            } catch ( InterruptedException e) {
                                                Thread.currentThread().interrupt(); // restore interrupted status
                                                break;
                                            }
                                            //send information to server
                                            clear();
                                            print("Try again ", 0, 25, false);
                                        }
                                        else{
                                            clear();
                                            print("game name is not valid, it cannot contain symbols ", 0, 25, false);
                                        }
                                    }
                                    else {
                                        clear();
                                        print("number of players must be between 2 and 4 ", 0, 25, false);
                                    }
                                }
                                catch(NumberFormatException nfe) {
                                    clear();
                                    print("number of players typed is not a number ", 0, 25, false);
                                }
                            }
                            else{
                                clear();
                                print("Not enough arguments ", 0, 25, false);
                            }

                        }
                    }while (!gameName.equalsIgnoreCase("--back"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(choice.equals("join"))
            {
                try {
                    clear();
                    do{
                        printTitle();
                        print("Insert a Game name  |  --back to go back  |  --refresh to refresh lobbies", 0, 20, false);
                        print("Available games: ", 90, 20, false);
                        for (int i=0; i<this.availableGames.size(); i++) {
                            if(this.availableGames.get(i).getNicknames().size() < this.availableGames.get(i).getMaxPlayers())
                                print("-> " + this.availableGames.get(i).getGameName() + " " + this.availableGames.get(i).getNicknames().size() + "/" + this.availableGames.get(i).getMaxPlayers(), 90, 22+i, false);
                        }
                        setCursor(0,22);
                        gameName = scanner.nextLine();
                        if (!gameName.equalsIgnoreCase("--back")) {

                            //String[] parts = gameName.split(" ");
                            if (gameName.equalsIgnoreCase("--refresh")) {
                                this.client.eventManager.notify(UserInputEvent.REFRESH_AVAILABLE_GAMES);
                                Thread.sleep(250);
                                clear();
                            } else {
                                if(isInLobbyList(gameName))
                                {
                                    print("joining game: "+ gameName,0,25,    true);
                                    this.client.eventManager.notify(UserInputEvent.JOIN_GAME, gameName);
                                    try {
                                        TimeUnit.MILLISECONDS.sleep(10000);
                                    } catch ( InterruptedException e) {
                                        Thread.currentThread().interrupt(); // restore interrupted status
                                        break;
                                    }
                                    clear();
                                    print("Try again ", 0, 25, false);
                                }
                                else
                                {
                                    clear();
                                    print("Game not found, please pick a game from the list",0,25,false);
                                }
                            }

                        }
                    }while (!gameName.equalsIgnoreCase("--back"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else
            {
                System.out.println("Wrong choice");
            }

        }while(Objects.requireNonNull(gameName).equalsIgnoreCase("--back"));
    });

    //checks if the game name is in the list of all lobbies
    private boolean isInLobbyList(String gameName)
    {
        for (int i=0; i<this.availableGames.size(); i++) {
            if (this.availableGames.get(i).getGameName().equalsIgnoreCase(gameName) && this.availableGames.get(i).getNicknames().size() < this.availableGames.get(i).getMaxPlayers())
                return true;
        }
        return false;
    }

    private boolean validateString(String input) {
        String regex = "^[a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    public static void main(String args[]) {
        boolean isRMI = Boolean.parseBoolean(args[0]);
        String serverAddress = args[1];
        ViewCLI view = new ViewCLI(isRMI, serverAddress);
        view.run();
    }

    public ViewCLI(boolean isRMI, String serverAddress) {
        selectedColumn = -1;
        selectedHandIndex = -1;
        selectedTiles = new ArrayList<>();
        availableGames = new ArrayList<>();
        try {
            this.client = new Client(false, isRMI, serverAddress);
            client.connect();
            client.initializeViewCLI(this);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    //handle messages from server
    @Override
    public void update(GameView msg, GameEvent ev) {
        // End the threads to create/join a game, in case the gameView was received after a reconnection
        this.endLobbyPhase();

        game = msg;

        //if the game state is END_GAME print the ranking
        if(game.getModelState().equals(ModelState.END_GAME))
        {
            clear();
            printEndGameScreen(game, nickname);
            setCursor(inputOffsetX, inputOffsetY);
        }
        else    //else print the new gameView
        {
            if(!showingHelp)//if a player is watching the help box, don't print the gameView yet
            {
                switch (ev)
                {
                    case ERROR:
                        if(game.getErrorState(nickname) != null)
                        {
                            clear();
                            printAll(game, selectedTiles, nickname);
                            printError(game.getErrorState(nickname));
                        }
                        break;
                    case BOARD_UPDATE:
                        selectedTiles.clear();
                        clear();
                        printAll(game, selectedTiles, nickname);
                        break;
                    default:
                        clear();
                        printAll(game, selectedTiles, nickname);
                        break;
                }
                setCursor(inputOffsetX, inputOffsetY);
            }
        }
    }

    @Override
    public void run() {
        //start the thread that asks the user for the nickname
        threadNick.start();
        try {
            threadNick.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        //start the thread that asks the user if he wants to create or join a game
        if (!reconnecting) {
            threadChoice.start();
            try {
                threadChoice.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        //start the thread that handles the user input for the entire game
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    clearRow(inputOffsetX, inputOffsetY);
                    setCursor(inputOffsetX, inputOffsetY);
                    String userCommand = scanner.nextLine();
                    if(this.game != null)
                        parseInput(userCommand);
                    else
                        printError("GAME HAS NOT STARTED YET, WAIT FOR OTHER PLAYERS");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
    }

    @Override
    public void endLoginPhase()
    {
        if(threadNick.isAlive())
            threadNick.interrupt();
    }

    @Override
    public void endLobbyPhase()
    {
        if(threadChoice.isAlive())
            threadChoice.interrupt();
    }


    @Override
    public String getGameName() {
        if (game == null)
            return null;
        return game.getGameName();
    }

    @Override
    public GameView getGameView() {
        return game;
    }


    public void parseInput(String s) {

        String[] parts = s.split(" ");
        //if there are no arguments return
        if (parts.length < 1) {
            printError("NOT ENOUGH ARGUMENTS");
            return;
        }

        //
        switch (parts[0]) {
            case "exit" -> {
                System.exit(0);
                return;
            }
            case "play" -> {
                //TODO: TEST THIS OPTION
                if(game.getModelState().equals(ModelState.END_GAME))
                {
                    clear();
                    threadChoice.run();
                }
                //threadChoice.run();
                return;
            }
            case "help", "h" -> {
                //print possible commands
                showingHelp = true;
                printHelp();
                scanner.nextLine();
                //clear();
                printAll(game, selectedTiles,nickname);
                showingHelp = false;
                return;
            }
            default -> {
            }
        }

        //if it's not the player turn return
        if(!game.getCurrPlayer().getNickname().equals(nickname))
        {
            printError("IT'S NOT YOUR TURN");
            return;
        }

        //if the game is paused, prevent further actions
        if (game.getModelState().equals(ModelState.PAUSE)) {
            printError("GAME IS PAUSED DUE TO OTHER PLAYERS' DISCONNECTION");
            return;
        }

        switch (parts[0]) {
            case "select", "s":
                if (parts.length < 2) {
                    printError("NUMBER OF ARGUMENTS NOT CORRECT");
                    return;
                }
                switch (parts[1]) {
                    case "tile", "t":
                        if(!game.getModelState().equals(ModelState.WAITING_SELECTION_TILE))
                        {
                            printError("YOU CAN'T SELECT A TILE NOW");
                            return;
                        }
                        if (parts.length != 4) {
                            printError("NUMBER OF ARGUMENTS NOT CORRECT");
                            return;
                        }
                        try {
                            int r = Integer.parseInt(parts[2]);
                            int c = Integer.parseInt(parts[3]);
                            if (!selectTile(r, c))
                                return;
                        } catch (NumberFormatException nfe) {
                            printError("ROW OR COLUMN NUMBERS ARE NOT CORRECT");
                            return;
                        }
                        clear();
                        printAll(game, selectedTiles, nickname);
                        break;
                    case "column", "c":
                        if(!game.getModelState().equals(ModelState.WAITING_SELECTION_BOOKSHELF_COLUMN))
                        {
                            printError("YOU CAN'T SELECT A COLUMN NOW");
                            return;
                        }
                        if (parts.length != 3) {
                            printError("NUMBER OF ARGUMENTS NOT CORRECT");
                            return;
                        }
                        try {
                            int c = Integer.parseInt(parts[2]);
                            if (!selectColumn(c))
                                return;
                        } catch (NumberFormatException nfe) {
                            printError("COLUMN NUMBER IS NOT CORRECT");
                            return;
                        }
                        break;
                    default:
                        printError("COMMAND DOES NOT EXIST");
                        break;
                }
                break;
            case "deselect", "d":
                if (parts.length < 2) {
                    printError("NUMBER OF ARGUMENTS NOT CORRECT");
                    return;
                }
                switch (parts[1]) {
                    case "tile", "t":
                        if(!game.getModelState().equals(ModelState.WAITING_SELECTION_TILE))
                        {
                            printError("YOU CAN'T DESELECT A TILE NOW");
                            return;
                        }
                        if (parts.length != 4) {
                            printError("NUMBER OF ARGUMENTS NOT CORRECT");
                            return;
                        }
                        try {
                            int r = Integer.parseInt(parts[2]);
                            int c = Integer.parseInt(parts[3]);
                            if (!deselectTile(r, c))
                                return;
                        } catch (NumberFormatException nfe) {
                            printError("ROW OR COLUMN NUMBERS ARE NOT CORRECT");
                            return;
                        }
                        clear();
                        printAll(game, selectedTiles, nickname);
                        break;
                    default:
                        printError("COMMAND DOES NOT EXIST");
                        break;
                }
                break;
            case "pick", "p":
                if(!game.getModelState().equals(ModelState.WAITING_1_SELECTION_TILE_FROM_HAND) &&
                        !game.getModelState().equals(ModelState.WAITING_2_SELECTION_TILE_FROM_HAND) &&
                        !game.getModelState().equals(ModelState.WAITING_3_SELECTION_TILE_FROM_HAND))
                {
                    printError("YOU CAN'T PICK A TILE FROM THE HAND NOW");
                    return;
                }
                if (parts.length != 2) {
                    printError("NUMBER OF ARGUMENTS NOT CORRECT");
                    return;
                }
                try {
                    int c = Integer.parseInt(parts[1]);
                    if (!pickTileFromHand(c))
                        return;
                } catch (NumberFormatException nfe) {
                    printError("HAND INDEX NUMBER IS NOT CORRECT");
                    return;
                }
                break;
            case "confirm", "c":
                if(selectedTiles.isEmpty())
                {
                    printError("NO TILES SELECTED");
                    return;
                }
                if(!game.getModelState().equals(ModelState.WAITING_SELECTION_TILE))
                {
                    printError("YOU CAN'T CONFIRM THE SELECTION NOW");
                    return;
                }
                confirmSelection();
                break;
            default:
                printError("COMMAND DOES NOT EXIST, TYPE \"help\" TO SEE ALL COMMANDS");
                return;
        }
    }

    private boolean selectTile(int r, int c) {
        if(!tileExists(r,c))
            return false;

        for (LocatedTile t : selectedTiles) {
            if (t.getRow() == r && t.getCol() == c) {
                printError("TILE ALREADY SELECTED");
                return false;
            }
        }

        if(game.getCurrPlayer().getBookshelf().getMinHeight() + selectedTiles.size() + 1 > Bookshelf.NUMROWS)
        {
            printError("SELECTION PREVENTED: U CAN'T FIT THE SELECTED TILES IN THE BOOKSHELF");
            return false;
        }
        //TODO: think on what really is the necessary to save
        LocatedTile t = new LocatedTile(null, r, c);
        selectedTiles.add(t);
        if(!isTilesGroupSelectable(selectedTiles))
        {
            selectedTiles.remove(t);
            printError("SELECTION PREVENTED: TILE IS NOT SELECTABLE, CHECK THE RULES");
            return false;
        }
        return true;
    }

    private boolean deselectTile(int r, int c) {
        if(!tileExists(r,c))
            return false;

        for (LocatedTile t : selectedTiles) {
            if (t.getRow() == r && t.getCol() == c) {
                selectedTiles.remove(t);
                if(isTilesGroupSelectable(selectedTiles))
                {
                    return true;
                }
                else
                {
                    selectedTiles.add(t);
                    printError("DESELECTION PREVENTED: YOU CANNOT DESELECT THAT TILE GIVEN THE CURRENT SELECTED TILES");
                    return false;
                }
            }
        }
        printError("TILE IS NOT SELECTED");
        return false;
    }

    private boolean tileExists(int r, int c)
    {
        if (r < 0 || r >= Board.DIMBOARD || c < 0 || c >= Board.DIMBOARD) {
            printError("ROW OR COLUMN ARE NOT VALID NUMBERS");
            return false;
        }
        if (game.getBoard().getTile(r, c) == null) {
            printError("THERE IS NO TILE IN POSITION: " + r + " " + c);
            return false;
        }
        return true;
    }

    private void confirmSelection()
    {
        this.client.eventManager.notify(UserInputEvent.SELECTED_TILES, selectedTiles);
    }

    private boolean selectColumn(int c)
    {
        if(c < 0 || c>= Bookshelf.NUMCOLUMNS)
        {
            printError("COLUMN NUMBER IS NOT VALID");
            return false;
        }
        //if the column is too full for the selected tiles return
        if(game.getCurrPlayer().getBookshelf().getHeight(c) + game.getCurrPlayer().getTilesPicked().size() > Bookshelf.NUMROWS)
        {
            printError("SELECTION PREVENTED: U CAN'T FIT THE SELECTED TILES IN THAT COLUMN");
            return false;
        }
        selectedColumn = c;
        this.client.eventManager.notify(UserInputEvent.SELECTED_BOOKSHELF_COLUMN, selectedColumn);
        return true;
    }

    private boolean pickTileFromHand(int index)
    {
        if(index<0 || index >= game.getCurrPlayer().getTilesPicked().size())
        {
            printError("INDEX OF CARD IN HAND IS NOT VALID");
            return false;
        }
        selectedHandIndex = index;
        this.client.eventManager.notify(UserInputEvent.SELECTED_HAND_TILE, selectedHandIndex);
        return true;

    }


    public List<LocatedTile> getSelectedTiles() {
        return selectedTiles;
    }

    public int getSelectedColumn() {
        return selectedColumn;
    }

    public int getSelectedHandIndex() {
        return selectedHandIndex;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void setAvailableGames(List<GameController.GameDefinition> availableGames) {
        this.availableGames = availableGames;
    }

    public void setReconnecting(boolean reconnecting) {
        this.reconnecting = reconnecting;
    }

    public boolean isTilesGroupSelectable(List<LocatedTile> chosen) {
        // Add the check that you cannot select more than 3 tiles
        if (chosen.size() > 3) {
            return false;
        }

        //Check that all the selected tiles are indeed selectable on their own (i.e. at least one free border)
        for (LocatedTile t : chosen) {
            if (!game.getBoard().hasOneOrMoreFreeBorders(t.getRow(), t.getCol()))
                return false;
        }

        // Skip the check if there is only one tile in the selection
        if (chosen.size() < 2) {
            // If so, return true since a single tile or no tiles are always in a line
            return true;
        }

        // The tiles are horizontal / vertical if all the rows / cols are the same
        boolean isHorizontal = chosen.stream().map(LocatedTile::getRow).distinct().count() == 1;
        boolean isVertical = chosen.stream().map(LocatedTile::getCol).distinct().count() == 1;

        if (!isHorizontal && !isVertical)
            return false;

        // Check that the chosen tile are "sequential" i.e., adjacent to each other
        SortedSet<Integer> sortedIndexes = new TreeSet<>();
        if (isHorizontal)
            sortedIndexes.addAll(chosen.stream().map(LocatedTile::getCol).collect(Collectors.toSet()));
        if (isVertical)
            sortedIndexes.addAll(chosen.stream().map(LocatedTile::getRow).collect(Collectors.toSet()));

        return sortedIndexes.last() - sortedIndexes.first() == sortedIndexes.size() - 1;
    }

    @Override
    public void nicknameAlreadyUsed() {
        printError("NICKNAME ALREADY USED");
    }
}
