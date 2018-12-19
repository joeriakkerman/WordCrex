package factories;

import java.io.IOException;
import game.models.Message;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

public class ChatCell extends ListCell<Message> {
	
    @FXML
    private HBox hBox;

    @FXML
    private Label lblName, lblMessage, lblDate;
    
    public ChatCell(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/ChatCell.fxml"));
        fxmlLoader.setController(this);
        try{
            fxmlLoader.load();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public void setInfo(Message message){
    	lblName.setText(message.getUsername());
        lblMessage.setText(message.getMessage());
        lblDate.setText(message.getMoment());
    }

    public HBox getBox(){
        return hBox;
    }
}
