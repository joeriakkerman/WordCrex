package game.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;
import game.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;

public class MenuController implements Initializable {
	
	@FXML
	private JFXButton btnGame, btnAdmin, btnModerator, btnObserver;
	
	@FXML
	public void btnGameClicked(MouseEvent event) throws IOException {
		new GamesController().create();
	}
	
	@FXML
	public void btnAdminClicked(MouseEvent event) throws IOException {
		new AdminController().create();
	}
	
	@FXML
	public void btnModeratorClicked(MouseEvent event) throws IOException {
		new ModeratorController().create();
	}
	
	@FXML
	public void btnObserverClicked(MouseEvent event) throws IOException {
		new ObserverController().create();
	}
	
	@FXML
	public void btnSettingsClicked(MouseEvent event) throws IOException {
		new SettingsController().create();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		hideMenuItems();
	}
	
	public void hideMenuItems() {
		if(!Main.user.isPlayer()) {
			btnGame.setVisible(false); 
			btnGame.setManaged(false);
		}
		if(!Main.user.isAdmin()) {
			btnAdmin.setVisible(false); 
			btnAdmin.setManaged(false);
		}
		if(!Main.user.isModerator()) {
			btnModerator.setVisible(false); 
			btnModerator.setManaged(false);
		}
		if(!Main.user.isObserver()) {
			btnObserver.setVisible(false); 
			btnObserver.setManaged(false);
		}
	}
}
