package game.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXListView;
import factories.ModeratorCellFactory;
import game.Main;
import game.models.Word;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;

public class ModeratorController extends MenuController implements Initializable {
	@FXML
    private JFXListView<Word> lvPendingWords;
    
    private ObservableList<Word> itemsPendingWords = FXCollections.observableArrayList();

	public void create() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ModeratorView.fxml"));
			Parent root = (Parent)loader.load();
			Main.setNewScene(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void updateList() {
		itemsPendingWords.clear();
		
		for (Word word : Word.getPendingWords()) {
			itemsPendingWords.add(word);
		}
    	
    	lvPendingWords.setItems(itemsPendingWords);
    	lvPendingWords.setCellFactory(lvPendingWords -> new ModeratorCellFactory(this));
	}
	
	@Override
    public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);
		updateList();
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
