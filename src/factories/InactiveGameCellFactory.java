package factories;

import java.util.List;
import game.models.Match;
import game.models.Turn;
import javafx.scene.control.ListCell;

public class InactiveGameCellFactory extends ListCell<Match> {
	
    @Override
    public void updateItem(Match match, boolean empty)
    {
        super.updateItem(match,empty);
        if(match != null) {
        	InactiveGameCell inactiveGameCell = new InactiveGameCell(match);
        	List<Turn> turns = match.getTurns();
        	String resignedName = "";
        	
        	if(turns.size() > 0) {
            	if (turns.get(turns.size() - 1).getTurnPlayerOne().hasResigned()) {
            		resignedName = match.getPlayerOne();
            	} else {
            		resignedName = match.getPlayerTwo();
            	}
        	}
        	
        	inactiveGameCell.setInfo(match.getPlayerOne(), match.getPlayerTwo(), match.getTotalScorePlayerOne(), match.getTotalScorePlayerTwo(), match.getGameState(), match.getAnswerPlayerTwo(), match.getWinner(), resignedName);
            setGraphic(inactiveGameCell.getBox());
        } else {
        	setGraphic(null);
        }
    }
}
