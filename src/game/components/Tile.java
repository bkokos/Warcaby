package game.components;


import static game.util.Constans.*;

import com.sun.webkit.ContextMenu.ShowContext;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class Tile extends StackPane{	
	
	private Pawn pawn;	

	public Tile(boolean white){
		setPrefWidth(TILE_SIZE);
		setPrefHeight(TILE_SIZE);
		
		setMaxSize(TILE_SIZE, TILE_SIZE);
		setMinSize(TILE_SIZE, TILE_SIZE);	
		
		setBackground(new Background(new BackgroundFill(white ? Color.WHITE : Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
		setAlignment(Pos.CENTER);
		
//		setOnMouseMoved(e -> {
//			System.out.println(e.getSceneX());
//		});
		
//		setOnMouseExited(e->{
//			System.out.println("exit");
//		});
	}
	
	public Pawn getPawn() {
		return pawn;
	}

	public void setPawn(Pawn pawn) {
		this.pawn = pawn;
	}
	
	public boolean hasPawn(){
		return pawn != null;
	}
}
