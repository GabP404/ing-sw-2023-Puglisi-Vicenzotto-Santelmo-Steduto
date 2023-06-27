package org.myshelfie.model;

import org.myshelfie.controller.Configuration;
import org.myshelfie.network.messages.gameMessages.GameEvent;
import org.myshelfie.network.server.Server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Player implements Serializable {
    private String nickname;
    private List<ScoringToken> commonGoalTokens;
    private Boolean hasFinalToken;
    private PersonalGoalCard personalGoal;
    private Bookshelf bookshelf;
    private List<LocatedTile> tilesPicked;
    private int selectedColumn;

    private boolean winner;
    private boolean online;

    private static int DIM_TILESPICKED = 3;

    /**
     * Constructor of the Player class.
     * @param nick      The player's nickname
     * @param persGoal  The player's personal goal card
     */
    public Player(String nick, PersonalGoalCard persGoal) {
        this.nickname = nick;
        this.commonGoalTokens = new ArrayList<>();
        this.hasFinalToken = false;
        this.personalGoal = persGoal;
        this.bookshelf = new Bookshelf();
        this.tilesPicked = new ArrayList<>();
        this.selectedColumn = -1;
        this.online = true;
        this.winner = false;
    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    public String getNickname() {
        return nickname;
    }

    public Boolean getHasFinalToken() {
        return hasFinalToken;
    }

    public void setHasFinalToken(Boolean hasFinalToken) {
        this.hasFinalToken = hasFinalToken;
        // notify the server that the final token has changed
        Server.eventManager.notify(GameEvent.FINAL_TOKEN_UPDATE, this);
    }

    public PersonalGoalCard getPersonalGoal() {
        return personalGoal;
    }


    public Bookshelf getBookshelf() {
        return bookshelf;
    }

    /**
     * Assign a token to this player.
     * @param t The token
     */
    public void addScoringToken(ScoringToken t) {
        if (t == null) return;
        commonGoalTokens.add(t);
        Server.eventManager.notify(GameEvent.TOKEN_UPDATE, this);
    }

    /**
     * @return the list of the common goal tokens.
     */
    public List<ScoringToken> getCommonGoalTokens() {
        return commonGoalTokens;
    }

    public List<LocatedTile> getTilesPicked() {
        return tilesPicked;
    }
    public void setTilesPicked(List<LocatedTile> tilesPicked) {
        this.tilesPicked = new ArrayList<>(tilesPicked);
    }

    public LocatedTile getTilePicked(int index) throws WrongArgumentException {
        if(index < 0 || index > this.tilesPicked.size()) throw new WrongArgumentException("Tile's index out of bound");
        return this.tilesPicked.get(index);
    }

    public void addTilesPicked(LocatedTile t) throws WrongArgumentException{
        if(this.tilesPicked.size() == DIM_TILESPICKED) throw new WrongArgumentException("Maximum number of tiles picked reached");
        this.tilesPicked.add(t);
    }

    /**
     * @return number of points earn from ScoringTokens
     */
    public int getPublicPoints() {
        int points_scoringToken = 0;
        for (ScoringToken s : this.commonGoalTokens) {
            points_scoringToken += s.getPoints();
        }
        int points_group = getBookshelfPoints();
        return points_scoringToken + points_group;
    }

    public int getCommonGoalPoints() {
        int points_scoringToken = 0;
        for (ScoringToken s : this.commonGoalTokens) {
            points_scoringToken += s.getPoints();
        }
        return points_scoringToken;
    }

    public int getBookshelfPoints() {
        HashMap<Integer,Integer> mapping = Configuration.getMapPointsGroup();
        int points_group = 0;
        List<Integer> groups = this.bookshelf.getAdjacentSizes();
        int maxKey = Integer.MIN_VALUE;

        for (int key : mapping.keySet()) {
            if (key > maxKey) {
                maxKey = key;
            }
        }
        for (Integer g :
                groups) {
            if(g > maxKey) points_group += mapping.get(maxKey);
            else points_group += mapping.get(g);
        }
        return points_group;
    }

    public void removeTilesPicked(LocatedTile t) throws WrongArgumentException{
        if (!this.tilesPicked.contains(t)) throw new WrongArgumentException("Tile not found");
        this.tilesPicked.remove(t);
        Server.eventManager.notify(GameEvent.TILES_PICKED_UPDATE, this);
    }

    public int getSelectedColumn() {
        return selectedColumn;
    }

    public void setSelectedColumn(int selectedColumn) throws WrongArgumentException {
        if(selectedColumn < 0 || selectedColumn >= Bookshelf.NUMCOLUMNS) {
            throw new WrongArgumentException("Column Out of range");
        }
        this.selectedColumn = selectedColumn;
        Server.eventManager.notify(GameEvent.SELECTED_COLUMN_UPDATE, this);
    }

    public void clearHand() {
        this.tilesPicked.clear();
    }

    public void clearSelectedColumn() {
        this.selectedColumn = -1;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
        Server.eventManager.notify(GameEvent.PLAYER_ONLINE_UPDATE, this);
    }

    public int getTotalPoints(){
        return getPublicPoints() + this.personalGoal.getPoints(this.bookshelf) +  (this.hasFinalToken ? 1 : 0);
    }
}
