
package RobotSimulation;

public class Robot extends ArenaItem {
	RobotArena arena;
	double rAngle, rSpeed;

	public Robot(double ix, double iy, double ir, double ia, double is, RobotArena arena) {
		super(ix, iy, ir);
		this.arena = arena; // Initialize the arena field
		col = 'r';
		rAngle = ia;
		rSpeed = is;
	}

	@Override
	public void drawItem(MyCanvas mc) {
		mc.showCircle(x, y, rad, col);
		drawwheels(mc); // use drawwheels to draw wheels
	}

	public void drawwheels(MyCanvas mc) {
		double x1 = calcX(rad, rAngle + 90);
		double y1 = calcY(rad, rAngle + 90);
		double x2 = calcX(rad, rAngle - 90);
		double y2 = calcY(rad, rAngle - 90);

		mc.showCircle(x1, y1, rad / 4, 'l');
		mc.showCircle(x2, y2, rad / 4, 'l');
		Line l = new Line(x1, y1, x2, y2);
		l.drawLine(mc);
	}

	@Override
	public void checkItem(RobotArena r) {
		rAngle = r.CheckRobotAngle(x, y, rad, rAngle, itemID);
	}

	@Override
	public void adjustItem() {
		tryToMove(arena);
	}

	@Override
	protected String getStrType() {
		return "Robot";
	}

	public void tryToMove(RobotArena arena) {
		// Calculate tentative new position
		double nextX = x + Math.cos(Math.toRadians(rAngle)) * rSpeed;
		double nextY = y + Math.sin(Math.toRadians(rAngle)) * rSpeed;

		// Check if the robot can move to the new position
		if (canMoveHere(arena, nextX, nextY)) {
			// Move to the new position
			x = nextX;
			y = nextY;
		} else {
			// Change direction if the robot cannot move
			rAngle = (rAngle + 90 + (Math.random() * 180)) % 360; // Randomly adjust direction
		}
	}

	private boolean canMoveHere(RobotArena arena, double nextX, double nextY) {
		// Check for wall collisions
		if (nextX < rad || nextX > arena.getXSize() - rad || nextY < rad || nextY > arena.getYSize() - rad) {
			return false; // Collision with a wall
		}

		// Check for collisions with other robots
		for (ArenaItem item : arena.items) {
			if (item != this && item.hitting(nextX, nextY, rad)) {
				return false; // Collision with another robot or obstacle
			}
		}

		return true; // No collisions detected, can move
	}
}
