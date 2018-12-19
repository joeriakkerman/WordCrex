package factories;

import java.io.IOException;

import game.controllers.MatchController;
import game.models.Match;
import game.models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class InactiveGameCell extends ListCell<User> {
	
    @FXML
    private HBox hBox;

    @FXML
    private Label lblScorePlayer1, lblPlayer1, lblPlayer2, lblStatus, lblScorePlayer2;
    
    private Match match;
    
    public InactiveGameCell(Match match) {
    	this.match = match;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/InactiveGameCell.fxml"));
        fxmlLoader.setController(this);
        try{
            fxmlLoader.load();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    
    @FXML
    public void clicked(MouseEvent event) throws IOException {
    	if (!match.getGameState().equals("request")) {
	    	MatchController.myMatch = match;
	    	new MatchController().create();
	    	MatchController.setMatch();
    	}
    }

    public void setInfo(String player1, String player2, int scorePlayer1, int scorePlayer2, String state, String answerPlayer2, String winner, String resignedName){
        lblPlayer1.setText(player1);
        lblPlayer2.setText(player2);
        lblScorePlayer1.setText(Integer.toString(scorePlayer1));
        lblScorePlayer2.setText(Integer.toString(scorePlayer2));
        
        if (state.equals("request")) {
        	lblStatus.setText("Uitnodiging is " + translateAnswerPlayer2(player1, answerPlayer2));
        } else {
        	lblStatus.setText(translateGameState(state, winner, resignedName));
        }
    }

    public HBox getBox(){
        return hBox;
    }
    
    private String translateGameState(String state, String winner, String resignedName) {
		String gameState = "";
		String winnerMessage = null;
		
		if (winner == null || winner.equals("")) {
			winnerMessage = "Het is gelijkspel geworden";
		} else {
			winnerMessage = winner + " heeft gewonnen";
		}
    	
    	switch (state) {
			case "finished":
				gameState = winnerMessage;
				break;
			case "resigned":
				gameState = resignedName + " heeft opgegeven";
				break;
			default:
				break;
    	}
    	
    	return gameState;
    }
    
    private String translateAnswerPlayer2(String answerPlayer1, String answerPlayer2) {
		String answer = "";
    	
		switch (answerPlayer2) {
		case "rejected":
			answer = "afgewezen";
			break;
		case "accepted":
			answer = "geaccepteerd. Wachtend op " + answerPlayer1;
			break;
		case "unknown":
			answer = "in de wacht";
			break;
		default:
			break;
	}
    	
    	return answer;
    }
}
