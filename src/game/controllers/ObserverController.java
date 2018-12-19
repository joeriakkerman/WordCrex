package game.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;

import factories.ObserverGameCellFactory;
import factories.ObserverMoveCellFactory;
import game.Main;
import game.models.Board;
import game.models.Match;
import game.models.Turn;
import game.models.Turn.TurnPlayer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;


public class ObserverController implements Initializable {
	@FXML
	private Canvas canvas;
	@FXML
    private Label lblPlayer1, lblScorePlayer1, lblPlayer2, lblScorePlayer2;
	@FXML
    private JFXListView<Match> lvGames;
	@FXML
    private JFXListView<Turn> lvTurns;
	@FXML
	private JFXComboBox<String> cbGameStatus;
	
    private ObservableList<Match> games = FXCollections.observableArrayList();
    private ObservableList<Turn> turns = FXCollections.observableArrayList();
	
	private Match match;
	private Board board;
	private GraphicsContext gc;
	private int width, height;
	private int currentTurn;
	
	public void create() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ObserverView.fxml"));
			Parent root = (Parent)loader.load();
			Main.setNewScene(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
		currentTurn = -1;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		ObservableList<String> states = FXCollections.observableArrayList();
		List<String> arr = new ArrayList<String>();
		arr.add("Alle Statussen");
		arr.addAll(Match.getGameStates());
		for(String s : arr) {
			states.add(s);
		}
		cbGameStatus.setItems(states);
		cbGameStatus.setValue("Alle Statussen");
		try {
			selectGameState(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		canvas.setHeight(Main.getHeight() * 0.8);
		canvas.setWidth(Main.getWidth() * 0.5);
		
		width = (int) canvas.getWidth();
		height = (int) canvas.getHeight();
		gc = canvas.getGraphicsContext2D();
		int h = height;
		if(h > width) h = width;
		board = new Board((int) ((width - h) / 2), 0, h);
		render(gc);
	}
	
	@FXML
	private void selectGameState(ActionEvent event) throws IOException {
		String state = cbGameStatus.getSelectionModel().getSelectedItem();
		games.clear();
		List<Match> matches = Match.getAllMatches();
		for (Match match : matches) {
			if(match.getGameState().equals(state) || state.equals("Alle Statussen")) games.add(match);
		}
		
		lvGames.setItems(games);
		lvGames.setCellFactory(lvMyTurn -> new ObserverGameCellFactory(this));
		
		turns.clear();
		lvTurns.setItems(turns);
		cbGameStatus.setValue(state);
	}
	
	private void render(GraphicsContext gc){
		gc.setFill(Paint.valueOf("0x273469"));
		gc.fillRect(0, 0, width, height); 
        if(board != null) board.render(gc);
	}
	
	private void addWords() {
		for(int i = 0; i <= currentTurn; i++) addWord(i);
	}
	
	private void addWord(int id) {
		board.placeWord(turns.get(id).getBoardLetters());
	}
	
	@FXML
	private void leftClick(MouseEvent event) throws IOException {
		if(match != null && currentTurn - 1 >= -1) {
			lvTurns.getSelectionModel().clearSelection();
			currentTurn--;
			if(currentTurn >= 0) lvTurns.getSelectionModel().select(currentTurn);
			board.initBoard();
			addWords();
			render(gc);
		}
	}
	
	@FXML
	private void rightClick(MouseEvent event) throws IOException {
		if(match != null && currentTurn + 1 < turns.size()) {
			lvTurns.getSelectionModel().clearSelection();
			currentTurn++;
			lvTurns.getSelectionModel().select(currentTurn);
			addWord(currentTurn);
			render(gc);
		}
	}
	
	private void setLabels() {
		if(match == null) return;
		lblPlayer1.setText(match.getPlayerOne());
	    lblPlayer2.setText(match.getPlayerTwo());
	    
	    int score1 = 0, score2 = 0;
	    for(int i = 0; i <= currentTurn; i++) {
	    	TurnPlayer tp1 = turns.get(i).getTurnPlayerOne();
	    	TurnPlayer tp2 = turns.get(i).getTurnPlayerTwo();
	    	score1 += tp1.getScore() + tp1.getBonus();
	    	score2 += tp2.getScore() + tp2.getBonus();
	    }
	    
	    lblScorePlayer1.setText("" + score1);
	    lblScorePlayer2.setText("" + score2);
	}
	
	public void setMatch(Match match) {
		this.match = match;
		
		turns.clear();
		List<Turn> tt = match.getTurns();
		for (Turn turn : tt) {
			turns.add(turn);
		}
		lvTurns.setItems(turns);
		lvTurns.setCellFactory(lvMyTurn -> new ObserverMoveCellFactory(this, match));
		
		currentTurn = -1;
		board.initBoard();
		render(gc);
		
		setLabels();
	}
	
	public Match getMatch() {
		return match;
	}
	
	public void setTurn(Turn turn) {
		currentTurn = turn.getTurnId() - 1;
		board.initBoard();
		addWords();
		render(gc);
		setLabels();
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
