package RobotSimulation;

import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * The <code>RobotViewer</code> class represents the primary GUI application for
 * the Robot Simulation. It builds the user interface, manages user interaction
 * via mouse and menus, and drives the simulation via an animation timer.
 *
 * <p>
 * This class creates and configures the main window, sets up the drawing
 * canvas, binds simulation data (e.g. score), and handles events such as adding
 * robots, obstacles, lights, and toggling a blackout.
 * </p>
 *
 * @author Ahmed Elamari
 * @version 2.0
 */
public class RobotViewer extends Application {

	/** The custom canvas used to render the simulation. */
	private MyCanvas mc;
	/** Timer to drive the animation loop of the simulation. */
	private AnimationTimer timer;
	/** Right panel (VBox) for displaying status labels about arena items. */
	private VBox rtPane;
	/** ScrollPane that wraps the right panel for scrolling if necessary. */
	private ScrollPane rtScroll;
	/** The simulation arena containing all robots, obstacles, lights, etc. */
	private RobotArena arena;
	/** Helper class for file operations on simulation data. */
	private TextFile tf = new TextFile("Text Files", "txt");
	/** The currently selected robot (for example when context menu is shown). */
	private Robot selectedRobot = null;
	/** Example score property demonstrating data binding with UI controls. */
	private IntegerProperty scoreProperty = new SimpleIntegerProperty(0);

	/**
	 * Displays an "About" dialog providing basic application information.
	 */
	private void showAbout() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("About");
		alert.setHeaderText(null);
		alert.setContentText("Robot Simulation\nVersion 1.0\nAuthor: Ahmed Elamari");
		alert.showAndWait();
	}

	/**
	 * Sets up mouse event handlers for the drawing canvas.
	 *
	 * <p>
	 * This method registers event handlers to support:
	 * <ul>
	 * <li>Right-click: select a robot and display a context menu.</li>
	 * <li>Middle-click: move all robots to the clicked location.</li>
	 * <li>Left-drag: move the selected robot.</li>
	 * </ul>
	 * </p>
	 *
	 * @param canvas the Canvas on which mouse events are to be handled
	 */
	void setMouseEvents(Canvas canvas) {
		// Handle mouse press
		canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
			// Right-click: attempt to select a robot and show context menu
			if (e.getButton() == MouseButton.SECONDARY) {
				Robot clickedRobot = arena.getRoboAt(e.getX(), e.getY());
				if (clickedRobot != null) {
					ContextMenu contextMenu = createRobotContextMenu(clickedRobot);
					contextMenu.show(canvas, e.getScreenX(), e.getScreenY());
					selectedRobot = clickedRobot;
				} else {
					selectedRobot = null;
				}
				drawWorld();
				drawStatus();
			}
			// Middle-click: move all robots to the clicked location
			else if (e.getButton() == MouseButton.MIDDLE) {
				arena.setRobot(e.getX(), e.getY());
				drawWorld();
			}
		});

		// Handle mouse drag (typically with left button)
		canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
			// If a robot is selected and the primary button is used, update its position.
			if (selectedRobot != null && e.getButton() == MouseButton.PRIMARY) {
				selectedRobot.setXY(e.getX(), e.getY());
				drawWorld();
			}
		});
	}

	/**
	 * Creates a context menu for interacting with a robot.
	 *
	 * <p>
	 * This menu provides options to select or delete the given robot.
	 * </p>
	 *
	 * @param robot the robot for which the context menu is created
	 * @return the ContextMenu configured for the robot
	 */
	private ContextMenu createRobotContextMenu(Robot robot) {
		ContextMenu menu = new ContextMenu();
		MenuItem selectItem = new MenuItem("Select");
		selectItem.setOnAction(e -> {
			selectedRobot = robot;
			drawWorld();
		});
		MenuItem deleteItem = new MenuItem("Delete");
		deleteItem.setOnAction(e -> {
			arena.removeRobot(robot);
			drawWorld();
			drawStatus();
		});
		menu.getItems().addAll(selectItem, deleteItem);
		return menu;
	}

	/**
	 * Creates and returns the MenuBar containing the File and Help menus.
	 *
	 * <p>
	 * The File menu allows creating a new arena, saving, loading, and exiting. The
	 * Help menu contains an About item.
	 * </p>
	 *
	 * @return the configured MenuBar
	 */
	MenuBar setMenu() {
		MenuBar menuBar = new MenuBar();

		// File menu
		Menu mFile = new Menu("File");

		MenuItem mNew = new MenuItem("New");
		mNew.setOnAction(actionEvent -> {
			arena = new RobotArena(400, 500);
			drawWorld();
			scoreProperty.set(0); // Reset the score property
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
	 *
	 * <p>
	 * This pane includes buttons for starting/stopping the simulation, adding
	 * robots, obstacles, lights, and toggling blackout mode.
	 * </p>
	 *
	 * @return an HBox containing the control buttons
	 */
	private HBox setButtons() {
		// Start simulation button
		Button btnStart = new Button("Start");
		btnStart.setTooltip(new Tooltip("Start the simulation"));
		btnStart.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
		btnStart.setOnAction(event -> timer.start());

		// Stop simulation button
		Button btnStop = new Button("Stop");
		btnStop.setTooltip(new Tooltip("Stop the simulation"));
		btnStop.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
		btnStop.setOnAction(event -> timer.stop());

		// === SplitMenuButton for adding different robots ===
		SplitMenuButton btnAddRobot = new SplitMenuButton();
		btnAddRobot.setText("Add Robot");
		btnAddRobot.setTooltip(new Tooltip("Add a robot to the arena"));
		btnAddRobot.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");

		// Default action (if user clicks main button area)
		btnAddRobot.setOnAction(e -> {
			arena.addRobot(); // The "normal" robot
			drawWorld();
			playFadeAnimation(btnAddRobot);
		});

		// Menu items for different types of robots
		MenuItem normalRobotItem = new MenuItem("Normal Robot");
		normalRobotItem.setOnAction(e -> {
			arena.addRobot();
			drawWorld();
			playFadeAnimation(btnAddRobot);
		});

		MenuItem whiskerRobotItem = new MenuItem("Whisker Robot");
		whiskerRobotItem.setOnAction(e -> {
			arena.addWhisker(); // You'd implement addWhiskerRobot() in RobotArena
			drawWorld();
			playFadeAnimation(btnAddRobot);
		});
		MenuItem beamRobotItem = new MenuItem("Beam Robot");
		beamRobotItem.setOnAction(e -> {
			arena.addBeamLight(); // You'd implement addLightRobot() in RobotArena
			drawWorld();
			playFadeAnimation(btnAddRobot);
		});
		MenuItem lightRobotItem = new MenuItem("Light Robot");
		lightRobotItem.setOnAction(e -> {
			arena.addBeamLight(); // You'd implement addLightRobot() in RobotArena
			drawWorld();
			playFadeAnimation(btnAddRobot);
		});

		// Add all robot-type items to the dropdown
		btnAddRobot.getItems().addAll(normalRobotItem, whiskerRobotItem, beamRobotItem, lightRobotItem);

		// Add obstacle button
		Button btnAddObstacle = new Button("Add Obstacle");
		btnAddObstacle.setTooltip(new Tooltip("Add an obstacle"));
		btnAddObstacle.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black;");
		btnAddObstacle.setOnAction(event -> {
			arena.addObstacle();
			drawWorld();
		});

		// Add light button
		Button btnAddLight = new Button("Add Light");
		btnAddLight.setTooltip(new Tooltip("Add a light source"));
		btnAddLight.setStyle("-fx-background-color: #FFD700; -fx-text-fill: white;");
		btnAddLight.setOnAction(event -> {
			arena.addLight();
			drawWorld();
		});

		// Black out button
		Button btnBlackOut = new Button("Black Out");
		btnBlackOut.setTooltip(new Tooltip("Black out the arena"));
		btnBlackOut.setStyle("-fx-background-color: #343a40; -fx-text-fill: white;");
		btnBlackOut.setOnAction(event -> {
			arena.blackOut();
			drawWorld();
		});

		// Put them all in an HBox
		HBox buttonBar = new HBox(10, btnStart, btnStop, btnAddRobot, // <-- Our new SplitMenuButton
				btnAddObstacle, btnAddLight, btnBlackOut);
		buttonBar.setPadding(new Insets(10));
		buttonBar.setAlignment(Pos.CENTER);
		return buttonBar;
	}

	/**
	 * Simple utility method to do a quick fade animation for visual feedback.
	 */
	private void playFadeAnimation(Node node) {
		FadeTransition fade = new FadeTransition(Duration.millis(1000), node);
		fade.setFromValue(1.0);
		fade.setToValue(0.3);
		fade.setCycleCount(2);
		fade.setAutoReverse(true);
		fade.play();
	}

	/**
	 * Displays the current score on the canvas at the specified coordinates.
	 *
	 * @param x     the x-coordinate for the score display
	 * @param y     the y-coordinate for the score display
	 * @param score the score to display
	 */
	public void showScore(double x, double y, int score) {
		mc.showText(x, y, Integer.toString(score));
	}

	/**
	 * Populates the right status panel with labels describing the arena items.
	 *
	 * <p>
	 * This method clears the existing labels and re-adds updated descriptions for
	 * each item in the arena.
	 * </p>
	 */
	public void drawStatus() {
		rtPane.getChildren().clear();
		ArrayList<String> descriptions = arena.describeAll();

		for (String s : descriptions) {
			Label label = new Label(s);
			label.setAlignment(Pos.CENTER);
			label.setStyle("-fx-padding: 5; -fx-font-size: 12px; -fx-text-alignment: center;");
			rtPane.getChildren().add(label);
		}
	}

	/**
	 * Saves the current arena state to a file.
	 *
	 * <p>
	 * This method uses the TextFile helper class to create a file and write the
	 * serialized arena data to it.
	 * </p>
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
	 * Loads the arena state from a file.
	 *
	 * <p>
	 * This method reads saved arena data and reconstructs the simulation arena. If
	 * an error occurs, an error dialog is shown.
	 * </p>
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
	 * Redraws the simulation arena on the canvas.
	 *
	 * <p>
	 * This method clears the canvas and then invokes the arena's drawing routine.
	 * </p>
	 */
	public void drawWorld() {
		mc.clearCanvas();
		arena.drawArena(mc);
	}

	/**
	 * The main entry point for the JavaFX application.
	 *
	 * <p>
	 * This method sets up the primary Stage, constructs all UI elements including
	 * the menu, canvas, right status panel, and bottom control buttons, and binds
	 * an animation timer to drive the simulation updates.
	 * </p>
	 *
	 * @param primaryStage the primary stage for this application
	 * @throws Exception if an error occurs during initialization
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Robot GUI Simulation");

		BorderPane bp = new BorderPane();
		bp.setPadding(new Insets(10, 20, 10, 20));

		// ---- Top: Menu and Score ----
		MenuBar menuBar = setMenu();
		Label scoreLabel = new Label();
		scoreLabel.setStyle("-fx-font-size: 14pt; -fx-font-weight: bold;");
		// Bind score property to update the score label dynamically
		scoreLabel.textProperty().bind(scoreProperty.asString("Score: %d"));
		VBox topVBox = new VBox(menuBar, scoreLabel);
		bp.setTop(topVBox);

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

		// ---- Animation Timer: Simulation Loop ----
		timer = new AnimationTimer() {
			@Override
			public void handle(long currentNanoTime) {
				arena.checkItems();
				arena.adjustItems();
				drawWorld();
				drawStatus();
			}
		};

		// ---- Right: Scrollable Status Panel ----
		rtPane = new VBox(5);
		rtPane.setAlignment(Pos.TOP_CENTER);
		rtPane.setPadding(new Insets(5));
		rtScroll = new ScrollPane(rtPane);
		rtScroll.setFitToWidth(true);
		rtScroll.setStyle("-fx-background-color: #f8f9fa;");
		bp.setRight(rtScroll);

		// ---- Bottom: Control Buttons ----
		bp.setBottom(setButtons());

		Scene scene = new Scene(bp, 700, 600);
		bp.prefHeightProperty().bind(scene.heightProperty());
		bp.prefWidthProperty().bind(scene.widthProperty());

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * The main method launches the JavaFX application.
	 *
	 * @param args command-line arguments (not used)
	 */
	public static void main(String[] args) {
		Application.launch(args);
	}
}
