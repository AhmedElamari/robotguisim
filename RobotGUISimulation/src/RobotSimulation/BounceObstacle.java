package RobotSimulation;

import java.util.Random;

import javafx.scene.paint.Color;

/**
 * A specialized Obstacle that: 1) Deflects robots in a random direction upon
 * collision (calculateBounceDirection). 2) Changes its size (radius) each time
 * it's hit. 3) Cycles through different colors on every bounce. 4) Increments a
 * robot's score when it collides (if Robot has addScore(...) support). 5)
 * Tracks consecutive "chain" bounces happening in quick succession, triggering
 * extra effects. 6) Can "break" or deactivate after a certain number of hits
 * (lifecycle).
 */
public class BounceObstacle extends Obstacle {

	// Array of colors that the obstacle cycles through after each hit
	private final Color[] colorCycle = { Color.PURPLE, Color.BLUEVIOLET, Color.MAGENTA, Color.ORCHID, Color.PINK,
			Color.PLUM, Color.HOTPINK };
	private int colorIndex = 0;

	// Track how many times this obstacle has been collided with
	private int hitCount = 0;
	private final int maxHits = 10; // The obstacle "breaks" after 10 hits

	// The obstacle can shrink or grow slightly upon collision
	private double sizeChangePerHit = -1.0; // Each hit shrinks the radius by 1

	// Whether the obstacle is still "active" (not broken)
	private boolean active = true;

	// For randomizing bounce directions
	private Random rand = new Random();

	// For "chain bounces": if multiple collisions occur within a short time,
	// we can do something special (like spawn a mini-obstacle).
	private long lastCollisionTime = 0;
	private final long chainThresholdMillis = 1500; // 1.5 seconds for a chain

	/**
	 * Construct a BounceObstacle with position (ix, iy) and radius ir.
	 */
	public BounceObstacle(double ix, double iy, double ir) {
		super(ix, iy, ir);
		// Default fallback character color (for text-based or older MyCanvas usage)
		col = 'p'; // 'p' stands for purple in ASCII mode, for instance
	}

	/**
	 * Draw this obstacle on the canvas. If it's "broken"/inactive, you could choose
	 * not to draw it or draw differently.
	 */
	@Override
	public void drawItem(MyCanvas mc) {
		if (!active) {
			// Obstacle is "broken". You could skip drawing or draw a faint outline.
			mc.showCircle(x, y, rad, 'g'); // Example: draw a grey circle
			return;
		}

		// If active, cycle color from colorCycle array
		Color fxColor = colorCycle[colorIndex];
		mc.showCircle(x, y, rad, fxColor);
	}

	/**
	 * Each animation step, the arena calls checkItem(...) to let this obstacle
	 * check for collisions with robots and respond accordingly.
	 */
	@Override
	public void checkItem(RobotArena arena) {
		// If it's broken or inactive, skip
		if (!active) {
			return;
		}

		// Check for collisions with each robot in the arena
		Robot[] allRobots = arena.getRobots();
		for (Robot robot : allRobots) {
			double dx = robot.getX() - x;
			double dy = robot.getY() - y;
			double dist = Math.sqrt(dx * dx + dy * dy);

			// If center-to-center distance < sum of radii => collision
			if (dist < robot.getRad() + this.rad) {
				// Handle collision
				handleCollision(arena, robot);
			}
		}
	}

	/**
	 * Handle collision with a single Robot: - Randomly deflect the robot - Award
	 * the robot some points - Shrink/grow this obstacle - Cycle color - Check chain
	 * collisions - "Break" after certain hits
	 */
	private void handleCollision(RobotArena arena, Robot robot) {
		// 1) Calculate random bounce direction for the robot
		calculateBounceDirection(robot);

		// 3) Shrink or grow the obstacle
		rad += sizeChangePerHit;
		// Make sure the radius never goes below some small minimum
		if (rad < 5)
			rad = 5;

		// 4) Cycle to next color
		cycleColor();

		// 5) Check if this is a "chain" bounce
		long now = System.currentTimeMillis();
		if (now - lastCollisionTime < chainThresholdMillis) {
			// Chain collision triggered! For example, spawn a small Obstacle:
			System.out.println("Chain collision! Spawning mini-obstacle...");
			double spawnX = x + rand.nextInt(40) - 20;
			double spawnY = y + rand.nextInt(40) - 20;
			arena.items.add(new Obstacle(spawnX, spawnY, 5));
		}
		lastCollisionTime = now;

		// 6) Track hits, check if we exceed maxHits
		hitCount++;
		if (hitCount >= maxHits) {
			breakObstacle(arena);
		}
	}

	/**
	 * "Break" => spawn some mini obstacles, remove self.
	 */
	private void breakObstacle(RobotArena arena) {
		active = false;
		System.out.println("BounceObstacle has broken after " + hitCount + " hits!");

		// spawn N mini obstacles that are each "absorbable"
		int miniCount = 3; // for example
		for (int i = 0; i < miniCount; i++) {
			double spawnX = x + rand.nextInt(30) - 15;
			double spawnY = y + rand.nextInt(30) - 15;
			miniObstacleXP mini = new miniObstacleXP(spawnX, spawnY, 5);
			arena.items.add(mini);
		}

		// optionally remove self from arena to never draw again
		// or keep it 'broken' visually
		// here let's just remove it from the main items list
		arena.items.remove(this);
	}

	/**
	 * Randomly changes the robot's angle. You can do more sophisticated physics if
	 * desired.
	 */
	private void calculateBounceDirection(Robot robot) {

		// New random angle
		double randomAngle = rand.nextDouble() * 360.0;

		// Simplest approach: just set a brand-new random angle
		robot.setAngle(randomAngle);

		// Optionally slow or speed up the robot as a side effect:
		// e.g. robot.setSpeed(robot.getSpeed() * 0.8);
	}

	/**
	 * Cycles to the next color in the colorCycle array.
	 */
	private void cycleColor() {
		colorIndex = (colorIndex + 1) % colorCycle.length;
	}
}
