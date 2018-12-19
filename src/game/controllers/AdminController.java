package game.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import game.Main;
import game.models.User;
import game.models.UserRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AdminController extends MenuController implements Initializable {
	
	@FXML
    private JFXListView<String> lvUsernames;
	
	@FXML
    private Label txtUsername, lblError;
	
	@FXML
    private TextField txtSearch;
	
	@FXML
    private VBox vBoxUserDetails;
	
	@FXML
	private HBox vBoxPlayer, vBoxAdmin, vBoxModerator, vBoxObserver;
	
	@FXML
    private JFXComboBox<String> cbRoles;
	
	private ObservableList<String> usernames = FXCollections.observableArrayList();
	private ObservableList<String> roles = FXCollections.observableArrayList();
	private User selectedUser = null;

	public void create() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AdminView.fxml"));
			Parent root = (Parent)loader.load();
			Main.setNewScene(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		super.initialize(arg0, arg1);
		updateUsernameList(User.getAllUsers());
		
		for (String role : User.getRoles()) {
			roles.add(role);
		}

		cbRoles.setItems(roles);

		vBoxUserDetails.setVisible(false);
		vBoxUserDetails.setManaged(false);
	}
	
	private void updateUsernameList(List<String> usernameList) {
		usernames.clear();
		for (String username : usernameList) {
			usernames.add(username);
		}
		lvUsernames.setItems(usernames);
	}
	
	@FXML
	public void searchKeyReleased(KeyEvent event) throws IOException {
		updateUsernameList(User.getSearchedUsers(txtSearch.getText()));
	}
	
	@FXML
	public void itemClicked(MouseEvent event) throws IOException {
		if(setUser()){
			lblError.setVisible(false);
			vBoxUserDetails.setVisible(true);
			vBoxUserDetails.setManaged(true);
		}
	}
	
	private boolean setUser() {
		if(lvUsernames.getSelectionModel().getSelectedItem() == null || lvUsernames.getSelectionModel().getSelectedItem().toString().equals("")) return false;
		UserRow userRow = User.getUserFromUsername(lvUsernames.getSelectionModel().getSelectedItem().toString());
		selectedUser = new User(userRow.username);
		selectedUser.setRoles(userRow.player, userRow.admin, userRow.moderator, userRow.observer);
		setUserRoles();
		return true;
	}
	
	private void setUserRoles() {
		txtUsername.setText(selectedUser.getUsername());
		
		if(!selectedUser.isPlayer()) {
			vBoxPlayer.setVisible(false); 
			vBoxPlayer.setManaged(false);
		} else {
			vBoxPlayer.setVisible(true); 
			vBoxPlayer.setManaged(true);
		}
		
		if(!selectedUser.isAdmin()) {
			vBoxAdmin.setVisible(false); 
			vBoxAdmin.setManaged(false);
		} else {
			vBoxAdmin.setVisible(true); 
			vBoxAdmin.setManaged(true);
		}
		
		if(!selectedUser.isModerator()) {
			vBoxModerator.setVisible(false); 
			vBoxModerator.setManaged(false);
		} else {
			vBoxModerator.setVisible(true); 
			vBoxModerator.setManaged(true);
		}
		
		if(!selectedUser.isObserver()) {
			vBoxObserver.setVisible(false); 
			vBoxObserver.setManaged(false);
		} else {
			vBoxObserver.setVisible(true); 
			vBoxObserver.setManaged(true);
		}
	}
	
	@FXML
	public void addRole(ActionEvent event) throws IOException {
		lblError.setVisible(false);
		
		if (cbRoles.getValue() != null) {
			if (!selectedUser.addRole(cbRoles.getValue())) {
				lblError.setText("De gebruiker heeft deze rol al.");
			}
		} else {
			lblError.setVisible(true);
			lblError.setText("Kies een rol alstublieft.");
		}
		
		setUser();
	}
	
	@FXML
	public void deletePlayerRole(ActionEvent event) throws IOException {
		deleteRole("player");
		setUser();
	}
	
	@FXML
	public void deleteAdminRole(ActionEvent event) throws IOException {
		deleteRole("administrator");
		setUser();
	}
	
	@FXML
	public void deleteModeratorRole(ActionEvent event) throws IOException {
		deleteRole("moderator");
		setUser();
	}
	
	@FXML
	public void deleteObserverRole(ActionEvent event) throws IOException {
		deleteRole("observer");
		setUser();
	}
	
	private void deleteRole(String role) {
		selectedUser.setRole(role, false);
		String answer = selectedUser.removeRole(role);
		
		if (!answer.equals("")) {
			lblError.setVisible(true);
			lblError.setText(answer);
		}
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
