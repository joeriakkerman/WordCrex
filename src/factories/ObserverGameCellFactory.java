package factories;

import game.controllers.ObserverController;
import game.models.Match;
import javafx.scene.control.ListCell;

public class ObserverGameCellFactory extends ListCell<Match> {
	
	private ObserverController controller;
	
	public ObserverGameCellFactory(ObserverController controller) {
		this.controller = controller;
	}
	
    @Override
    public void updateItem(Match match, boolean empty)
    {
        super.updateItem(match, empty);
        if(match != null) {
        	ObserverGameCell observerGameCell = new ObserverGameCell(match, controller);
        	observerGameCell.setInfo(match.getPlayerOne(), match.getPlayerTwo(), match.getTotalScorePlayerOne(), match.getTotalScorePlayerTwo(), match.getGameState());
            setGraphic(observerGameCell.getBox());
        } else {
        	setGraphic(null);
        }
    }
}
