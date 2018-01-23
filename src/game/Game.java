package game;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static game.util.Constans.*;
import game.components.Pawn;
import game.components.Tile;
import game.components.utils.PawnType;
import game.util.Dialog;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Game extends Application {

	private ColorPicker colorPicker1;
	private ColorPicker colorPicker2;

	private List<Pawn> player1Pawns = new ArrayList<>();
	private List<Pawn> player2Pawns = new ArrayList<>();

	private BorderPane root;

	private MenuBar menuBar;
	private GridPane gameBoard;

	private Label movesPlayer1;
	private Label movesPlayer2;

	private double mouseX, mouseY;
	private double previousX, previousY;

	private boolean isStartDirection;

	public static void main(String[] args) {
		launch(args);
	}

	Tile[][] tiles = new Tile[BOARD_SIZE][BOARD_SIZE];

	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("Warcaby");
		stage.setResizable(false);
		root = new BorderPane();
		root.setTop(createMenuBar());
		root.setRight(createScorePane());
		root.setCenter(createGameBoardPane());
		Scene scene = new Scene(root, MAIN_SCENE_WIDTH, MAIN_SCENE_HEIGHT);
		stage.setScene(scene);
		stage.show();

	}

	private GridPane createGameBoardPane() {
		isStartDirection = true;
		gameBoard = new GridPane();
		gameBoard.setAlignment(Pos.CENTER);
		gameBoard.setStyle("-fx-background-color: #3a3a3a;");
		// gameBoard.setBorder(new Border(new BorderStroke(Color.BLACK,
		// BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				Tile tile = new Tile((i + j) % 2 == 0);

				tile.setOnMouseClicked(e -> {
					if (e.getClickCount() == 2) {
						RotateTransition rotateTran = new RotateTransition(Duration.seconds(1), gameBoard);
						rotateTran.setByAngle(180);
						rotateTran.play();
						changeDirection();
					}
				});

				gameBoard.add(tile, j, i);
				tiles[i][j] = tile;

			}
		}
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				if (i <= 2 && (i + j) % 2 != 0) {
					Pawn pawn = createPawn(PawnType.one, TILE_SIZE * 0.38, colorPicker1.getValue());
					gameBoard.add(pawn, j, i);
					player1Pawns.add(pawn);
					tiles[i][j].setPawn(pawn);
				}

				if (i >= 5 && (i + j) % 2 != 0) {
					Pawn pawn = createPawn(PawnType.two, TILE_SIZE * 0.38, colorPicker2.getValue());
					gameBoard.add(pawn, j, i);
					player2Pawns.add(pawn);
					tiles[i][j].setPawn(pawn);
				}
			}
		}

		return gameBoard;
	}

	private MenuBar createMenuBar() {
		menuBar = new MenuBar();
		menuBar.setUseSystemMenuBar(true);
		Menu gameMenu = new Menu("Gra");
		MenuItem newGame = new MenuItem("Nowa gra");
		MenuItem endGame = new MenuItem("Zakończ grę");
		newGame.setOnAction(e -> {
			Optional<ButtonType> result = Dialog
					.creteYesNoAlert(AlertType.CONFIRMATION, "Warcaby", "Nowa gra", "Czy chcesz zacząć od nowa?")
					.showAndWait();
			if (result.get() == Dialog.yes) {
				if (colorPicker1.getValue().equals(colorPicker2.getValue())) {
					colorPicker1.setValue(Color.WHITE);
					colorPicker2.setValue(Color.BLACK);
				}
				root.setCenter(createGameBoardPane());
				movesPlayer1.setText("0");
				movesPlayer2.setText("0");
			}
		});
		endGame.setOnAction(e -> {
			Optional<ButtonType> result = Dialog.creteYesNoAlert(AlertType.CONFIRMATION, "Warcaby", "Zakończ grę",
					"Czy na pewno chcesz zakończyć grę?").showAndWait();

			if (result.get() == Dialog.yes) {
				Platform.exit();
			} else {

			}
		});
		List<MenuItem> gameItems = new ArrayList<>();
		gameItems.add(newGame);
		gameItems.add(endGame);
		gameMenu.getItems().addAll(gameItems);

		Menu helpMenu = new Menu("Pomoc");
		MenuItem gameRules = new MenuItem("Zasady gry");
		MenuItem about = new MenuItem("O Programie");
		gameRules.setDisable(false);
		about.setDisable(true);
		List<MenuItem> helpItems = new ArrayList<>();
		gameRules.setOnAction(e -> {
			final Stage newStage = new Stage();
			newStage.setTitle("Nowe okno");
			final WebView browser = new WebView();
			final WebEngine webEngine = browser.getEngine();

			ScrollPane scrollPane = new ScrollPane();
			scrollPane.setContent(browser);

			webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
				@Override
				public void changed(ObservableValue ov, State oldState, State newState) {

					if (newState == Worker.State.SUCCEEDED) {
						newStage.setTitle(webEngine.getLocation());
					}
				}
			});
			webEngine.load("https://pl.wikipedia.org/wiki/Warcaby");
			newStage.setScene(new Scene(scrollPane, 800, 600));
			newStage.show();
		});
		helpItems.add(gameRules);
		helpItems.add(about);
		helpMenu.getItems().addAll(helpItems);
		menuBar.getMenus().addAll(gameMenu, helpMenu);
		return menuBar;
	}

	private Pane createScorePane() {
		VBox scorePane = new VBox();
		scorePane.setMaxWidth(MAIN_SCENE_WIDTH - MAIN_SCENE_HEIGHT);
		try {
			scorePane.getStylesheets().add("styles/scorepane.css");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try (FileInputStream input = new FileInputStream("resources/pionek-na-szachownicy.png")) {
			Image image = new Image(input);
			ImageView imageView = new ImageView(image);
			imageView.setFitHeight(240);
			imageView.setFitWidth(240);
			scorePane.getChildren().add(imageView);
		} catch (IOException e) {
			e.printStackTrace();
		}

		scorePane.setStyle("-fx-background-color: #3a3a3a;");
		Label title = new Label("Warcaby");
		title.setId("title");

		Label player1 = new Label("Gracz 1");
		player1.setId("player-label");

		colorPicker1 = new ColorPicker(Color.WHITE);
		colorPicker1.setOnAction(e -> {
			if (colorPicker1.getValue().equals(colorPicker2.getValue())) {
				new Alert(AlertType.WARNING, "Nie możesz wybrać takiego samego koloru jak przeciwnik", ButtonType.OK)
						.showAndWait();
			} else {
				for (Pawn pawn : player1Pawns) {
					pawn.setFill(colorPicker1.getValue());
				}
			}
		});

		Label move1 = new Label("Moves: ");
		move1.setId("move-label");
		movesPlayer1 = new Label("0");
		movesPlayer1.setId("move-label");

		Label player2 = new Label("Gracz 2");
		player2.setId("player-label");

		colorPicker2 = new ColorPicker(Color.BLACK);
		colorPicker2.setOnAction(e -> {
			if (colorPicker2.getValue().equals(colorPicker1.getValue())) {
				new Alert(AlertType.WARNING, "Nie możesz wybrać takiego samego koloru jak przeciwnik", ButtonType.OK)
						.showAndWait();
			} else {
				for (Pawn pawn : player2Pawns) {
					pawn.setFill(colorPicker2.getValue());
				}
			}
		});

		Label move2 = new Label("Moves: ");
		move2.setId("move-label");
		movesPlayer2 = new Label("0");
		movesPlayer2.setId("move-label");

		scorePane.setAlignment(Pos.CENTER);
		scorePane.setSpacing(10);

		scorePane.getChildren().addAll(title, player1, move1, movesPlayer1, colorPicker1, player2, move2, movesPlayer2,
				colorPicker2);
		return scorePane;
	}

	private Pawn createPawn(PawnType pawnType, double radius, Paint color) {
		Pawn pawn = new Pawn(pawnType, radius, color);

		pawn.setOnMousePressed(e -> {
			pawn.setCursor(Cursor.CLOSED_HAND);
			previousX = pawn.getTranslateX();
			previousY = pawn.getTranslateY();
			mouseX = e.getSceneX();
			mouseY = e.getSceneY();
			pawn.toFront();

			System.out.println(convertPos((int) mouseX, 1));
			System.out.println(convertPos((int) mouseY, 30));

		});

		pawn.setOnMouseDragged(e -> {
			if (isStartDirection) {
				pawn.setTranslateX(e.getSceneX() - mouseX + previousX);
				pawn.setTranslateY(e.getSceneY() - mouseY + previousY);
			} else {
				pawn.setTranslateX((-1) * (e.getSceneX() - mouseX - previousX));
				pawn.setTranslateY((-1) * (e.getSceneY() - mouseY - previousY));
			}

		});

		pawn.setOnMouseReleased(e -> {
			pawn.setCursor(Cursor.OPEN_HAND);
			int previousX = convertPos((int) mouseX, 1);
			int previousY = convertPos((int) mouseY, 30);

			int currentX = convertPos((int) e.getSceneX(), 1);
			int currentY = convertPos((int) e.getSceneY(), 30);

			if (PawnType.one.equals(pawn.getPawnType())) {
				if (isStartDirection) {
					if (previousY - currentY == -1 && (previousX - currentX == 1 || previousX - currentX == -1)
							&& (currentX + currentY) % 2 != 0 && !tiles[currentY][currentX].hasPawn()) {
						gameBoard.getChildren().remove(pawn);
						gameBoard.add(pawn, currentX, currentY);
						pawn.setTranslateX(TILE_SIZE / 8 - 1);
						pawn.setTranslateY(TILE_SIZE / 8 - 10);
						tiles[previousY][previousX].setPawn(null);
						tiles[currentY][currentX].setPawn(pawn);

						Integer moves = Integer.parseInt(movesPlayer1.getText()) + 1;
						movesPlayer1.setText(moves.toString());
					} else if (previousY - currentY == -2 && (previousX - currentX == 2 || previousX - currentX == -2)
							&& (currentX + currentY) % 2 != 0 && (tiles[previousY - 1][previousX + 1].hasPawn()
									|| tiles[previousY - 1][previousX - 1].hasPawn())) {
						gameBoard.getChildren().remove(pawn);
						gameBoard.add(pawn, currentX, currentY);
						pawn.setTranslateX(TILE_SIZE / 8 - 1);
						pawn.setTranslateY(TILE_SIZE / 8 - 10);
						System.out.println("currentX: " + currentX + " currentY: " + currentY + " previousX: "
								+ (previousX) + " previousY: " + (previousY) + " previousX+1: " + (previousX + 1)
								+ " previousY+1: " + (previousY + 1));
						if (currentX - previousX == -2) {
							gameBoard.getChildren().remove(tiles[previousY + 1][previousX - 1].getPawn());
							tiles[previousY + 1][previousX - 1].setPawn(null);

						} else {
							gameBoard.getChildren().remove(tiles[previousY + 1][previousX + 1].getPawn());
							tiles[previousY + 1][previousX + 1].setPawn(null);
						}
						tiles[previousY][previousX].setPawn(null);
						tiles[currentY][currentX].setPawn(pawn);
					} else {
						pawn.setTranslateX(TILE_SIZE / 8 - 1);
						pawn.setTranslateY(TILE_SIZE / 8 - 10);
					}
				} else {
					if (previousY - currentY == 1 && (previousX - currentX == 1 || previousX - currentX == -1)
							&& (currentX + currentY) % 2 != 0 && !tiles[7 - currentY][7 - currentX].hasPawn()) {
						gameBoard.getChildren().remove(pawn);
						gameBoard.add(pawn, 7 - currentX, 7 - currentY);
						pawn.setTranslateX(TILE_SIZE / 8 - 1);
						pawn.setTranslateY(TILE_SIZE / 8 - 10);
						tiles[7 - previousY][7 - previousX].setPawn(null);
						tiles[7 - currentY][7 - currentX].setPawn(pawn);
						Integer moves = Integer.parseInt(movesPlayer2.getText()) + 1;
						movesPlayer2.setText(moves.toString());
					} else if (previousY - currentY == 2 && (previousX - currentX == 2 || previousX - currentX == -2)
							&& (currentX + currentY) % 2 != 0
							&& (tiles[7 - (previousY - 1)][7 - (previousX + 1)].hasPawn()
									|| tiles[7 - (previousY - 1)][7 - (previousX - 1)].hasPawn())) {
						gameBoard.getChildren().remove(pawn);
						gameBoard.add(pawn, 7 - currentX, 7 - currentY);
						pawn.setTranslateX(TILE_SIZE / 8 - 1);
						pawn.setTranslateY(TILE_SIZE / 8 - 10);

						if (currentX - previousX == -2) {
							System.out.println((7 - (previousY - 1)) + "; " + (7 - (previousX - 1)));
							gameBoard.getChildren().remove(tiles[7 - (previousY - 1)][7 - (previousX - 1)].getPawn());
							tiles[7 - (previousY - 1)][7 - (previousX - 1)].setPawn(null);

						} else {
							gameBoard.getChildren().remove(tiles[7 - (previousY - 1)][7 - (previousX + 1)].getPawn());
							tiles[7 - (previousY - 1)][7 - (previousX + 1)].setPawn(null);
						}
						tiles[7 - previousY][7 - previousX].setPawn(null);
						tiles[7 - currentY][7 - currentX].setPawn(pawn);

					} else {
						pawn.setTranslateX(TILE_SIZE / 8 - 1);
						pawn.setTranslateY(TILE_SIZE / 8 - 10);
					}
				}
			} else {
				if (isStartDirection) {
					if (previousY - currentY == 1 && (previousX - currentX == 1 || previousX - currentX == -1)
							&& (currentX + currentY) % 2 != 0 && !tiles[currentY][currentX].hasPawn()) {
						gameBoard.getChildren().remove(pawn);
						gameBoard.add(pawn, currentX, currentY);
						pawn.setTranslateX(TILE_SIZE / 8 - 1);
						pawn.setTranslateY(TILE_SIZE / 8 - 10);
						tiles[previousY][previousX].setPawn(null);
						tiles[currentY][currentX].setPawn(pawn);
						Integer moves = Integer.parseInt(movesPlayer2.getText()) + 1;
						movesPlayer2.setText(moves.toString());
					} else if (previousY - currentY == 2 && (previousX - currentX == 2 || previousX - currentX == -2)
							&& (currentX + currentY) % 2 != 0 && (tiles[previousY - 1][previousX + 1].hasPawn()
									|| tiles[previousY - 1][previousX - 1].hasPawn())) {
						gameBoard.getChildren().remove(pawn);
						gameBoard.add(pawn, currentX, currentY);
						pawn.setTranslateX(TILE_SIZE / 8 - 1);
						pawn.setTranslateY(TILE_SIZE / 8 - 10);
						System.out.println("currentX: " + currentX + " currentY: " + currentY + " previousX: "
								+ (previousX) + " previousY: " + (previousY) + " previousX+1: " + (previousX + 1)
								+ " previousY+1: " + (previousY + 1));
						if (currentX - previousX == -2) {
							gameBoard.getChildren().remove(tiles[previousY - 1][previousX - 1].getPawn());
							tiles[previousY - 1][previousX - 1].setPawn(null);

						} else {
							gameBoard.getChildren().remove(tiles[previousY - 1][previousX + 1].getPawn());
							tiles[previousY - 1][previousX + 1].setPawn(null);
						}
						tiles[previousY][previousX].setPawn(null);
						tiles[currentY][currentX].setPawn(pawn);
					} else {
						pawn.setTranslateX(TILE_SIZE / 8 - 1);
						pawn.setTranslateY(TILE_SIZE / 8 - 10);
					}
				} else {
					if (previousY - currentY == -1 && (previousX - currentX == 1 || previousX - currentX == -1)
							&& (currentX + currentY) % 2 != 0 && !tiles[7 - currentY][7 - currentX].hasPawn()) {
						gameBoard.getChildren().remove(pawn);
						gameBoard.add(pawn, 7 - currentX, 7 - currentY);
						pawn.setTranslateX(TILE_SIZE / 8 - 1);
						pawn.setTranslateY(TILE_SIZE / 8 - 10);
						tiles[7 - previousY][7 - previousX].setPawn(null);
						tiles[7 - currentY][7 - currentX].setPawn(pawn);

						Integer moves = Integer.parseInt(movesPlayer1.getText()) + 1;
						movesPlayer1.setText(moves.toString());
					} else if (previousY - currentY == -2 && (previousX - currentX == 2 || previousX - currentX == -2)
							&& (currentX + currentY) % 2 != 0
							&& (tiles[7 - (previousY + 1)][7 - previousX + 1].hasPawn()
									|| tiles[7 - (previousY + 1)][7 - (previousX - 1)].hasPawn())) {
						gameBoard.getChildren().remove(pawn);
						gameBoard.add(pawn, 7 - currentX, 7 - currentY);
						pawn.setTranslateX(TILE_SIZE / 8 - 1);
						pawn.setTranslateY(TILE_SIZE / 8 - 10);

						if (currentX - previousX == -2) {
							gameBoard.getChildren().remove(tiles[7 - (previousY + 1)][7 - (previousX - 1)].getPawn());
							tiles[7 - (previousY + 1)][7 - (previousX - 1)].setPawn(null);

						} else {
							gameBoard.getChildren().remove(tiles[7 - (previousY + 1)][7 - (previousX + 1)].getPawn());
							tiles[7 - (previousY + 1)][7 - (previousX + 1)].setPawn(null);
						}
						tiles[7 - previousY][7 - previousX].setPawn(null);
						tiles[7 - currentY][7 - currentX].setPawn(pawn);

					} else {
						pawn.setTranslateX(TILE_SIZE / 8 - 1);
						pawn.setTranslateY(TILE_SIZE / 8 - 10);
					}
				}
			}
		});
		return pawn;
	}

	private int convertPos(int currentScenePos, int shift) {
		if (currentScenePos >= shift && currentScenePos <= TILE_SIZE + shift - 1) {
			return 0;
		} else if (currentScenePos >= TILE_SIZE + shift && currentScenePos <= 2 * TILE_SIZE + shift - 1) {
			return 1;
		} else if (currentScenePos >= 2 * TILE_SIZE + shift && currentScenePos <= 3 * TILE_SIZE + shift - 1) {
			return 2;
		} else if (currentScenePos >= 3 * TILE_SIZE + shift && currentScenePos <= 4 * TILE_SIZE + shift - 1) {
			return 3;
		} else if (currentScenePos >= 4 * TILE_SIZE + shift && currentScenePos <= 5 * TILE_SIZE + shift - 1) {
			return 4;
		} else if (currentScenePos >= 5 * TILE_SIZE + shift && currentScenePos <= 6 * TILE_SIZE + shift - 1) {
			return 5;
		} else if (currentScenePos >= 6 * TILE_SIZE + shift && currentScenePos <= 7 * TILE_SIZE + shift - 1) {
			return 6;
		} else if (currentScenePos >= 7 * TILE_SIZE + shift && currentScenePos <= 8 * TILE_SIZE + shift - 1) {
			return 7;
		}
		return 0;
	}

	private void changeDirection() {
		if (isStartDirection) {
			isStartDirection = false;
		} else {
			isStartDirection = true;
		}
	}

}
