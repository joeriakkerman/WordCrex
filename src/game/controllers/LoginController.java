package game.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import game.Main;
import game.models.Database;
import game.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class LoginController implements Initializable {
	
	@FXML
	private Label lblError, lblRegister;
	
	@FXML
	private JFXTextField txtUName;
	
	@FXML
	private JFXPasswordField txtPass;
	
	@FXML
	private JFXButton btnLogin;
	
	public void create() {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/views/LoginView.fxml"));
			Main.setNewScene(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		if (!Database.connect()) {
			lblError.setVisible(true);
			lblError.setText("Er kon geen verbinding worden gemaakt met de database.\nProbeer opnieuw op te starten");
			btnLogin.setDisable(true);
			lblRegister.setDisable(true);
		}
	}
	
	@FXML
	public void login(ActionEvent event) throws IOException {
		User.login(txtUName.getText().toString(), txtPass.getText().toString());
		
		if (Main.user.isPlayer() == false && Main.user.isAdmin() == false && Main.user.isModerator() == false && Main.user.isObserver() == false)
			lblError.setVisible(true);
		else
			new GamesController().create();
	}
	
	@FXML
	public void register(MouseEvent event) throws IOException {
		new RegisterController().create();
	}
	
	@FXML
	public void exit(MouseEvent event) throws IOException {
		System.exit(0);
	}


}
