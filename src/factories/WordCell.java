package factories;

import java.io.IOException;
import game.models.Word;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

public class WordCell extends ListCell<Word> {
	@FXML
    private HBox hBox;
	
    @FXML
    protected Label lblWord, lblStatus;
    
    public WordCell(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/WordCell.fxml"));
        fxmlLoader.setController(this);
        try{
            fxmlLoader.load();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    
    public void setInfo(String word, String status){
        lblWord.setText(word);
        lblStatus.setText(status);
    }
    
    public HBox getBox(){
        return hBox;
    }
}
