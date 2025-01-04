package RobotSimulation;

import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RobotViewer extends Application {
	private MyCanvas mc;
	private AnimationTimer timer;
	private VBox rtPane;
	private RobotArena arena;

	private void showAbout() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("About");
		alert.setHeaderText(null);
		alert.setContentText("Robot Simulation\nVersion 1.0\nAuthor: Ahmed Elamari");
		alert.showAndWait();

	}

	/**
	 * set up a mouse event - when mouse pressed, robot will be put there.
	 */
	void setMouseEvents(Canvas canvas) {
		canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				arena.setRobot(e.getX(), e.getY());
				drawWorld();
				drawStatus();
			}
		});
	}

	MenuBar setMenu() {
		MenuBar menuBar = new MenuBar();
		Menu mFile = new Menu("File");
		MenuItem exit = new MenuItem("Exit");
		exit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent t) {
				timer.stop();
				System.exit(0);
			}
		});
		mFile.getItems().addAll(exit);

		Menu mHelp = new Menu("Help");
		MenuItem mAbout = new MenuItem("About");
		mAbout.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				showAbout();
			}
		});
		mHelp.getItems().addAll(mAbout);
		menuBar.getMenus().addAll(mFile, mHelp);
		return menuBar;
	}

	private HBox setButtons() {
		Button btnStart = new Button("Start");
		btnStart.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				timer.start();
			}
		});

		Button btnStop = new Button("Stop");
		btnStop.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				timer.stop();
			}
		});

		Button btnAdd = new Button("Add Robot");
		btnAdd.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				arena.addRobot();
				drawWorld();
			}
		});
		Button btnAddObstacle = new Button("Add Obstacle");
		btnAddObstacle.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				arena.addObstacle();
				drawWorld();
			}
		});

		return new HBox(new Label("Control: "), btnStart, btnStop, new Label("Add: "), btnAdd, btnAddObstacle);
	}

	public void showScore(double x, double y, int score) {
		mc.showText(x, y, Integer.toString(score));
	}

	public void drawWorld() {
		mc.clearCanvas();
		arena.drawArena(mc);
	}

	public void drawStatus() {
		rtPane.getChildren().clear();
		ArrayList<String> allRs = arena.describeAll();
		for (String s : allRs) {
			rtPane.getChildren().add(new Label(s));
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Robot GUI Simulation");
		BorderPane bp = new BorderPane();
		bp.setPadding(new Insets(10, 20, 10, 20));
		bp.setTop(setMenu());
		Group root = new Group();
		Canvas canvas = new Canvas(400, 500);
		root.getChildren().add(canvas);
		bp.setLeft(root);

		mc = new MyCanvas(canvas.getGraphicsContext2D(), 400, 500);

		setMouseEvents(canvas);

		arena = new RobotArena(400, 500);
		drawWorld();

		timer = new AnimationTimer() { // set up timer
			public void handle(long currentNanoTime) { // and its action when on
				arena.checkItems(); // check the angle of all balls
				arena.adjustItems(); // move all balls
				drawWorld(); // redraw the world
				drawStatus(); // indicate where balls are
			}
		};

		rtPane = new VBox();
		rtPane.setAlignment(Pos.TOP_CENTER);
		rtPane.setPadding(new Insets(5, 75, 75, 5));
		bp.setRight(rtPane);

		bp.setBottom(setButtons());

		Scene scene = new Scene(bp, 700, 600);
		bp.prefHeightProperty().bind(scene.heightProperty());
		bp.prefWidthProperty().bind(scene.widthProperty());

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
