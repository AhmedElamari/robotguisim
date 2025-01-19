package RobotSimulation;

import java.util.ArrayList;

/**
 * The <code>RobotArena</code> class represents the simulation arena where all
 * arena items (robots, obstacles, lights, beams, etc.) are managed, drawn, and
 * updated.
 *
 * <p>
 * This class maintains the dimensions of the arena, a list of all items, and
 * provides methods to load and save the arena state, add new items, and check
 * for collisions. It also manages special functionalities such as toggling a
 * blackout (which may affect the appearance of certain items).
 * </p>
 *
 * @author Ahmed Elamari
 * @version 1.0
 */
public class RobotArena {

	/** The maximum X dimension (width) of the arena. */
	double xMax;
	/** The maximum Y dimension (height) of the arena. */
	double yMax;

	/** List containing all arena items (robots, lights, obstacles, etc.). */
	protected ArrayList<ArenaItem> items;
	/** List of obstacles in the arena (for quick access, if needed). */
	protected ArrayList<Obstacle> obstacles;
	/** List of robots in the arena. */
	protected ArrayList<Robot> robots;
	/** List of lights in the arena. */
	protected ArrayList<Light> lights;
	/** List of whiskers in the arena. */
	protected ArrayList<Whisker> whiskers;
	/** List of beams in the arena. */
	protected ArrayList<Beam> beams;

	/** Indicates whether the arena is in blackout mode. */
	boolean isBlackOut = false;

	/**
	 * Default constructor that initializes the arena with predefined dimensions.
	 * The default dimensions are 500 (width) by 400 (height).
	 */
	RobotArena() {
		this(500, 400);
	}

	/**
	 * Constructs a <code>RobotArena</code> with custom dimensions.
	 *
	 * @param xS the width of the arena
	 * @param yS the height of the arena
	 */
	RobotArena(double xS, double yS) {
		xMax = xS;
		yMax = yS;
		items = new ArrayList<ArenaItem>();

		// Add sample items to the arena.
		// Example initial items include various types of robots, whiskers, lights,
		// beams, and obstacles.
		items.add(new PredatorRobot(300, 300, 10, 45, 2, this));
		items.add(new Prey(200, 200, 10, 45, 2, this));
		items.add(new Robot(100, 100, 10, 45, 2, this));
		items.add(new Whisker(100, 50, 10, 45, 1, this));
		items.add(new Obstacle(350, 100, 10));
		items.add(new Obstacle(100, 300, 10));

	}

	/**
	 * Generates a file string representing the current state of the arena.
	 *
	 * <p>
	 * The output includes the arena dimensions on the first line, followed by a
	 * properly formatted string for each item.
	 * </p>
	 *
	 * @return a <code>String</code> representing the arena and its items
	 */
	public String filestring() {
		StringBuilder sb = new StringBuilder();

		// First line: arena dimensions
		sb.append(xMax).append(" ").append(yMax).append("\n");

		// Add each item's file string, one per line.
		for (ArenaItem item : items) {
			sb.append(item.fileString()).append("\n");
		}

		return sb.toString();
	}

	/**
	 * Constructs a <code>RobotArena</code> by reading its state from a saved data
	 * string.
	 *
	 * <p>
	 * The saved data should contain the arena dimensions on the first line,
	 * followed by lines representing each arena item. This constructor parses the
	 * saved data and reconstructs the arena's contents.
	 * </p>
	 *
	 * @param savedData a <code>String</code> containing the serialized arena data
	 */
	public RobotArena(String savedData) {
		items = new ArrayList<>();
		String[] lines = savedData.split("\n");

		if (lines.length > 0) {
			// Parse the first line to get arena dimensions.
			String[] dims = lines[0].trim().split("\\s+");
			if (dims.length >= 2) {
				try {
					xMax = Double.parseDouble(dims[0]);
					yMax = Double.parseDouble(dims[1]);
					System.out.println("Arena dimensions: " + xMax + " x " + yMax);
				} catch (NumberFormatException e) {
					xMax = 500;
					yMax = 400;
				}
			}

			// Parse remaining lines for individual items.
			for (int i = 1; i < lines.length; i++) {
				String line = lines[i].trim();
				if (line.isEmpty()) {
					continue;
				}

				String[] parts = line.split("\\s+");
				if (parts.length < 1) {
					continue;
				}

				try {
					switch (parts[0]) {
					case "Robot":
						if (parts.length >= 7) {
							double x = Double.parseDouble(parts[1]);
							double y = Double.parseDouble(parts[2]);
							double rad = Double.parseDouble(parts[3]);
							char col = parts[4].charAt(0);
							double angle = Double.parseDouble(parts[5]);
							double speed = Double.parseDouble(parts[6]);
							Robot r = new Robot(x, y, rad, angle, speed, this);
							r.col = col;
							items.add(r);
							System.out.println("Added Robot");
						}
						break;

					case "Whisker":
						if (parts.length >= 7) {
							double x = Double.parseDouble(parts[1]);
							double y = Double.parseDouble(parts[2]);
							double rad = Double.parseDouble(parts[3]);
							char col = parts[4].charAt(0);
							double angle = Double.parseDouble(parts[5]);
							double speed = Double.parseDouble(parts[6]);
							Whisker w = new Whisker(x, y, rad, angle, speed, this);
							w.col = col;
							items.add(w);
							System.out.println("Added Whisker");
						}
						break;

					case "Beam":
						if (parts.length >= 8) {
							String type = parts[1]; // Should be "Robot" or "Light"
							double x = Double.parseDouble(parts[2]);
							double y = Double.parseDouble(parts[3]);
							double rad = Double.parseDouble(parts[4]);
							char col = parts[5].charAt(0);
							double angle = Double.parseDouble(parts[6]);
							double speed = Double.parseDouble(parts[7]);

							if (type.equals("Light")) {
								BeamLight bl = new BeamLight(x, y, rad, angle, speed, this);
								bl.col = col;
								items.add(bl);
								System.out.println("Added Beam Light");
							} else if (type.equals("Robot")) {
								Beam b = new Beam(x, y, rad, angle, speed, this);
								b.col = col;
								items.add(b);
								System.out.println("Added Beam Robot");
							}
						}
						break;

					case "Light":
						if (parts.length >= 5) {
							double x = Double.parseDouble(parts[1]);
							double y = Double.parseDouble(parts[2]);
							double rad = Double.parseDouble(parts[3]);
							char col = parts[4].charAt(0);
							Light l = new Light(x, y, rad);
							l.col = col;
							items.add(l);
							System.out.println("Added Light");
						}
						break;

					case "Obstacle":
						if (parts.length >= 5) {
							double x = Double.parseDouble(parts[1]);
							double y = Double.parseDouble(parts[2]);
							double rad = Double.parseDouble(parts[3]);
							char col = parts[4].charAt(0);
							Obstacle o = new Obstacle(x, y, rad);
							o.col = col;
							items.add(o);
							System.out.println("Added Obstacle");
						}
						break;

					case "Prey":
						if (parts.length >= 7) {
							double x = Double.parseDouble(parts[1]);
							double y = Double.parseDouble(parts[2]);
							double rad = Double.parseDouble(parts[3]);
							char col = parts[4].charAt(0);
							double angle = Double.parseDouble(parts[5]);
							double speed = Double.parseDouble(parts[6]);
							Prey p = new Prey(x, y, rad, angle, speed, this);
							p.col = col;
							items.add(p);
							System.out.println("Added Prey");
						}
						break;

					case "Predator":
						if (parts.length >= 7) {
							double x = Double.parseDouble(parts[1]);
							double y = Double.parseDouble(parts[2]);
							double rad = Double.parseDouble(parts[3]);
							char col = parts[4].charAt(0);
							double angle = Double.parseDouble(parts[5]);
							double speed = Double.parseDouble(parts[6]);
							int preyeaten = Integer.parseInt(parts[7]);
							PredatorRobot pr = new PredatorRobot(x, y, rad, angle, speed, this);
							pr.col = col;
							items.add(pr);
							System.out.println("Added Predator");
						}
						break;
					}
				} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
					System.out.println("Error parsing line: " + line);
					e.printStackTrace();
				}
			}
			System.out.println("Total items loaded: " + items.size());
		}
	}

	/**
	 * Returns the width of the arena.
	 *
	 * @return the arena's width
	 */
	public double getXSize() {
		return xMax;
	}

	/**
	 * Returns the height of the arena.
	 *
	 * @return the arena's height
	 */
	public double getYSize() {
		return yMax;
	}

	/**
	 * Draws the entire arena and its items on the specified canvas.
	 *
	 * <p>
	 * The background color is set based on whether the arena is in blackout mode.
	 * </p>
	 *
	 * @param mc the <code>MyCanvas</code> object used for drawing
	 */
	public void drawArena(MyCanvas mc) {
		mc.setBackgroundColor(isBlackOut ? 'l' : 'w');
		for (ArenaItem i : items) {
			i.drawItem(mc);
		}
	}

	/**
	 * Checks the state of all items in the arena.
	 *
	 * <p>
	 * This method calls the <code>checkItem</code> method on each arena item.
	 * Additionally, it removes any prey that are currently being eaten.
	 * </p>
	 */
	public void checkItems() {
		ArrayList<ArenaItem> itemsToRemove = new ArrayList<>();

		// First pass: Check each item and mark those that need to be removed.
		for (ArenaItem i : items) {
			i.checkItem(this);
			if (i instanceof Prey && ((Prey) i).isBeingEaten()) {
				itemsToRemove.add(i);
			}
		}

		// Remove all items marked for removal.
		items.removeAll(itemsToRemove);
	}

	/**
	 * Marks the specified prey as being eaten.
	 *
	 * @param prey the <code>ArenaItem</code> to mark as being eaten (if it is a
	 *             prey)
	 */
	public void removePrey(ArenaItem prey) {
		if (prey instanceof Prey) {
			((Prey) prey).beingEaten();
		}
	}

	/**
	 * Adjusts the state of all items in the arena by calling each item's
	 * <code>adjustItem</code> method.
	 */
	public void adjustItems() {
		for (ArenaItem i : items) {
			i.adjustItem();
		}
	}

	/**
	 * Sets the position of all robots in the arena to the specified coordinates.
	 *
	 * @param x the new X coordinate for all robots
	 * @param y the new Y coordinate for all robots
	 */
	public void setRobots(double x, double y) {
		for (ArenaItem i : items) {
			if (i instanceof Robot) {
				i.setXY(x, y);
			}
		}
	}

	/**
	 * Returns a list of string descriptions for all items in the arena.
	 *
	 * @return an <code>ArrayList</code> of <code>String</code> descriptions of
	 *         items
	 */
	public ArrayList<String> describeAll() {
		ArrayList<String> all = new ArrayList<>();
		for (ArenaItem i : items) {
			all.add(i.toString());
		}
		return all;
	}

	/**
	 * Checks if a robot can occupy a specified position without colliding with
	 * other robots.
	 *
	 * @param x     the X coordinate of the candidate position
	 * @param y     the Y coordinate of the candidate position
	 * @param rad   the radius of the robot
	 * @param notID the ID of the robot to exclude from the check
	 * @return <code>true</code> if the position is free; <code>false</code>
	 *         otherwise
	 */
	public boolean checkRobot(double x, double y, double rad, int notID) {
		boolean ans = true;
		for (ArenaItem i : items) {
			if (i instanceof Robot && i.getID() != notID && i.hitting(x, y, rad)) {
				ans = false;
			}
		}
		return ans;
	}

	/**
	 * Adds a new robot to the center of the arena.
	 *
	 * <p>
	 * The new robot is created at the midpoint of the arena dimensions.
	 * </p>
	 */
	public void addRobot() {
		Robot newRobot = new Robot(xMax / 2, yMax / 2, 10, 45, 2, this);
		items.add(newRobot);
	}

	/**
	 * Returns the robot located at the specified position, if any.
	 *
	 * @param x the X coordinate to check
	 * @param y the Y coordinate to check
	 * @return the <code>Robot</code> at the specified position, or
	 *         <code>null</code> if none is found
	 */
	public Robot getRoboAt(double x, double y) {
		for (ArenaItem item : items) {
			if (item instanceof Robot) {
				Robot r = (Robot) item;
				double dist = distanceBetween(x, y, r.getX(), r.getY());
				if (dist <= r.getRad()) {
					return r;
				}
			}
		}
		return null;
	}

	/**
	 * Calculates the distance between two points in the arena.
	 *
	 * @param x  the X coordinate of the first point
	 * @param y  the Y coordinate of the first point
	 * @param x2 the X coordinate of the second point
	 * @param y2 the Y coordinate of the second point
	 * @return the Euclidean distance between the two points
	 */
	private double distanceBetween(double x, double y, double x2, double y2) {
		return Math.sqrt(Math.pow(x - x2, 2) + Math.pow(y - y2, 2));
	}

	/**
	 * Attempts to compute a new direction angle when a robot collides with a wall
	 * or another item.
	 *
	 * <p>
	 * This method performs checks against arena boundaries, other robots, and
	 * obstacles. If a collision is detected, it attempts multiple randomized
	 * offsets to find a new valid angle.
	 * </p>
	 *
	 * @param x     the X coordinate of the robot
	 * @param y     the Y coordinate of the robot
	 * @param rad   the radius of the robot
	 * @param ang   the current angle of the robot
	 * @param notID the ID of the robot to exclude from collision checks
	 * @return the new angle that avoids collisions
	 */
	public double CheckRobotAngle(double x, double y, double rad, double ang, int notID) {
		double ans = ang;
		double randomOffset;
		int maxAttempts = 5;
		boolean collisionDetected = false;

		// Check collision with arena boundaries.
		if (x - rad < 0 || x + rad > xMax || y - rad < 0 || y + rad > yMax) {
			collisionDetected = true;
		}
		// Check collision with other robots.
		for (ArenaItem i : items) {
			if (i instanceof Robot && i.getID() != notID) {
				Robot ro = (Robot) i;
				double dist = Math.sqrt(Math.pow(ro.getX() - x, 2) + Math.pow(ro.getY() - y, 2));
				if (dist < ro.getRad() + rad) {
					collisionDetected = true;
					break;
				}
			}
			// Check collision with obstacles.
			if (i instanceof Obstacle) {
				double ox = i.getX();
				double oy = i.getY();
				double od = Math.sqrt(Math.pow(ox - x, 2) + Math.pow(oy - y, 2));
				if (od < i.getRad() + rad) {
					collisionDetected = true;
					break;
				}
			}
		}

		if (!collisionDetected) {
			return ans;
		}

		// Attempt to find a new angle that avoids collisions.
		for (int attempt = 0; attempt < maxAttempts; attempt++) {
			randomOffset = (Math.random() * 90) - 45;
			double candidateAngle = (ang + 180 + randomOffset) % 360;
			double radAngle = Math.toRadians(candidateAngle);
			double step = 2.0;
			double newX = x + step * Math.cos(radAngle);
			double newY = y + step * Math.sin(radAngle);
			// Check if the new position is within arena boundaries.
			if (newX - rad >= 0 && newX + rad <= xMax && newY - rad >= 0 && newY + rad <= yMax) {
				ans = candidateAngle;
				break;
			}
		}
		return ans;
	}

	/**
	 * Sets the position of all robots in the arena to the specified coordinates.
	 *
	 * @param x the new X coordinate for all robots
	 * @param y the new Y coordinate for all robots
	 */
	public void setRobot(double x, double y) {
		for (ArenaItem i : items) {
			if (i instanceof Robot) {
				i.setXY(x, y);
			}
		}
	}

	/**
	 * Adds a new obstacle at a random position in the arena.
	 *
	 * <p>
	 * This method attempts multiple times to find a free spot that does not overlap
	 * any existing item in the arena.
	 * </p>
	 */
	public void addObstacle() {
		double maxTries = 100;
		double obsRad = 10;
		boolean foundSpot = false;
		double candidateX = 0;
		double candidateY = 0;

		for (int i = 0; i < maxTries && !foundSpot; i++) {
			candidateX = obsRad + Math.random() * (xMax - 2 * obsRad);
			candidateY = obsRad + Math.random() * (yMax - 2 * obsRad);

			// Check if the candidate position overlaps any existing item.
			if (!overlapsAnyItem(candidateX, candidateY, obsRad)) {
				foundSpot = true;
			}
		}
		if (foundSpot) {
			Obstacle newObstacle = new Obstacle(candidateX, candidateY, obsRad);
			items.add(newObstacle);
			System.out.println("Added Obstacle at (" + candidateX + ", " + candidateY + ")");
		} else {
			System.out.println("Failed to place Obstacle after " + maxTries + " attempts.");
		}
	}

	/**
	 * Adds a new light at a random valid position in the arena.
	 *
	 * <p>
	 * The method ensures that the new light does not overlap any existing item. It
	 * tries for a fixed number of attempts before failing.
	 * </p>
	 */
	public void addLight() {
		double lightRadius = 10;
		int maxTries = 100;
		boolean foundSpot = false;
		double candidateX = 0;
		double candidateY = 0;

		for (int i = 0; i < maxTries && !foundSpot; i++) {
			candidateX = lightRadius + Math.random() * (xMax - 2 * lightRadius);
			candidateY = lightRadius + Math.random() * (yMax - 2 * lightRadius);

			if (!overlapsAnyItem(candidateX, candidateY, lightRadius)) {
				foundSpot = true;
			}
		}
		if (foundSpot) {
			Light newLight = new Light(candidateX, candidateY, lightRadius);
			items.add(newLight);
			System.out.println("Added Light at (" + candidateX + ", " + candidateY + ")");
		} else {
			System.out.println("Failed to place Light after " + maxTries + " attempts.");
		}
	}

	/**
	 * Checks if placing an item at the given coordinates with the specified radius
	 * would overlap any existing item in the arena.
	 *
	 * @param lx   the candidate X coordinate
	 * @param ly   the candidate Y coordinate
	 * @param lRad the radius of the candidate item
	 * @return <code>true</code> if there is an overlap; <code>false</code>
	 *         otherwise
	 */
	private boolean overlapsAnyItem(double lx, double ly, double lRad) {
		for (ArenaItem item : items) {
			double dx = lx - item.getX();
			double dy = ly - item.getY();
			double dist = Math.sqrt(dx * dx + dy * dy);
			if (dist < (lRad + item.getRad())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds a new whisker at a random position in the arena.
	 */
	public void addWhisker() {
		double x = Math.random() * xMax;
		double y = Math.random() * yMax;
		Whisker newWhisker = new Whisker(x, y, 10, 45, 1, this);
		items.add(newWhisker);
	}

	/**
	 * Adds a new beam at a random position in the arena.
	 */
	public void addBeam() {
		double x = Math.random() * xMax;
		double y = Math.random() * yMax;
		Beam newBeam = new Beam(x, y, 10, 45, 2, this);
		items.add(newBeam);
	}

	/**
	 * Adds a new beam light at a random position in the arena.
	 */
	public void addBeamLight() {
		double x = Math.random() * xMax;
		double y = Math.random() * yMax;
		BeamLight newBeamLight = new BeamLight(x, y, 10, 45, 2, this);
		items.add(newBeamLight);
	}

	/**
	 * Adds a new prey at a random position in the arena.
	 */
	public void addPrey() {
		double x = Math.random() * xMax;
		double y = Math.random() * yMax;
		Prey newPrey = new Prey(x, y, 10, 45, 2, this);
		items.add(newPrey);
	}

	/**
	 * Adds a new predator at a random position in the arena.
	 */
	public void addPredator() {
		double x = Math.random() * xMax;
		double y = Math.random() * yMax;
		PredatorRobot newPredator = new PredatorRobot(x, y, 10, 45, 2, this);
		items.add(newPredator);
	}

	/**
	 * Toggles the blackout mode for the arena.
	 *
	 * <p>
	 * When toggled, it will change the background color of the arena and notify
	 * items such as whiskers and robots to toggle their visual indicators
	 * accordingly (e.g., whisker or wheel color).
	 * </p>
	 */
	public void blackOut() {
		isBlackOut = !isBlackOut;

		for (ArenaItem item : items) {
			// Toggle color for whiskers.
			if (item instanceof Whisker) {
				((Whisker) item).toggleWhiskerColor(isBlackOut);
			}
			// Toggle wheel color for robots.
			if (item instanceof Robot) {
				((Robot) item).toggleWheelColor(isBlackOut);
			}
		}
	}

	/**
	 * Removes the specified robot from the arena.
	 *
	 * @param robot the <code>Robot</code> to remove from the arena
	 */
	public void removeRobot(Robot robot) {
		items.remove(robot);
	}
}
