package game.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import factories.ChatCellFactory;
import factories.ObserverMoveCellFactory;
import game.Main;
import game.models.Board;
import game.models.Chat;
import game.models.Database;
import game.models.Hand;
import game.models.Match;
import game.models.Message;
import game.models.Turn;
import game.models.Turn.Letter;
import game.models.Turn.TurnPlayer;
import game.models.tiles.HandCharacter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;

public class MatchController implements Initializable {
	@FXML
	private Canvas canvas;
	
	@FXML
    private Label lblPlayer1, lblScorePlayer1, lblPlayer2, lblScorePlayer2, lblLetters, lblTitle;
	
	@FXML
	private JFXTextField txtChat;
	
	@FXML
    private JFXListView<Message> lvMessages;
	
	@FXML
    private JFXListView<Turn> lvHistory;
	
	@FXML
	private JFXButton btnSwitchLV;
	
	@FXML
	private HBox boxChatField;
	
	@FXML
	private JFXButton btnPlay, btnShuffle, btnPass, btnPlaceBack, btnResign;
	
	@FXML
	private FontAwesomeIconView iconWinner1, iconWinner2;

	private Mouse mouse;
	private static Board board;
	private static Hand hand;
	private static GraphicsContext gc;
	private static int width, height;
	public static Match myMatch;
	private static Chat chat;
	private Thread chatThread;
	private static final int INTERVAL = 5000;
    private ObservableList<Message> itemsMessages = FXCollections.observableArrayList();
    private ObservableList<Turn> itemsMoves = FXCollections.observableArrayList();
	
	public void create() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MatchView.fxml"));
			Parent root = (Parent)loader.load();
			Main.setNewScene(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void render(GraphicsContext gc){
		gc.setFill(Paint.valueOf("0x273469"));
		gc.fillRect(0, 0, width, height); 
        if(board != null) board.render(gc);
        if(hand != null) hand.render(gc);
	}
	
	public static void setMatch() {
		int h = (height / 18) * 16;
		if(h > width) h = width;
		board = new Board((int) ((width - h) / 2), 0, h);
		
		List<List<Letter>> boardLetters = myMatch.getBoardLetters();
		for(int i = 0; i < boardLetters.size(); i++) 
			board.placeWord(boardLetters.get(i));
		
		List<Turn> turns = myMatch.getTurns();
		boolean added = false;
		if(turns.size() > 0) {
			Turn lastTurn = turns.get(turns.size() - 1);
			List<Letter> letters;
			if(myMatch.getPlayerOne().equals(Main.user.getUsername())) {
				letters = lastTurn.getTurnPlayerOne().getLetters();
				added = lastTurn.getTurnPlayerOne().hasPassed();
			}else {
				letters = lastTurn.getTurnPlayerTwo().getLetters();
				added = lastTurn.getTurnPlayerTwo().hasPassed();
			}
			
			if(letters != null && letters.size() > 0) {
				board.placeWord(letters);
				added = true;
			}
		}
		
		if(!added) hand = new Hand(0, (int)((height / 18) * 16.5), width, (int)((height / 18) * 1.5), Match.getHandLetters(myMatch.getGameId(), Match.getLastTurn(myMatch.getGameId())));
		chat = new Chat(myMatch.getGameId());
		render(gc);
	}
	
	private void updateMyMatch() {
		int gameId = myMatch.getGameId();
		List<Match> matches = Match.getPlayingMatches();
		for(int i = 0 ; i < matches.size(); i++) {
			if(matches.get(i).getGameId() == gameId) {
				myMatch = matches.get(i);
				break;
			}
		}
		setLabels();
		setTrophys();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setLabels();
		setTrophys();
		lvHistory.setVisible(false);
		lvHistory.setManaged(false);
		lvMessages.setVisible(true);
		lvMessages.setManaged(true);
		
		canvas.setHeight(Main.getHeight() * 0.8);
		canvas.setWidth(Main.getWidth() * 0.5);
		
		width = (int) canvas.getWidth();
		height = (int) canvas.getHeight();
		mouse = new Mouse();
		canvas.setOnMousePressed(e -> {
			mouse.pressed((int)e.getX(), (int)e.getY());
		});
		canvas.setOnMouseReleased(e -> {
			mouse.released((int)e.getX(), (int)e.getY());
		});
		canvas.setOnMouseDragged(e -> {
			mouse.dragged((int)e.getX(), (int)e.getY());
		});
		gc = canvas.getGraphicsContext2D();
		render(gc);
		
		chatThread = new Thread(() -> {
	        while (true) {
	            try {
	                Platform.runLater(() -> {
	    				setChatMessages();
	                });
	                Thread.sleep(INTERVAL);
	            }
	            catch (Exception e) {}
	        }
	    });
		chatThread.start();
	    
	    if (!myMatch.isMyTurn() || myMatch.getGameState().equals("finished") || myMatch.getGameState().equals("resigned")) {
	    	mouse.setCanDraw(false);
	    	btnPlay.setDisable(true);
	    	btnShuffle.setDisable(true);
	    	btnPass.setDisable(true);
	    	btnPlaceBack.setDisable(true);
	    	if (myMatch.getGameState().equals("finished") || myMatch.getGameState().equals("resigned")) {
	    		btnResign.setDisable(true);
	    	}
	    }
	}
	
	@FXML
	public void onMove(ActionEvent event) throws IOException {
		btnPlay.setDisable(true);
		if(hand == null) return;
		List<Letter> placedChars = board.getPlacedCharacters(myMatch);
		
		if (placedChars != null) {
			if(board.middleTileContainsTile()) {
				if(myMatch.addPlayerTurn(board.getScore())) {
					if(myMatch.addBoardLetters(placedChars, myMatch.getPlayerIdInGame())) {
						addNewTurn(false);
					}else {
						showAlert("Er is iets misgegaan. Probeer opnieuw.");
					}
					render(gc);
				}else {
					showAlert("Er is iets misgegaan.");
				}
			}else {
				showAlert("Het middelste vakje moet altijd bedenkt zijn. Probeer opnieuw.");
			}
		} else {
			showAlert("Plaats een geldig woord alstublieft.");
		}

		btnPlay.setDisable(false);
	}
	
	@FXML
	public void onShuffle(ActionEvent event) throws IOException {
		if(hand == null) return;
		hand.shuffle();
		hand.render(gc);
	}
	
	private void addNewTurn(boolean pass) {
		List<Turn> turns = Database.getTurns(myMatch.getGameId());
		if(turns.size() > 0) {
			Turn lastTurn = turns.get(turns.size() - 1);
			int playerId = myMatch.getPlayerIdInGame();
			
			TurnPlayer tp = null;
			if(playerId == 1) {
				tp = lastTurn.getTurnPlayerTwo();
			}else {
				tp = lastTurn.getTurnPlayerOne();
			}

			// testen
			if(tp == null || tp.getLetters().size() > 0 || tp.hasPassed()) {
				if(pass || myMatch.updateBonus()) {
					Turn turn = myMatch.getTurns().get(myMatch.getTurns().size() - 1);
					boolean hasPlayerOnePassed = turn.getTurnPlayerOne().hasPassed();
					boolean hasPlayerTwoPassed = turn.getTurnPlayerTwo().hasPassed();
					
					if (playerId == 1) {
						hasPlayerOnePassed = true;
					} else {
						hasPlayerTwoPassed = true;
					}
					
					if ((myMatch.getAmountOfLettersLeft() < 7 && (hasPlayerOnePassed && hasPlayerTwoPassed)) || myMatch.getAmountOfLettersLeft() == 0) {
						String winner = null;
						String msg = "";
						
						if(myMatch.getTotalScorePlayerOne() > myMatch.getTotalScorePlayerTwo()) {
							winner = myMatch.getPlayerOne();
							msg = winner + " heeft gewonnen!";
						}
						else if(myMatch.getTotalScorePlayerOne() < myMatch.getTotalScorePlayerTwo()) {
							winner = myMatch.getPlayerTwo();
							msg = winner + " heeft gewonnen!";
						}
						else {
							msg = "Het is gelijkspel!";
						}
						
						showAlert("Er zijn niet genoeg letters meer over! " + msg);

						Match.updateGameState(myMatch.getGameId(), "finished", winner);
						
						new GamesController().create();
					} else {
						Match.initNewHand(myMatch.getGameId());
						updateMyMatch();
						setMatch();
						setMoveHistory();
					}
				}else {
					showAlert("Er is iets misgegaan. Probeer opnieuw.");
				}
			}else {
				new GamesController().create();
			}
		}else {
			showAlert("Er is iets misgegaan. Probeer opnieuw.");
		}
	}
	
	@FXML
	public void onPass(ActionEvent event) throws IOException {
		btnPass.setDisable(true);
		if(hand == null) return;
		if (myMatch.pass()) {
			addNewTurn(true);
		} else {
			showAlert("Er is iets misgegaan.");
		}
		btnPass.setDisable(false);
	}
	
	@FXML
	public void onPlaceBack(ActionEvent event) throws IOException {
		if(hand == null) return;
		board.placeBackAllHandCharacters(hand);
		render(gc);
	}
	
	@FXML
	public void resign(ActionEvent event) throws IOException {
		btnResign.setDisable(true);
		if (myMatch.resign()) {
			new GamesController().create();
		} else {
			showAlert("Er is iets misgegaan.");
		}
		btnResign.setDisable(false);
	}
	
	@FXML
	public void exit(MouseEvent event) throws IOException {
		System.exit(0);
	}
	
	@SuppressWarnings("deprecation")
	@FXML
	public void goBack(MouseEvent event) throws IOException {
		chatThread.stop();
		new GamesController().create();
	}
	
	@FXML
	public void createMessage(ActionEvent event) throws IOException {
		String message = chat.createMessage(this.txtChat.getText());
		if(message != null) {
			showAlert("Er is iets misgegaan.");
		}
		this.txtChat.clear();
		setChatMessages();
	}
	
	private void setLabels() {
		if(myMatch == null) return;
		lblPlayer1.setText(myMatch.getPlayerOne());
	    lblPlayer2.setText(myMatch.getPlayerTwo());
	    lblScorePlayer1.setText("" + myMatch.getTotalScorePlayerOne());
	    lblScorePlayer2.setText("" + myMatch.getTotalScorePlayerTwo());
	    lblLetters.setText("" + myMatch.getAmountOfLettersLeft());
	}
	
	private void setTrophys() {
	    try {
		    Turn turn = myMatch.getTurns().get(myMatch.getTurns().size() - 2);
		    
		    if((turn.getTurnPlayerOne().getScore() + turn.getTurnPlayerOne().getBonus()) > (turn.getTurnPlayerTwo().getScore() + turn.getTurnPlayerTwo().getBonus())) {
				iconWinner1.setVisible(true);
			} else if (turn.getTurnPlayerOne().getScore() < turn.getTurnPlayerTwo().getScore()) {
				iconWinner2.setVisible(true);
			}
	    } catch(Exception e) {}
	}
	
	@FXML
	public void switchChatAndHistory() {
		if(btnSwitchLV.getText().equals("Zetgeschiedenis")) {
			lblTitle.setText("Zetgeschiedenis");
			lvMessages.setVisible(false);
			lvMessages.setManaged(false);
			lvHistory.setVisible(true);
			lvHistory.setManaged(true);
			boxChatField.setVisible(false);
			boxChatField.setManaged(false);
			setMoveHistory();
			btnSwitchLV.setText("Chat");
		} else {
			lblTitle.setText("Chat");
			lvHistory.setVisible(false);
			lvHistory.setManaged(false);
			lvMessages.setVisible(true);
			lvMessages.setManaged(true);
			boxChatField.setVisible(true);
			boxChatField.setManaged(true);
			setChatMessages();
			btnSwitchLV.setText("Zetgeschiedenis");
		}
	}
	
	private void setChatMessages() {
		itemsMessages.clear();
		
		for (Message message : chat.getMessages()) {
			itemsMessages.add(message);
		}

		lvMessages.setItems(itemsMessages);
		lvMessages.setCellFactory(t -> new ChatCellFactory());
		lvMessages.scrollTo(itemsMessages.size());
	}
	
	private void setMoveHistory() {
		itemsMoves.clear();
		
		List<Turn> turns = myMatch.getTurns();
		for(int i = 0; i < turns.size(); i++) {
			if(i == turns.size() - 1 && myMatch.isMyTurn()) break;
			itemsMoves.add(turns.get(i));
		}

		lvHistory.setItems(itemsMoves);
		lvHistory.setCellFactory(t -> new ObserverMoveCellFactory(null, myMatch));
	}
	
	private static void showAlert(String message) {
		Alert alert = new Alert(AlertType.NONE, message, javafx.scene.control.ButtonType.OK);
		alert.setTitle("Bericht");
		alert.setOnCloseRequest(new EventHandler<DialogEvent>() {
			@Override
			public void handle(DialogEvent arg0) {
				alert.close();
			}
		});
		alert.show();
	}
	
	class Mouse{
		
		private HandCharacter hc;
		private boolean canDraw = true;
		
		private void pressed(int x, int y){
			if (canDraw) {
				hc = hand.isPressed(x, y);
				if(hc == null) {
					hc = board.isPressed(x - Board.getX(), y - Board.getY());
					if(hc != null) {
						int tileSize = Board.getTileSize();
						board.placeOnTile(x - Board.getX(), y - Board.getY(), Board.getTile((x - Board.getX()) / tileSize, (y - Board.getY()) / tileSize));
						hc.setDefaultSize();
						hand.placeBack(hc);
					}
				}
				
				if(hc != null) board.updateScore(null);
			}
		}
		
		private void released(int x, int y){
			if (canDraw) {
				if(hc != null) {
					if(board.placeOnTile(x - Board.getX(), y - Board.getY(), hc)) {
						hand.deleteCharacterFromHand(hc);
					}else {
						hc.setDefaultPosition();
					}
					board.updateScore(board.checkForWord());
				}
				render(gc);
			}
		}
		
		private void dragged(int x, int y){
			if (canDraw) {
				if(hc == null) return;
				hc.setX(x - hc.getSize() / 2);
				hc.setY(y - hc.getSize() / 2);
				render(gc);
			}
		}
		
		public void setCanDraw(boolean canDraw) {
			this.canDraw = canDraw;
		}
	}
}
