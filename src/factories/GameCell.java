package factories;

import java.io.IOException;
import game.Main;
import game.controllers.MatchController;
import game.models.Match;
import game.models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class GameCell extends ListCell<User> {
	
    @FXML
    private HBox hBox;

    @FXML
    private Label lblScorePlayer1, lblPlayer1, lblPlayer2, lblStatus, lblScorePlayer2;
    
    private Match match;
    
    public GameCell(Match match){
    	this.match = match;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/GameCell.fxml"));
        fxmlLoader.setController(this);
        try{
            fxmlLoader.load();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    
    @FXML
    public void clicked(MouseEvent event) throws IOException {
    	MatchController.myMatch = match;
    	new MatchController().create();
    	MatchController.setMatch();
    }

    public void setInfo(String player1, String player2, int scorePlayer1, int scorePlayer2, boolean status){
        lblPlayer1.setText(player1);
        lblPlayer2.setText(player2);
        lblScorePlayer1.setText(Integer.toString(scorePlayer1));
        lblScorePlayer2.setText(Integer.toString(scorePlayer2));
        if(!status) {
        	lblStatus.setText(match.getOpponentsUsername() + " is aan zet!");
        }else {
        	lblStatus.setText(Main.user.getUsername() + " is aan zet!");
        }
    }

    public HBox getBox(){
        return hBox;
    }
}
