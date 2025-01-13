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
		items.add(new PredatorRobot(300, 300, 10, 45, 2, this));
		items.add(new Prey(200, 200, 10, 45, 2, this));
		items.add(new Prey(200, 200, 10, 45, 2, this));
		items.add(new Prey(200, 200, 10, 45, 2, this));

	}

	public String filestring() {
		StringBuilder sb = new StringBuilder();

		// First line: arena dimensions
		sb.append(xMax).append(" ").append(yMax).append("\n");

		// Add each item on a new line
		for (ArenaItem item : items) {
			// Each item's fileString() method must return a properly formatted string
			sb.append(item.fileString()).append("\n");
		}

		return sb.toString();
	}

	public RobotArena(String savedData) {
		items = new ArrayList<>();
		String[] lines = savedData.split("\n");

		if (lines.length > 0) {
			// Parse first line for arena dimensions
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

			// Parse remaining lines for items
			for (int i = 1; i < lines.length; i++) {
				String line = lines[i].trim();
				if (line.isEmpty())
					continue;

				String[] parts = line.split("\\s+");
				if (parts.length < 1)
					continue;

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
		// Create a list to store items that need to be removed
		ArrayList<ArenaItem> itemsToRemove = new ArrayList<>();

		// First pass: check items and mark for removal
		for (ArenaItem i : items) {
			i.checkItem(this);
			// If an item needs to be removed, add it to the removal list
			if (i instanceof Prey && ((Prey) i).isBeingEaten()) {
				itemsToRemove.add(i);
			}
		}

		// Second pass: remove marked items
		items.removeAll(itemsToRemove);
	}

	public void removePrey(ArenaItem prey) {
		if (prey instanceof Prey) {
			((Prey) prey).beingEaten();
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
