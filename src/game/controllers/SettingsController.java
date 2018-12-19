package game.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import factories.WordCellFactory;
import game.Main;
import game.models.User;
import game.models.Word;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class SettingsController extends MenuController {
	
	@FXML
	private Label lblError;
	
	@FXML
	private Label lblError1, lbName, lbPlayer, lbAdmin, lbModerator, lbObserver;
	
	@FXML
	private JFXPasswordField txtNewPassword;
	
	@FXML
	private JFXPasswordField txtConfirmPassword;
	
	@FXML
    private JFXListView<Word> lvWords;
	
	@FXML
	private JFXTextField txtNewWord;
	
	private ObservableList<Word> words = FXCollections.observableArrayList();

	public void create() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/SettingsView.fxml"));
			Parent root = (Parent)loader.load();
			Main.setNewScene(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		super.initialize(arg0, arg1);
		
		setWordList();
		
		lbName.setText(Main.user.getUsername());
		lbPlayer.setText(Main.user.isPlayer() ? "Ja" : "Nee");
		lbAdmin.setText(Main.user.isAdmin() ? "Ja" : "Nee");
		lbModerator.setText(Main.user.isModerator() ? "Ja" : "Nee");
		lbObserver.setText(Main.user.isObserver() ? "Ja" : "Nee");
	}
	
	@FXML
	public void changePassword(ActionEvent event) throws IOException {
		lblError.setTextFill(Color.RED);
		String message = User.changePassword(Main.user.getUsername(), txtNewPassword.getText().toString(), txtConfirmPassword.getText().toString());
		lblError.setText(message);

		// Als je deze tekst aanpast moet je het ook in de User aanpassen
		if (message.equals("Wachtwoord is gewijzigd.")) {
			lblError.setTextFill(Color.GREEN);
		}
		
		lblError.setVisible(true);
	}
	
	@FXML
	public void addWord(ActionEvent event) throws IOException {
		lblError1.setTextFill(Color.RED);
		String message = Word.addWord(txtNewWord.getText().toString(), "NL", Main.user.getUsername());
		lblError1.setText(message);

		// Als je deze tekst aanpast moet je het ook in de Word aanpassen
		if (message.equals("Het woord is ingediend.")) {
			lblError1.setTextFill(Color.GREEN);
		}
		
		lblError1.setVisible(true);
		setWordList();
	}
	
	private void setWordList() {
		words.clear();
		
		for (Word word : Word.getMyWords()) {
			words.add(word);
		}
		
		lvWords.setItems(words);
		lvWords.setCellFactory(lvWords -> new WordCellFactory());
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
