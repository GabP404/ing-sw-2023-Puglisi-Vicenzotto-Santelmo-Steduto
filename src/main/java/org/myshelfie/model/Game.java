package org.myshelfie.model;
import org.myshelfie.util.Observable;

import java.util.*;

public class Game extends Observable<Game.Event> {
    public enum Event {
        BOOKSHELF_UPDATE, BOARD_UPDATE, TOKEN_UPDATE
    }
    private Player currPlayer;
    private List<Player> players;
    private Board board;
    private HashMap<CommonGoalCard,List<ScoringToken>> commonGoals;
    private TileBag tileBag;
    private boolean playing;

    public Game(List<Player> players, Board board, HashMap<CommonGoalCard,List<ScoringToken>> commonGoals, TileBag tileBag) {
        this.players = players;
        this.board = board;
        this.commonGoals = commonGoals;
        this.tileBag = tileBag;
        this.currPlayer = players.get(0);
        suspendGame();
    }

    public Game() {
        suspendGame();
    }

    public void startGame() {
        playing = true;
    }

    public void suspendGame() {
        playing = false;
    }

    public Player getCurrPlayer() {
        return currPlayer;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Board getBoard() {
        return board;
    }

    public List<CommonGoalCard> getCommonGoals() {
        List<CommonGoalCard> x = new ArrayList<>();
        commonGoals.forEach(
                (key,value) -> x.add(key)
        );
        return x;
    }
    public TileBag getTileBag() {
        return tileBag;
    }
    public Player getNextPlayer() {
        int pos = players.indexOf(currPlayer);
        if( pos == players.size() - 1) return players.get(0);
        return players.get(pos + 1);
    }

    public ScoringToken popTopScoringToken(CommonGoalCard c) {
        LinkedList<ScoringToken> x = (LinkedList<ScoringToken>) commonGoals.get(c);
        return x.removeFirst();
    }
    public ScoringToken getTopScoringToken(CommonGoalCard c) {
        LinkedList<ScoringToken> x = (LinkedList<ScoringToken>) commonGoals.get(c);
        return x.getFirst();
    }
    public boolean isPlaying() {
        return playing;
    }

    public void setCurrPlayer(Player currPlayer) {
        this.currPlayer = currPlayer;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setCommonGoals(List<CommonGoalCard> commonGoals) {
        this.commonGoals = commonGoals;
    }

    public void setTileBag(TileBag tileBag) {
        this.tileBag = tileBag;
    }
}