package game.controllers;

import java.io.IOException;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import game.Main;
import game.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class RegisterController {
	
	@FXML
	private Label lblError;

	@FXML
	private JFXTextField txtUName;
	
	@FXML
	private JFXPasswordField txtPass;

	@FXML
	private JFXPasswordField txtConfirmPass;
	
	public void create() {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/views/RegisterView.fxml"));
			Main.setNewScene(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	public void register(ActionEvent event) throws IOException {
		lblError.setTextFill(Color.RED);
		String message = User.register(txtUName.getText(), txtPass.getText().toString(), txtConfirmPass.getText().toString());
		lblError.setText(message);

		// Als je deze tekst aanpast moet je het ook in de User aanpassen
		if (message.equals("U bent geregistreerd. Ga terug om in te loggen.")) {
			lblError.setTextFill(Color.GREEN);
		}
		
		lblError.setVisible(true);
	}
	
	@FXML
	public void goBack() throws IOException {
		new LoginController().create();
	}
	
	@FXML
	public void exit(MouseEvent event) throws IOException {
		System.exit(0);
	}
}
