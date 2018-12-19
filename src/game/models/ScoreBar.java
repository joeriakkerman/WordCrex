package game.models;

import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ScoreBar {
	
	private List<Score> scores;
	private Paint paint;
	private final int size, arcSize;
	
	public ScoreBar() {
		size = Board.getTileSize() / 2;
		arcSize = 5;
		paint = Paint.valueOf("0xffbb00");
	}
	
	public void updateScore(List<Score> score) {
		this.scores = score;
	}
	
	public void render(GraphicsContext gc){
		if(scores != null) {
			for(int i = 0; i < scores.size(); i++) {
				gc.setStroke(paint);
				gc.strokeRect(scores.get(i).x, scores.get(i).y, scores.get(i).width, scores.get(i).height);
			}
			final int id = 0;
			if(scores.size() >= 1){
				gc.setFill(paint);
				gc.fillRoundRect(scores.get(id).x + scores.get(id).width - size / 2, scores.get(id).y - size / 2, size, size, arcSize, arcSize);
				gc.setFill(Paint.valueOf("0xffffff"));
				gc.setFont(Font.font("Open Sans", FontWeight.EXTRA_BOLD, 14));
				gc.fillText("" + getTotalScore(), scores.get(id).x + scores.get(id).width, scores.get(id).y, size);
			}
		}
	}
	
	public int getTotalScore() {
		int score = 0;
		for(int i = 0; i < scores.size(); i++) score += scores.get(i).score;
		return score;
	}
	
	public class Score {
		
		public int x, y, width, height, score;
		public String word;
		
		public Score(int x, int y, int width, int height, int score, String word) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.score = score;
			this.word = word;
		}
		
		public boolean equals(Score score) {
			if(x == score.x && y == score.y && width == score.width && height == score.height && word.equals(score.word) && this.score == score.score)
				return true;
			return false;
		}
	}
}
