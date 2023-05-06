package org.myshelfie.view;

import org.myshelfie.model.*;
import org.myshelfie.network.EventManager;
import org.myshelfie.network.client.ClientImpl;
import org.myshelfie.network.messages.commandMessages.UserInputEventType;
import org.myshelfie.network.messages.gameMessages.GameEventType;
import org.myshelfie.network.messages.gameMessages.GameView;

import java.util.*;

import static org.myshelfie.view.Color.*;
import static org.myshelfie.view.Color.BLUE;

public class CommandLineInterface implements Runnable {
    private static final int boardOffsetX = 10;
    private static final int boardOffsetY = 15;
    private static final int bookshelfOffsetX = 40;
    private static final int bookshelfOffsetY = 15;
    private static final int commonGoalOffsetX = 5;
    private static final int commonGoalOffsetY = 2;
    private static final int personalGoalOffsetX = 110;
    private static final int personalGoalOffsetY = 15;
    private static final int bookshelvesDistance = 15;
    public static final int inputOffsetX = 3;
    public static final int inputOffsetY = 30;

    private static final int errorOffsetX = 3;
    private static final int errorOffsetY = 33;

    private List<LocatedTile> selectedTiles;    // tiles selected from the board
    private int selectedColumn;
    private int selectedHandIndex;
    private final String nickname;


    private GameView game;

    public CommandLineInterface(String nick) {
        selectedColumn = -1;
        selectedHandIndex = -1;
        nickname = nick;
        selectedTiles = new ArrayList<>();

    }

    public void update(GameView msg, GameEventType ev) {

        clear();
        switch (ev)
        {
            //work in progress
            case BOARD_UPDATE -> selectedTiles.clear();
        }
        game = msg;
        printAll();
        setCursor(inputOffsetX, inputOffsetY);
        //System.out.println("Received from server the event " + ev + "signaling a change in the model!");
        //System.out.println("    Message payload: " + msg);
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        clearRow(inputOffsetX, inputOffsetY);
        setCursor(inputOffsetX, inputOffsetY);
        String userCommand = scanner.nextLine();
        parseInput(userCommand);
    }


    public static synchronized void print(Object o) {
        System.out.print(o);
        System.out.flush();
    }

    public synchronized static void print(String string, int x, int y, boolean toClean) {
        if (toClean) {
            clearRow(x, y);
        }

        print(RESET);

        setCursor(x, y);

        print(string + RESET);

        resetCursor();
    }

    public static void clearRow(int x, int y) {
        setCursor(x, y);

        print("\033[K");
    }

    public static void setCursor(int x, int y) {
        print(String.format("\033[%d;%dH", y, x));
    }

    public static void resetCursor() {
        setCursor(0, 0);
    }

    /**
     * Used to clear console before using it
     */
    public static void firstClear() {
        print("\033[H\033[3J\033[2J");
    }

    /**
     * Used to clear console while game is running
     */
    public static void clear() {
        for (int i = 0; i < 150; i++) {
            clearRow(0, i);
        }
    }

    public static void printWin() {
        String c = BG_BRIGHT_BLUE.toString() + BLUE.toString();

        print(c + "                                                           ", 6, 9, false);
        print(c + "  ██╗   ██╗██╗ ██████╗████████╗ ██████╗ ██████╗ ██╗   ██╗  ", 6, 10, false);
        print(c + "  ██║   ██║██║██╔════╝╚══██╔══╝██╔═══██╗██╔══██╗╚██╗ ██╔╝  ", 6, 11, false);
        print(c + "  ██║   ██║██║██║        ██║   ██║   ██║██████╔╝ ╚████╔╝   ", 6, 12, false);
        print(c + "  ╚██╗ ██╔╝██║██║        ██║   ██║   ██║██╔══██╗  ╚██╔╝    ", 6, 13, false);
        print(c + "   ╚████╔╝ ██║╚██████╗   ██║   ╚██████╔╝██║  ██║   ██║     ", 6, 14, false);
        print(c + "    ╚═══╝  ╚═╝ ╚═════╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝   ╚═╝     ", 6, 15, false);
        print(c + "                                                           ", 6, 16, false);
    }

    public void parseInput(String s) {
        String[] parts = s.split(" ");

        if (parts.length < 1) {
            printError("NOT ENOUGH ARGUMENTS");
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
                        break;
                    case "column", "c":
                        if (parts.length != 3) {
                            printError("NUMBER OF ARGUMENTS NOT CORRECT");
                            return;
                        }
                        int c = Integer.parseInt(parts[2]);
                        if (!selectColumn(c))
                            return;
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
                        break;
                    case "column", "c":
                        //not sure if player should deselect column..
                        break;
                }
                break;
            case "pick", "p":
                if (parts.length != 2) {
                    printError("NUMBER OF ARGUMENTS NOT CORRECT");
                    return;
                }
                int c = Integer.parseInt(parts[1]);
                if (!pickTileFromHand(c))
                    return;
                break;
            case "confirm", "c":
                confirmSelection();
                break;
            case "help", "h":
                //print possible commands
                break;
            default:
                printError("COMMAND DOES NOT EXIST");
                return;
        }
        printAll();
        clearRow(errorOffsetX, errorOffsetY);
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
        //TODO: think on what really is the necessary to save
        selectedTiles.add(new LocatedTile(null, r, c));
        return true;
    }

    private boolean deselectTile(int r, int c) {
        if(!tileExists(r,c))
            return false;

        for (LocatedTile t : selectedTiles) {
            if (t.getRow() == r && t.getCol() == c) {
                selectedTiles.remove(t);
                return true;
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
        ClientImpl.eventManager.notify(UserInputEventType.SELECTED_TILES);
    }

    private boolean selectColumn(int c)
    {
        if(c < 0 || c>= Bookshelf.NUMCOLUMNS)
        {
            printError("COLUMN NUMBER IS NOT VALID");
            return false;
        }
        selectedColumn = c;
        ClientImpl.eventManager.notify(UserInputEventType.SELECTED_BOOKSHELF_COLUMN);
        return true;
    }

    private boolean pickTileFromHand(int index)
    {
        if(index<0 || index >= game.getPlayers().get(myPlayerIndex()).getTilesPicked().size())
        {
            printError("INDEX OF CARD IN HAND IS NOT VALID");
            return false;
        }
        selectedHandIndex = index;
        ClientImpl.eventManager.notify(UserInputEventType.SELECTED_HAND_TILE);
        return true;

    }

    public void printAll()
    {
        for(int i = 0; i < errorOffsetY-2; i++)
            clearRow(0,i);
        printCommonGoals();
        printBoard();
        printAllBookshelves();
        printPersonalGoal();
    }

    public void printBoard()
    {
        print("BOARD: ",boardOffsetX, boardOffsetY-2,false);
        for(int i = 0; i<Board.DIMBOARD; i++)
        {
            for(int j = 0; j<Board.DIMBOARD; j++)
            {
                if(j == 0)
                    print(String.valueOf(i), boardOffsetX+j-1,boardOffsetY+i,false);

                if(game.getBoard().getTile(i, j) != null)
                {
                    boolean selected = false;
                    for(LocatedTile t : selectedTiles)
                    {
                        if(t.getRow() == i && t.getCol() == j)
                        {
                            selected = true;
                        }
                    }

                    String c;

                    //c = getColorFromTile(game.getBoard().getTile(i, j)) + BLACK.toString();
                    c = getColorFromTile(game.getBoard().getTile(i, j));

                    if(selected)
                        c = c  + "█";
                    else
                        c = c + "■";

                    //String c = BG_GREEN.toString() + BLACK.toString();
                    print(c, boardOffsetX+j,boardOffsetY+i,false);
                }
                else
                {
                    String c = BG_GRAY1.toString();
                    print(" ", boardOffsetX+j, boardOffsetY+i, false);
                }


            }
        }
        print("012345678", boardOffsetX, bookshelfOffsetY+9, false);
    }

    private String getBGColorFromTile(Tile t)
    {
        switch (t.getItemType())
        {
            case CAT: return BG_GREEN.toString();
            case BOOK: return BG_GRAY1.toString();
            case PLANT: return BG_MAGENTA.toString();
            case GAME: return BG_YELLOW.toString();
            case FRAME: return BG_BLUE.toString();
            case TROPHY: return BG_LIGHT_BLUE.toString();
        }
        return "";
    }

    private String getColorFromTile(Tile t)
    {
        switch (t.getItemType())
        {
            case CAT: return GREEN.toString();
            case BOOK: return LIGHT_GRAY.toString();
            case PLANT: return MAGENTA.toString();
            case GAME: return YELLOW.toString();
            case FRAME: return BLUE.toString();
            case TROPHY: return CYAN.toString();
        }
        return "";
    }

    private void printError(String s)
    {
        clearRow(errorOffsetX, errorOffsetY);
        String c = RED.toString();
        print(c + s, errorOffsetX, errorOffsetY, false);
    }

    private void printBookshelf(int numPlayer)
    {
        print(game.getPlayers().get(numPlayer).getNickname(), bookshelfOffsetX + (numPlayer*bookshelvesDistance), bookshelfOffsetY-2, false);
        for(int i = 0; i<Bookshelf.NUMROWS; i++)
        {
            for(int j = 0; j<Bookshelf.NUMCOLUMNS; j++)
            {
                if(j == 0)
                    print(String.valueOf(i), bookshelfOffsetX + (numPlayer*bookshelvesDistance)-1,bookshelfOffsetY+i,false);
                if(game.getPlayers().get(numPlayer).getImmutableBookshelf().getTile(i, j) != null)
                {
                    String c = getColorFromTile(game.getPlayers().get(numPlayer).getImmutableBookshelf().getTile(i, j)) + "■";
                    print(c, bookshelfOffsetX+j + (numPlayer*bookshelvesDistance),bookshelfOffsetY+i,false);
                }
                else
                {
                    print(" ", bookshelfOffsetX+j + (numPlayer*bookshelvesDistance), bookshelfOffsetY+i, false);
                }
            }
        }
        setCursor(bookshelfOffsetX + (numPlayer*bookshelvesDistance), bookshelfOffsetY + 6);
        for(int i = 0; i<Bookshelf.NUMCOLUMNS; i++)
        {
            if(numPlayer == myPlayerIndex() && selectedColumn == i)
            {
                print(BG_YELLOW.toString() + i + RESET);
            }
            else
                print(i);
        }
    }

    private void printAllBookshelves()
    {
        for(int i = 0; i < game.getPlayers().size(); i++)
        {
            printBookshelf(i);
            printHandBox(i);
            printHand(i);

        }
    }

    private void printCommonGoals()
    {
        String c = YELLOW.toString();
        print(c + "COMMON GOALS:", commonGoalOffsetX, commonGoalOffsetY, false);
        printCommonGoalBoxes();
        int offset = 0;
        for(CommonGoalCard card : game.getCommonGoals())
        {
            printCommonGoalDesc(Integer.parseInt(card.getId()), offset);
            offset++;
        }

    }

    private void printCommonGoalDesc(int id, int offset)
    {
        int cordX = commonGoalOffsetX+2 + (offset*60);
        int cordY = commonGoalOffsetY + 2;

        switch (id)
        {
            case 1:
                print("Sei gruppi separati formati ciascuno", cordX, cordY, false);
                print("da due tessere adiacenti dello stesso tipo", cordX, cordY + 1, false);
                print("Le tessere di un gruppo possono", cordX, cordY + 2, false);
                print("essere diverse da quelle di un altro gruppo.", cordX, cordY + 3, false);
                break;
            case 2:
                print("Quattro gruppi separati formati ciascuno", cordX, cordY, false);
                print("da quattro tessere adiacenti dello stesso", cordX, cordY + 1, false);
                print("tipo. Le tessere di un gruppo possono", cordX, cordY + 2, false);
                print("essere diverse da quelle di un altro gruppo.", cordX, cordY + 3, false);
                break;
            case 3:
                print("Quattro tessere dello stesso tipo", cordX, cordY, false);
                print("ai quattro angoli della Libreria. ", cordX, cordY + 1, false);
                break;
            case 4:
                print("Due gruppi separati di 4 tessere dello", cordX, cordY, false);
                print("stesso tipo che formano un quadrato 2x2.", cordX, cordY + 1, false);
                print("Le tessere dei due gruppi devono essere", cordX, cordY + 2, false);
                print("dello stesso tipo.", cordX, cordY + 3, false);
                break;
            case 5:
                print("Tre colonne formate ciascuna da", cordX, cordY, false);
                print("6 tessere di uno, due o tre tipi differenti.", cordX, cordY + 1, false);
                print("Colonne diverse possono avere", cordX, cordY + 2, false);
                print("combinazioni diverse di tipi di tessere.", cordX, cordY + 3, false);
                break;
            case 6:
                print("Otto tessere dello stesso tipo. Non ci", cordX, cordY, false);
                print("sono restrizioni sulla posizione di", cordX, cordY + 1, false);
                print("queste tessere.", cordX, cordY + 2, false);
                break;
            case 7:
                print("Cinque tessere dello stesso tipo che", cordX, cordY, false);
                print("formano una diagonale. ", cordX, cordY + 1, false);
                break;
            case 8:
                print("Quattro righe formate ciascuna", cordX, cordY, false);
                print("da 5 tessere di uno, due o tre tipi", cordX, cordY + 1, false);
                print("differenti. Righe diverse possono avere", cordX, cordY + 2, false);
                print("combinazioni diverse di tipi di tessere.", cordX, cordY + 3, false);
                break;
            case 9:
                print("Due colonne formate ciascuna", cordX, cordY, false);
                print("da 6 diversi tipi di tessere.", cordX, cordY + 1, false);
                break;
            case 10:
                print("Due righe formate ciascuna", cordX, cordY, false);
                print("da 5 diversi tipi di tessere.", cordX, cordY + 1, false);
                break;
            case 11:
                print("Cinque tessere dello stesso tipo", cordX, cordY, false);
                print("che formano una X.", cordX, cordY + 1, false);
                break;
            case 12:
                print("Cinque colonne di altezza crescente o", cordX, cordY, false);
                print("decrescente.", cordX, cordY + 1, false);
                print("Le tessere possono essere di qualsiasi tipo.", cordX, cordY + 2, false);
                break;
            default:
                print("NOT YET IMPLEMENTED", commonGoalOffsetX+2 + (offset*60), commonGoalOffsetY + 2, false);
        }
    }

    private void  printCommonGoalBoxes()
    {
        String c = YELLOW.toString();

        for(int i = 0; i<game.getCommonGoals().size(); i++)
        {
            print(c + "╔══════════════════════════════════════════════╗", commonGoalOffsetX + (i*60), commonGoalOffsetY + 1, false);
            print(c + "║                                              ║", commonGoalOffsetX + (i*60), commonGoalOffsetY + 2, false);
            print(c + "║                                              ║", commonGoalOffsetX + (i*60), commonGoalOffsetY + 3, false);
            print(c + "║                                              ║", commonGoalOffsetX + (i*60), commonGoalOffsetY + 4, false);
            print(c + "║                                              ║", commonGoalOffsetX + (i*60), commonGoalOffsetY + 5, false);
            print(c + "╚══════════════════════════════════════════════╝", commonGoalOffsetX + (i*60), commonGoalOffsetY + 6, false);
        }
    }

    private void printHand(int numPlayer)
    {
        //erasing old hand on screen
        setCursor(bookshelfOffsetX + (numPlayer*bookshelvesDistance), bookshelfOffsetY + 9);
        print("      ");
        setCursor(bookshelfOffsetX + (numPlayer*bookshelvesDistance), bookshelfOffsetY + 9);
        for(Tile t : game.getPlayers().get(numPlayer).getTilesPicked())
        {
            print(String.valueOf(t.getItemType().name().charAt(0)+ " "));
        }
        //print("T T T", bookshelfOffsetX + (numPlayer*bookshelvesDistance), bookshelfOffsetY + 9, false);
    }
    private void printHandBox(int numPlayer)
    {
        print("╔═══════╗", bookshelfOffsetX + (numPlayer*bookshelvesDistance) - 2, bookshelfOffsetY + 8, false);
        print("║       ║", bookshelfOffsetX + (numPlayer*bookshelvesDistance) - 2, bookshelfOffsetY + 9, false);
        print("╚═══════╝", bookshelfOffsetX + (numPlayer*bookshelvesDistance) - 2, bookshelfOffsetY + 10, false);
    }

    private void printPersonalGoal()
    {
        print(YELLOW.toString() + "PERSONAL GOAL", personalGoalOffsetX-3, personalGoalOffsetY-2, false);

        for(int i = 0; i<Bookshelf.NUMROWS; i++)
        {
            for(int j = 0; j<Bookshelf.NUMCOLUMNS; j++)
            {
                if(j == 0)
                    print(String.valueOf(i),personalGoalOffsetX-1,personalGoalOffsetY+i, false);
                String c = BG_BRIGHT_BLUE.toString() + BLUE.toString();
                print(c + " ", personalGoalOffsetX+j, personalGoalOffsetY+i, false);
            }
        }
        print("01234", personalGoalOffsetX, personalGoalOffsetY+6, false);



        /*for (Pair<Pair<Integer, Integer>, Tile> c: game.getPlayers().get(myPlayerIndex()).getPersonalGoal().getConstraints()) {
            int col = c.getLeft().getLeft();
            int row = c.getLeft().getRight();
            print(BG_YELLOW + String.valueOf(c.getRight().getItemType().name().charAt(0)), personalGoalOffsetX+col,bookshelfOffsetY+row,false);

        }*/
    }

    private int myPlayerIndex()
    {
        int count = 0;
        for(ImmutablePlayer p : game.getPlayers())
        {
            if(p.getNickname().equals(nickname))
                return count;
            count++;
        }
        return -1;
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

}
