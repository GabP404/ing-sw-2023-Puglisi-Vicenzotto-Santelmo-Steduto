package org.myshelfie.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ImmutablePlayerTest {

    @Test
    public void testConstructorAndGetterPlayer() throws IOException, URISyntaxException {
        PersonalGoalDeck pgc = PersonalGoalDeck.getInstance();
        List<PersonalGoalCard> pgcGame = pgc.draw(1);
        PersonalGoalCard pg = pgcGame.get(0);
        String nick = "User101";
        Player p = new Player(nick,pg);
        p.addScoringToken(new ScoringToken(8,"1"));
        p.addScoringToken(new ScoringToken(4,"2"));

        ImmutablePlayer x = new ImmutablePlayer(p);
        assertNotNull(x);
        assertNotNull(x.getImmutableBookshelf());
        assertFalse(x.getHasFinalToken());
        assertNotNull(x.getCommonGoalTokens());
        assertNotNull(x.getNickname());
        assertNotNull(x.getTilesPicked());
        assertNotNull(x.getPersonalGoal());
        assertTrue(x.getPointsScoringTokens() == 12);
    }


}