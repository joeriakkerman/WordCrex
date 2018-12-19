package game.models.tiles;

import javafx.scene.paint.Paint;

public class ThreeWordTile extends Tile {

	public ThreeWordTile(int x, int y) {
		super(x, y);
		paint = Paint.valueOf("0xed008c");
		multiplier = "3W";
	}
}
