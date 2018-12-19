package game.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXListView;
import factories.GameCellFactory;
import factories.InactiveGameCellFactory;
import factories.InvitesCellFactory;
import game.Main;
import game.models.Match;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;

public class GamesController extends MenuController implements Initializable {
	
	@FXML
    private JFXListView<Match> lvPlayingMatches, lvInactiveMatches, lvInvites;
    
    private ObservableList<Match> itemsPlayingMatches = FXCollections.observableArrayList();
    private ObservableList<Match> itemsInactiveMatches = FXCollections.observableArrayList();
    private ObservableList<Match> itemsInvites = FXCollections.observableArrayList();
    
	public void create() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/GamesView.fxml"));
			Parent root = (Parent)loader.load();
			Main.setNewScene(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		super.initialize(arg0, arg1);
		updateLists();
	}
	
	private void setPlayingMatches() {
		for (Match match : Match.getPlayingMatches()) {
			itemsPlayingMatches.add(match);
		}
		
		lvPlayingMatches.setItems(itemsPlayingMatches);
		lvPlayingMatches.setCellFactory(lvMyTurn -> new GameCellFactory());
	}
	
	private void setInactiveMatches() {
		for (Match match : Match.getInactiveMatches()) {
			if (match.getPlayerTwo().equals(Main.user.getUsername()) && match.getAnswerPlayerTwo().equals("unknown")) {
				
			} else {
				itemsInactiveMatches.add(match);
			}
		}
		
		lvInactiveMatches.setItems(itemsInactiveMatches);
		lvInactiveMatches.setCellFactory(lvMyTurn -> new InactiveGameCellFactory());
	}
	
	private void setRequestedMatches() {
		for(Match match : Match.getInvitedMatches()) {
			itemsInvites.add(match);
		}
		
		lvInvites.setItems(itemsInvites);
		lvInvites.setCellFactory(lvInvites -> new InvitesCellFactory(this));
	}
	
	public void updateLists() {
		itemsPlayingMatches.clear();
		itemsInactiveMatches.clear();
		itemsInvites.clear();
		
		Match.isMatchAccepted();
		
		setPlayingMatches();
		setInactiveMatches();
		setRequestedMatches();
	}
	
	@FXML
	public void btnCreateMatch(MouseEvent event) throws IOException {
		new CreateMatchController().create();
	}
	
	@FXML
	public void exit(MouseEvent event) throws IOException {
		System.exit(0);
	}
	
	@FXML
	public void goBack(MouseEvent event) throws IOException {
		new LoginController().create();
	}
}