package org.myshelfie.model.commonGoal;

import org.myshelfie.model.Bookshelf;
import org.myshelfie.model.CommonGoalCard;
import org.myshelfie.model.ScoringToken;

import java.util.ArrayDeque;

public class SameTypeGroupings extends CommonGoalCard {
    /**
     * Initialize the CommonGoalCard associating the points' stack to it
     *
     * @param tokens The token stack that will be placed on the card
     *               NOTE: the stack's generation logic will be in the controller
     */
    public SameTypeGroupings(String id, ArrayDeque<ScoringToken> tokens) {
        super(id, tokens);
    }

    /**
     * Check if the specific constraints of the card are satisfied
     * @param bookshelf The library that will be checked
     */
    public Boolean checkGoalSatisfied(Bookshelf bookshelf){
        // this is an example class, TODO: implement logic for the check of every CommonGoalCard
        return null;
    }
}
