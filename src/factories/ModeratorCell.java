package factories;

import java.io.IOException;

import game.controllers.ModeratorController;
import game.models.Word;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

public class ModeratorCell extends ListCell<Word> {
	
    @FXML
    private HBox hBox;

    @FXML
    private Label lblName;
    
    private ModeratorController controller;

    public ModeratorCell(ModeratorController controller) {
    	this.controller = controller;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/ModeratorCell.fxml"));
        fxmlLoader.setController(this);
        try
        {
            fxmlLoader.load();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void setInfo(Word word){
        lblName.setText(word.getWord());
    }

    public HBox getBox(){
        return hBox;
    }
    
    @FXML
	public void accept(ActionEvent event) throws IOException {
		Word.updateWord(lblName.getText(), true);
		controller.updateList();
	}
    
    @FXML
	public void decline(ActionEvent event) throws IOException {
    	Word.updateWord(lblName.getText(), false);
		controller.updateList();
	}
}
