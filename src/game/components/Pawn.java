package game.components;

import static game.util.Constans.*;
import game.components.utils.PawnType;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class Pawn extends Circle{

	
	
	private PawnType pawnType;
	
	public Pawn(PawnType pawnType, double radius, Paint color){
		super(radius, color);
		this.pawnType = pawnType;
		setTranslateX(TILE_SIZE/8-1);
		setTranslateY(TILE_SIZE/8-10);
		
		setStroke(Color.LIGHTGRAY);
		setStrokeWidth(3);
		
		setCursor(Cursor.OPEN_HAND);
		
		
		
		ScaleTransition scaleTran = new ScaleTransition(Duration.seconds(1), this);
        scaleTran.setFromX(0);
        scaleTran.setFromY(0);
		scaleTran.setToX(1);
        scaleTran.setToY(1);
        scaleTran.setCycleCount(1);
        scaleTran.setAutoReverse(false);
        scaleTran.play();
	}
	
	public PawnType getPawnType() {
		return pawnType;
	}
	public void setPawnType(PawnType pawnType) {
		this.pawnType = pawnType;
	}
	
}
