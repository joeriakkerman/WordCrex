package factories;

import game.models.Message;
import javafx.scene.control.ListCell;

public class ChatCellFactory extends ListCell<Message> {
	
    @Override
    public void updateItem(Message message, boolean empty)
    {
        super.updateItem(message,empty);
        if (message != null) {
        	ChatCell chatCell = new ChatCell();
            chatCell.setInfo(message);
            setGraphic(chatCell.getBox());
        } else {
        	setGraphic(null);
        }
    }
}
