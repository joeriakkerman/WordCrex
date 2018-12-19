package factories;

import game.controllers.ModeratorController;
import game.models.Word;
import javafx.scene.control.ListCell;

public class ModeratorCellFactory extends ListCell<Word> {

	private ModeratorController controller;
	
	public ModeratorCellFactory(ModeratorController controller) {
		this.controller = controller;
	}
	
    @Override
    public void updateItem(Word word, boolean empty)
    {
        super.updateItem(word, empty);
        if(word != null)
        {
        	ModeratorCell moderatorPWCell = new ModeratorCell(controller);
            moderatorPWCell.setInfo(word);
            setGraphic(moderatorPWCell.getBox());
        } else {
        	setGraphic(null);
        }
    }
}
