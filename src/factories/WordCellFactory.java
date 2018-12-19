package factories;

import game.models.Word;
import javafx.scene.control.ListCell;

public class WordCellFactory extends ListCell<Word> {
	
	@Override
    public void updateItem(Word word, boolean empty)
    {
        super.updateItem(word, empty);
        
        if(word != null) {
        	WordCell wordCell = new WordCell();
            wordCell.setInfo(word.getWord(), word.getTranslatedState());
            setGraphic(wordCell.getBox());
        } else {
        	setGraphic(null);
        }
    }
}
