package factories;

import java.io.IOException;

import game.controllers.ObserverController;
import game.models.Turn;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class ObserverMoveCell extends ListCell<Turn> {
	
    @FXML
    private HBox hBox;

    @FXML
    private Label lblPlayer1, lblPlayer2;
    
    private ObserverController controller;
    private Turn turn;
    
    public ObserverMoveCell(ObserverController controller, Turn turn){
    	this.controller = controller;
    	this.turn = turn;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/ObserverMoveCell.fxml"));
        fxmlLoader.setController(this);
        try{
            fxmlLoader.load();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    
    @FXML
    private void clicked(MouseEvent event) throws IOException {
    	if(controller != null) controller.setTurn(turn);
    }

    public void setInfo(String playerOne, String playerTwo){
        lblPlayer1.setText(playerOne);
        lblPlayer2.setText(playerTwo);
    }

    public HBox getBox(){
        return hBox;
    }
}
