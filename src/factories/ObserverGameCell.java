package factories;

import java.io.IOException;

import game.controllers.ObserverController;
import game.models.Match;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class ObserverGameCell extends ListCell<Match> {
	
    @FXML
    private HBox hBox;

    @FXML
    private Label lblScorePlayer1, lblPlayer1, lblPlayer2, lblScorePlayer2, lblStatus;
    
    private Match match;
    private ObserverController controller;
    
    public ObserverGameCell(Match match, ObserverController controller){
        this.match = match;
        this.controller = controller;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/ObserverGameCell.fxml"));
        fxmlLoader.setController(this);
        try{
            fxmlLoader.load();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    
    @FXML
    private void clicked(MouseEvent event) throws IOException {
    	controller.setMatch(match);
    }

    public void setInfo(String player1, String player2, int scorePlayer1, int scorePlayer2, String status){
        lblPlayer1.setText(player1);
        lblPlayer2.setText(player2);
        lblScorePlayer1.setText(Integer.toString(scorePlayer1));
        lblScorePlayer2.setText(Integer.toString(scorePlayer2));
        lblStatus.setText(status);
    }

    public HBox getBox(){
        return hBox;
    }
}
