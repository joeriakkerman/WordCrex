package factories;

import game.controllers.GamesController;
import game.models.Match;
import javafx.scene.control.ListCell;

public class InvitesCellFactory extends ListCell<Match> {
	
	private GamesController controller;
	
	public InvitesCellFactory(GamesController controller) {
		this.controller = controller;
	}
	
    @Override
    public void updateItem(Match match, boolean empty){
        super.updateItem(match, empty);
        if(match != null){
            InvitesCell gameCellUitnodigingen = new InvitesCell(controller, match);
            gameCellUitnodigingen.setInfo(match.getOpponentsUsername());
            setGraphic(gameCellUitnodigingen.getBox());
        } else {
        	setGraphic(null);
        }
    }
}
