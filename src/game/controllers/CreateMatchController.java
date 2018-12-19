package game.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXListView;
import game.Main;
import game.models.Match;
import game.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class CreateMatchController extends MenuController implements Initializable {
	
	@FXML
	private Label lblError, playerOne, playerTwo;
	
	@FXML
    private JFXListView<String> lvFriends;
    
	private ObservableList<String> users = FXCollections.observableArrayList();
	private final String NO_USERNAME = "..........";
	
	public void create() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CreateMatchView.fxml"));
			Parent root = (Parent)loader.load();
			Main.setNewScene(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		super.initialize(arg0, arg1);
		playerOne.setText(Main.user.getUsername());
		playerTwo.setText(NO_USERNAME);
		
		users.addAll(User.getAllUsers());
		lvFriends.setItems(users);
	}
	
	@FXML
	public void itemClicked(MouseEvent event) throws IOException {
		lblError.setVisible(false);
		playerTwo.setText(lvFriends.getSelectionModel().getSelectedItem().toString());
	}
	
	@FXML
	public void start(ActionEvent event) throws IOException {
		String opponent = playerTwo.getText().toString();
		if(!opponent.equals(NO_USERNAME)) {
			if (!Match.hasMatchWith(opponent)) {
				if (Match.createMatch(opponent)) {
					new GamesController().create();
				} else {
					lblError.setText("Er is iets misgegaan!");
					lblError.setVisible(true);
				}
			} else {
				lblError.setText("Je hebt al een spel of uitdaging open staan met " + opponent + ".");
				lblError.setVisible(true);
			}
		}else {
			lblError.setText("Je moet eerst nog een tegenstander selecteren!");
			lblError.setVisible(true);
		}
	}
	
	@FXML
	public void exit(MouseEvent event) throws IOException {
		System.exit(0);
	}
	
	@FXML
	public void goBack(MouseEvent event) throws IOException {
		new GamesController().create();
	}
}
