package RobotSimulation;

import java.util.ArrayList;

public class RobotArena {
	double xMax, yMax; // Maximum dimensions of the arena
	protected ArrayList<ArenaItem> items; // List of items in the arena
	protected ArrayList<Obstacle> obstacles; // List of obstacles in the arena
	protected ArrayList<Robot> robots; // List of robots in the arena
	protected ArrayList<Light> lights; // List of lights in the arena
	protected ArrayList<Whisker> whiskers; // List of whiskers in the arena
	protected ArrayList<Beam> beams; // List of beams in the arena
	boolean isBlackOut = false; // Indicates if the arena lights are out

	// Default constructor initializes the arena with predefined dimensions
	RobotArena() {
		this(500, 400);
	}

	// Constructor with custom dimensions for the arena
	RobotArena(double xS, double yS) {
		xMax = xS;
		yMax = yS;
		items = new ArrayList<ArenaItem>();
		// Example initial robot
		items.add(new BeamLight(200, 200, 10, 45, 2, this));
	}

	public double getXSize() {
		return xMax; // Returns the width of the arena
	}

	public double getYSize() {
		return yMax; // Returns the height of the arena
	}

	// Draws the arena and its items on the provided canvas
	public void drawArena(MyCanvas mc) {
		if (isBlackOut) {
			mc.setBackgroundColor('d'); // Set background to dark if blackout
		} else {
			mc.setBackgroundColor('w'); // Set background to white otherwise
		}
		for (ArenaItem i : items) {
			i.drawItem(mc); // Draw each item in the arena
		}
	}

	// Checks the state of all items in the arena
	public void checkItems() {
		for (ArenaItem i : items) {
			i.checkItem(this); // Check each item for updates or interactions
		}
	}

	// Adjusts the state of all items in the arena
	public void adjustItems() {
		for (ArenaItem i : items) {
			i.adjustItem(); // Call the adjust method for each item
		}
	}

	// Sets the position of all robots in the arena to the specified coordinates
	public void setRobots(double x, double y) {
		for (ArenaItem i : items) {
			if (i instanceof Robot) {
				i.setXY(x, y); // Update robot's position
			}
		}
	}

	// Returns a description of all items in the arena
	public ArrayList<String> describeAll() {
		ArrayList<String> all = new ArrayList<>(); // List to hold descriptions
		for (ArenaItem i : items) {
			all.add(i.toString()); // Add each item's description
		}
		return all; // Return the list of descriptions
	}

	// Checks if a robot can occupy a specified position
	public boolean checkRobot(double x, double y, double rad, int notID) {
		boolean ans = true; // Assume the position is free initially
		for (ArenaItem i : items) {
			if (i instanceof Robot && i.getID() != notID && i.hitting(x, y, rad)) {
				ans = false; // A collision is detected
			}
		}
		return ans; // Return whether the position is available
	}

	// Adds a new robot to the center of the arena
	public void addRobot() {
		Robot newRobot = new Robot(xMax / 2, yMax / 2, 10, 45, 2, this);
		items.add(newRobot); // Add the new robot to the items list
	}

	/**
	 * Modified to randomize the angle of the robot's bounce when colliding with a
	 * wall or another item.
	 */
	/**
	 * Attempts to compute a new direction angle when a robot collides with a wall
	 * or another robot. Tries multiple randomized offsets to avoid getting stuck
	 * near the walls.
	 */
	public double CheckRobotAngle(double x, double y, double rad, double ang, int notID) {
		double ans = ang; // Start with the current angle
		double randomOffset;
		int maxAttempts = 5; // Maximum attempts to find a new angle
		boolean collisionDetected = false; // Flag to track collisions
		// Check for collisions with arena boundaries
		if (x - rad < 0 || x + rad > xMax || y - rad < 0 || y + rad > yMax) {
			collisionDetected = true; // Collision with wall detected
		}
		// Check for collisions with other robots or obstacles
		for (ArenaItem i : items) {
			// Check collision with other robots
			if (i instanceof Robot && i.getID() != notID) {
				Robot ro = (Robot) i;
				double dist = Math.sqrt(Math.pow(ro.getX() - x, 2) + Math.pow(ro.getY() - y, 2));
				if (dist < ro.getRad() + rad) {
					collisionDetected = true; // Collision with another robot detected
					break;
				}
			}
			// Check collision with obstacles
			if (i instanceof Obstacle) {
				double ox = i.getX();
				double oy = i.getY();
				double od = Math.sqrt(Math.pow(ox - x, 2) + Math.pow(oy - y, 2));
				if (od < i.getRad() + rad) {
					collisionDetected = true; // Collision with an obstacle detected
					break;
				}
			}
		}
		if (!collisionDetected) {
			return ans; // No collision, return the original angle
		}
		// Attempt to find a new angle that avoids collisions
		for (int attempt = 0; attempt < maxAttempts; attempt++) {
			randomOffset = (Math.random() * 90) - 45; // Random offset between -45 and +45 degrees
			double candidateAngle = (ang + 180 + randomOffset) % 360; // Calculate candidate angle
			double radAngle = Math.toRadians(candidateAngle); // Convert to radians
			double step = 2.0; // Step size for movement
			double newX = x + step * Math.cos(radAngle); // Calculate new X position
			double newY = y + step * Math.sin(radAngle); // Calculate new Y position
			// Check if the new position is within arena boundaries
			if (newX - rad >= 0 && newX + rad <= xMax && newY - rad >= 0 && newY + rad <= yMax) {
				ans = candidateAngle; // Update the angle if valid
				break; // Exit the loop on success
			}
		}
		return ans; // Return the new angle
	}

	// Sets the position of all robots in the arena to the specified coordinates
	// (similar to setRobots)
	public void setRobot(double x, double y) {
		for (ArenaItem i : items) {
			if (i instanceof Robot) {
				i.setXY(x, y); // Update robot's position
			}
		}
	}

	// Adds a new obstacle at a random position in the arena
	public void addObstacle() {
		double x = Math.random() * xMax; // Random X position
		double y = Math.random() * yMax; // Random Y position
		Obstacle newObstacle = new Obstacle(x, y, 10);
		items.add(newObstacle); // Ensure obstacle is added to the items ArrayList
	}

	// Adds a new light at a random position in the arena
	public void addLight() {
		double x = Math.random() * xMax; // Random X position
		double y = Math.random() * yMax; // Random Y position
		Light newLight = new Light(x, y, 10);
		items.add(newLight); // Ensure light is added to the items ArrayList
	}

	// Adds a new whisker at a random position in the arena
	public void addWhisker() {
		double x = Math.random() * xMax; // Random X position
		double y = Math.random() * yMax; // Random Y position
		Whisker newWhisker = new Whisker(x, y, 10, 45, 1, this);
		items.add(newWhisker); // Ensure whisker is added to the items ArrayList
	}

	// Checks if a position is free for movement based on collisions with items
	public boolean canMoveHere(double x, double y, double rad) {
		for (ArenaItem i : items) {
			if (i.hitting(x, y, rad)) {
				return false; // Position is occupied
			}
		}
		return true; // Position is free
	}

	// Toggles the blackout state of the arena
	public void blackOut() {
		isBlackOut = !isBlackOut; // Switch blackout state
	}
}