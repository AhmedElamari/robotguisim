package RobotSimulation;

import java.util.Random;

import javafx.scene.paint.Color;

/**
 * Represents a specialized obstacle in the robot simulation arena that:
 * <ul>
 * <li>Deflects robots in a random direction upon collision.</li>
 * <li>Changes size and cycles through colors with each collision.</li>
 * <li>Tracks consecutive chain bounces for additional effects.</li>
 * <li>Deactivates ("breaks") after a certain number of hits.</li>
 * </ul>
 * 
 * <p>
 * This obstacle enhances the simulation with dynamic and interactive behaviors,
 * making it more engaging.
 * </p>
 * 
 * @author Ahmed Elamari
 * @version 2.0
 */
public class BounceObstacle extends Obstacle {

	/** Array of colors the obstacle cycles through after each collision. */
	private final Color[] colorCycle = { Color.PURPLE, Color.BLUEVIOLET, Color.MAGENTA, Color.ORCHID, Color.PINK,
			Color.PLUM, Color.HOTPINK };

	/** Current index of the colorCycle array. */
	private int colorIndex = 0;

	/** Tracks the number of times this obstacle has been hit. */
	private int hitCount = 0;

	/** Maximum number of hits before the obstacle breaks. */
	private final int maxHits = 10;

	/** Amount by which the radius changes per hit (can shrink or grow). */
	private double sizeChangePerHit = -1.0;

	/** Flag to indicate whether the obstacle is still active. */
	private boolean active = true;

	/** Random generator for bounce direction and spawn locations. */
	private Random rand = new Random();

	/** Tracks the time of the last collision for chain bounce detection. */
	private long lastCollisionTime = 0;

	/** Time threshold (in milliseconds) for detecting chain collisions. */
	private final long chainThresholdMillis = 1500;

	/**
	 * Constructs a BounceObstacle with the specified position and radius.
	 * 
	 * @param ix The x-coordinate of the obstacle.
	 * @param iy The y-coordinate of the obstacle.
	 * @param ir The radius of the obstacle.
	 */
	public BounceObstacle(double ix, double iy, double ir) {
		super(ix, iy, ir);
		col = 'p'; // Default color for text-based or older rendering methods.
	}

	/**
	 * Draws the obstacle on the canvas. Displays differently if the obstacle is
	 * broken.
	 * 
	 * @param mc The canvas used for drawing the obstacle.
	 */
	@Override
	public void drawItem(MyCanvas mc) {
		if (!active) {
			// Draw a grey circle to represent a broken obstacle.
			mc.showCircle(x, y, rad, 'g');
			return;
		}

		// Draw the active obstacle with the current color from the color cycle.
		Color fxColor = colorCycle[colorIndex];
		mc.showCircle(x, y, rad, fxColor);
	}

	/**
	 * Checks for collisions with robots in the arena and responds accordingly.
	 * 
	 * @param arena The arena containing the obstacle and robots.
	 */
	@Override
	public void checkItem(RobotArena arena) {
		if (!active) {
			// Skip processing if the obstacle is broken.
			return;
		}

		// Check for collisions with each robot in the arena.
		for (Robot robot : arena.getRobots()) {
			double dx = robot.getX() - x;
			double dy = robot.getY() - y;
			double dist = Math.sqrt(dx * dx + dy * dy);

			if (dist < robot.getRad() + this.rad) {
				// Handle collision when a robot is within range.
				handleCollision(arena, robot);
			}
		}
	}

	/**
	 * Handles the collision with a robot, applying random deflection, size change,
	 * color cycling, chain detection, and breaking behavior.
	 * 
	 * @param arena The arena containing the robot and obstacle.
	 * @param robot The robot colliding with this obstacle.
	 */
	private void handleCollision(RobotArena arena, Robot robot) {
		// Deflect the robot in a random direction.
		calculateBounceDirection(robot);

		// Adjust the size of the obstacle.
		rad += sizeChangePerHit;
		if (rad < 5) {
			rad = 5; // Ensure the radius does not go below a minimum value.
		}

		// Cycle to the next color.
		cycleColor();

		// Check for chain collisions and trigger additional effects if detected.
		long now = System.currentTimeMillis();
		if (now - lastCollisionTime < chainThresholdMillis) {
			System.out.println("Chain collision! Spawning mini-obstacle...");
			double spawnX = x + rand.nextInt(40) - 20;
			double spawnY = y + rand.nextInt(40) - 20;
			arena.items.add(new Obstacle(spawnX, spawnY, 5));
		}
		lastCollisionTime = now;

		// Increment hit count and check if the obstacle should break.
		hitCount++;
		if (hitCount >= maxHits) {
			breakObstacle(arena);
		}
	}

	/**
	 * Breaks the obstacle, spawns mini-obstacles, and deactivates itself.
	 * 
	 * @param arena The arena where the obstacle resides.
	 */
	private void breakObstacle(RobotArena arena) {
		active = false;
		System.out.println("BounceObstacle has broken after " + hitCount + " hits!");

		// Spawn several mini-obstacles as a result of breaking.
		int miniCount = 3;
		for (int i = 0; i < miniCount; i++) {
			double spawnX = x + rand.nextInt(30) - 15;
			double spawnY = y + rand.nextInt(30) - 15;
			miniObstacleXP mini = new miniObstacleXP(spawnX, spawnY, 5);
			arena.items.add(mini);
		}

		// Remove the obstacle from the arena's item list.
		arena.items.remove(this);
	}

	/**
	 * Deflects the robot in a random direction by altering its angle.
	 * 
	 * @param robot The robot to deflect.
	 */
	private void calculateBounceDirection(Robot robot) {
		double randomAngle = rand.nextDouble() * 360.0;
		robot.setAngle(randomAngle);
	}

	/**
	 * Cycles to the next color in the color cycle array.
	 */
	private void cycleColor() {
		colorIndex = (colorIndex + 1) % colorCycle.length;
	}
}
