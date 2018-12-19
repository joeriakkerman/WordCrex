package game;

import game.controllers.LoginController;
import game.models.Database;
import game.models.User;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
	
	private static Stage stage;
	public static User user;
	
	public static double getHeight() {
		return Main.stage.getHeight();
	}
	
	public static double getWidth() {
		return Main.stage.getWidth();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) {
		Main.stage = stage;
		Main.stage.initStyle(StageStyle.UNDECORATED);
		Main.stage.getIcons().add(new Image("images/WordCrex_Logo_Red.png"));
		Main.stage.setTitle("WordCrex");
		Main.stage.setMaximized(true);
		Main.stage.show();
		new LoginController().create();
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		try {
			Database.disconnect();
		} catch(Exception e) {
			System.out.println("Er was nog geen connection.");
		}
	}
	
	public static void setNewScene(Parent root) {
		Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
		stage.setScene(new Scene(root, screenSize.getWidth(), screenSize.getHeight()));
		stage.show();
	}
}
