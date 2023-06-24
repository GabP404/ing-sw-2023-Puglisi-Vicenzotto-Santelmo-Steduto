package org.myshelfie.view.GUI;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.myshelfie.model.Player;
import org.myshelfie.model.WrongArgumentException;
import org.myshelfie.model.util.Pair;
import org.myshelfie.network.messages.gameMessages.ImmutablePlayer;

import java.net.URL;
import java.util.ResourceBundle;

public class RankingControllerFX implements Initializable {

    @FXML
    private Label points_LB;

    @FXML
    private Label username_LBL;

    @FXML
    private ImageView winner_IMG;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void setData(Pair<ImmutablePlayer,Boolean> result) {
        ImmutablePlayer player = result.getLeft();
        username_LBL.setText(player.getNickname());
        if(!player.isOnline()) {
            username_LBL.setStyle("-fx-text-fill: grey");
        }
        try {
            points_LB.setText("Points: "+ String.valueOf(player.getTotalPoints()));
        } catch (WrongArgumentException e) {
            throw new RuntimeException(e);
        }
        if (result.getRight()) {
            winner_IMG.setVisible(true);
        }
    }
}