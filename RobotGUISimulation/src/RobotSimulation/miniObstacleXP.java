package RobotSimulation;

public class miniObstacleXP extends Obstacle {

	// Whether it's still active
	private boolean active = true;

	public miniObstacleXP(double x, double y, double r) {
		super(x, y, r);
		col = 'g'; // 'o' for orange, or whatever color you want
	}

	@Override
	public void checkItem(RobotArena arena) {
		if (!active)
			return;

		// For each robot, see if there's a collision
		for (Robot robot : arena.getRobots()) {
			double dx = robot.getX() - x;
			double dy = robot.getY() - y;
			double dist = Math.sqrt(dx * dx + dy * dy);

			// If collision with the robot => "absorb"
			if (dist < robot.getRad() + this.rad) {
				// 1) remove self from arena
				arena.items.remove(this);
				active = false;

				// 2) give 1 point
				arena.addScore(1);

				System.out.println("MiniObstacle absorbed for +1 point!");
				// Stop checking once removed
				return;
			}
		}
	}
}
