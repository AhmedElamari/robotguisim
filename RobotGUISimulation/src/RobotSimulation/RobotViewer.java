package RobotSimulation;

import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class RobotViewer extends Application {

	private MyCanvas mc;
	private AnimationTimer timer;
	private VBox rtPane; // Will hold labels describing arena items
	private ScrollPane rtScroll; // Wraps rtPane for scrolling
	private RobotArena arena;
	private TextFile tf = new TextFile("Text Files", "txt");

	// Example score property to demonstrate data binding
	private IntegerProperty scoreProperty = new SimpleIntegerProperty(0);

	/**
	 * Show About dialog
	 */
	private void showAbout() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("About");
		alert.setHeaderText(null);
		alert.setContentText("Robot Simulation\nVersion 1.0\nAuthor: Ahmed Elamari");
		alert.showAndWait();
	}

	/**
	 * When the mouse is pressed on the Canvas, set the robot to that location.
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

	/**
	 * Create the MenuBar with File and Help menus.
	 */
	MenuBar setMenu() {
		MenuBar menuBar = new MenuBar();

		// File menu
		Menu mFile = new Menu("File");

		MenuItem mNew = new MenuItem("New");
		mNew.setOnAction(actionEvent -> {
			arena = new RobotArena(400, 500);
			drawWorld();
			scoreProperty.set(0); // Reset example score
		});

		MenuItem mSave = new MenuItem("Save");
		mSave.setOnAction(e -> Save());

		MenuItem mLoad = new MenuItem("Load");
		mLoad.setOnAction(e -> {
			Load();
			drawWorld();
		});

		MenuItem mExit = new MenuItem("Exit");
		mExit.setOnAction(e -> {
			timer.stop();
			System.exit(0);
		});

		mFile.getItems().addAll(mNew, mSave, mLoad, mExit);

		// Help menu
		Menu mHelp = new Menu("Help");
		MenuItem mAbout = new MenuItem("About");
		mAbout.setOnAction(e -> showAbout());
		mHelp.getItems().addAll(mAbout);

		menuBar.getMenus().addAll(mFile, mHelp);
		return menuBar;
	}

	/**
	 * Creates the bottom button bar for controlling the simulation.
	 */
	private HBox setButtons() {
		Button btnStart = new Button("Start");
		btnStart.setTooltip(new Tooltip("Start the simulation"));
		btnStart.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
		btnStart.setOnAction(event -> timer.start());

		Button btnStop = new Button("Stop");
		btnStop.setTooltip(new Tooltip("Stop the simulation"));
		btnStop.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
		btnStop.setOnAction(event -> timer.stop());

		Button btnAdd = new Button("Add Robot");
		btnAdd.setTooltip(new Tooltip("Add a robot to the arena"));
		btnAdd.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
		btnAdd.setOnAction(event -> {
			arena.addRobot();
			drawWorld();
			// A quick fade animation for visual feedback
			FadeTransition fade = new FadeTransition(Duration.millis(1000), btnAdd);
			fade.setFromValue(1.0);
			fade.setToValue(0.3);
			fade.setCycleCount(2);
			fade.setAutoReverse(true);
			fade.play();
		});

		Button btnAddObstacle = new Button("Add Obstacle");
		btnAddObstacle.setTooltip(new Tooltip("Add an obstacle"));
		btnAddObstacle.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black;");
		btnAddObstacle.setOnAction(event -> {
			arena.addObstacle();
			drawWorld();
		});

		Button btnBlackOut = new Button("Black Out");
		btnBlackOut.setTooltip(new Tooltip("Toggle blackout mode"));
		btnBlackOut.setStyle("-fx-background-color: #343a40; -fx-text-fill: white;");
		btnBlackOut.setOnAction(event -> {
			arena.blackOut();
			drawWorld();
		});

		// Example "Score +1" button to show data binding usage
		Button btnScoreUp = new Button("Score +1");
		btnScoreUp.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white;");
		btnScoreUp.setOnAction(e -> scoreProperty.set(scoreProperty.get() + 1));

		HBox buttonBar = new HBox(10, btnStart, btnStop, btnAdd, btnAddObstacle, btnBlackOut, btnScoreUp);
		buttonBar.setPadding(new Insets(10));
		buttonBar.setAlignment(Pos.CENTER);
		return buttonBar;
	}

	/**
	 * Draw a score at coordinates (x, y) on the canvas.
	 */
	public void showScore(double x, double y, int score) {
		mc.showText(x, y, Integer.toString(score));
	}

	/**
	 * Populate the right panel with labels describing the arena items. Now centers
	 * the text and adds scroll if needed.
	 */
	public void drawStatus() {
		rtPane.getChildren().clear();
		ArrayList<String> alrRs = arena.describeAll();

		for (String s : alrRs) {
			Label label = new Label(s);
			// Center each label’s text
			label.setAlignment(Pos.CENTER);
			// Optionally apply styling
			label.setStyle("-fx-padding: 5; -fx-font-size: 12px; -fx-text-alignment: center;");
			rtPane.getChildren().add(label);
		}
	}

	/**
	 * Save the arena to file.
	 */
	public void Save() {
		if (tf.createFile()) {
			String arenaData = arena.filestring();
			tf.writeAllFile(arenaData);
			System.out.println("Saved to: " + tf.usedFileName());
		} else {
			System.out.println("Save operation cancelled");
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Save Failed");
			alert.setHeaderText(null);
			alert.setContentText("Failed to save the simulation.");
			alert.showAndWait();
		}
	}

	/**
	 * Load the arena from file.
	 */
	public void Load() {
		try {
			if (tf.openFile()) {
				System.out.println("Reading from: " + tf.usedFileName());
				String fileContent = tf.readAllFile();
				arena = new RobotArena(fileContent);
				drawWorld();
			} else {
				System.out.println("Load operation cancelled");
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Load Failed");
				alert.setHeaderText(null);
				alert.setContentText("Failed to load the simulation.");
				alert.showAndWait();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Load Error");
			alert.setContentText("An error occurred while loading: " + e.getMessage());
			alert.showAndWait();
		}
	}

	/**
	 * Redraw the arena on the canvas.
	 */
	public void drawWorld() {
		mc.clearCanvas();
		arena.drawArena(mc);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Robot GUI Simulation");

		BorderPane bp = new BorderPane();
		bp.setPadding(new Insets(10, 20, 10, 20));

		// ---- Top Menu ----
		bp.setTop(setMenu());

		// ---- Left: Canvas for drawing ----
		Group root = new Group();
		Canvas canvas = new Canvas(400, 500);
		root.getChildren().add(canvas);
		bp.setLeft(root);

		mc = new MyCanvas(canvas.getGraphicsContext2D(), 400, 500);
		setMouseEvents(canvas);

		// Create the initial arena
		arena = new RobotArena(400, 500);
		drawWorld();

		// Animation timer
		timer = new AnimationTimer() {
			@Override
			public void handle(long currentNanoTime) {
				arena.checkItems();
				arena.adjustItems();
				drawWorld();
				drawStatus();
			}
		};

		// ---- Right: a scrollable panel for statuses ----
		rtPane = new VBox(5);
		rtPane.setAlignment(Pos.TOP_CENTER);
		rtPane.setPadding(new Insets(5));

		rtScroll = new ScrollPane(rtPane);
		rtScroll.setFitToWidth(true); // Ensures content fits the width
		rtScroll.setStyle("-fx-background-color: #f8f9fa;");
		// You can also style the scroll bars, e.g., by CSS in an external file

		bp.setRight(rtScroll);

		// ---- Bottom: Buttons ----
		bp.setBottom(setButtons());

		// Example data binding: create a Label for the score
		Label scoreLabel = new Label();
		scoreLabel.setStyle("-fx-font-size: 14pt; -fx-font-weight: bold;");
		// Bind label text to score property
		scoreLabel.textProperty().bind(scoreProperty.asString("Score: %d"));

		// We’ll add both the MenuBar and the score label in a VBox
		VBox topVBox = new VBox(setMenu(), scoreLabel);
		bp.setTop(topVBox);

		Scene scene = new Scene(bp, 700, 600);

		// Make the layout responsive
		bp.prefHeightProperty().bind(scene.heightProperty());
		bp.prefWidthProperty().bind(scene.widthProperty());

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
