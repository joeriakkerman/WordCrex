package factories;

import game.controllers.ObserverController;
import game.models.Match;
import game.models.Turn;
import javafx.scene.control.ListCell;

public class ObserverMoveCellFactory extends ListCell<Turn> {
	
	private ObserverController controller;
	private Match match;
	
	public ObserverMoveCellFactory(ObserverController controller, Match match) {
		this.controller = controller;
		this.match = match;
	}
	
    @Override
    public void updateItem(Turn turn, boolean empty) {
        super.updateItem(turn,empty);
        if(turn != null) {
        	ObserverMoveCell observerMoveCell = new ObserverMoveCell(controller, turn);
        	
        	String playerOne = "", playerTwo = "";
			
        	if(turn.getTurnPlayerOne().hasResigned()) {
        		playerOne = match.getPlayerOne() + " heeft opgegeven";
        		playerTwo = "";
        	}else if(turn.getTurnPlayerTwo().hasResigned()) {
        		playerOne = "";
        		playerTwo = match.getPlayerTwo() + " heeft opgegeven";
        	}else {
        		if(turn.getTurnPlayerOne().hasPassed()) {
            		playerOne = match.getPlayerOne() + " heeft gepast";
            	}
            	if(turn.getTurnPlayerTwo().hasPassed()) {
            		playerTwo = match.getPlayerTwo() + " heeft gepast";
            	}
            	
            	if(playerOne.equals("") && turn.getTurnPlayerOne().getLetters().size() > 0 && turn.getTurnId()-1 >= 0 && turn.getTurnId() < match.getTurns().size()) {
            		playerOne = match.getPlayerOne() + " heeft " + match.getWordFromTurn(turn.getTurnId()-1, 1) + " aangelegd voor " + turn.getTurnPlayerOne().getScore();
            		if(turn.getTurnPlayerOne().getBonus() > 0) playerOne += " + " + turn.getTurnPlayerOne().getBonus();
            	}
            	if(playerTwo.equals("") && turn.getTurnPlayerTwo().getLetters().size() > 0 && turn.getTurnId()-1 >= 0 && turn.getTurnId() < match.getTurns().size()) {
            		playerTwo = match.getPlayerTwo() + " heeft " + match.getWordFromTurn(turn.getTurnId()-1, 2) + " aangelegd voor " + turn.getTurnPlayerTwo().getScore();
            		if(turn.getTurnPlayerTwo().getBonus() > 0) playerTwo += " + " + turn.getTurnPlayerTwo().getBonus();
            	}
        	}
        	
			observerMoveCell.setInfo(playerOne, playerTwo);
            setGraphic(observerMoveCell.getBox());
        } else {
        	setGraphic(null);
        }
    }
}
