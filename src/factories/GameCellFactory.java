package factories;

import game.models.Match;
import javafx.scene.control.ListCell;

public class GameCellFactory extends ListCell<Match> {
	
    @Override
    public void updateItem(Match match, boolean empty)
    {
        super.updateItem(match,empty);
        if(match != null) {
        	GameCell gameCell = new GameCell(match);
        	
            gameCell.setInfo(match.getPlayerOne(), match.getPlayerTwo(), match.getTotalScorePlayerOne(), match.getTotalScorePlayerTwo(), match.isMyTurn());
            setGraphic(gameCell.getBox());
        } else {
        	setGraphic(null);
        }
    }
}
