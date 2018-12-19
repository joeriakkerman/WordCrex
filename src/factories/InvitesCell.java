package factories;

import java.io.IOException;

import game.controllers.GamesController;
import game.models.Match;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

public class InvitesCell extends ListCell<Match> {
	
    @FXML
    private HBox hBox;

    @FXML
    private Label lblName;
    
    private Match match;
    private GamesController controller;
    
    public InvitesCell(GamesController controller, Match match){
    	this.controller = controller;
    	this.match = match;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/InvitesCell.fxml"));
        fxmlLoader.setController(this);
        try{
            fxmlLoader.load();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    
    public void setInfo(String username){
        lblName.setText(username);
    }

    public HBox getBox(){
        return hBox;
    }
    
    @FXML
	public void accept(ActionEvent event) throws IOException {
    	if(match != null) {
    		match.updateAnswerPlayerTwo(match.getGameId(), "accepted");
        	controller.updateLists();
    	}
	}
    
    @FXML
	public void decline(ActionEvent event) throws IOException {
    	if(match != null) {
    		match.updateAnswerPlayerTwo(match.getGameId(), "rejected");
        	controller.updateLists();
    	}
	}
}
