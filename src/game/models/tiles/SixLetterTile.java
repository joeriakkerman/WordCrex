package game.models.tiles;

import javafx.scene.paint.Paint;

public class SixLetterTile extends Tile {

	public SixLetterTile(int x, int y) {
		super(x, y);
		paint = Paint.valueOf("0x0b9444");
		multiplier = "6L";
	}
}