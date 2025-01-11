package RobotSimulation;

public abstract class ArenaItem {
	protected double x, y, rad; // position and radius
	protected char col; // color
	static int itemCounter = 0;
	protected int itemID;

	/**
	 * Constructor for ArenaItem
	 * 
	 * @param d
	 * @param e
	 * @param f
	 * @param c
	 */
	public ArenaItem(double d, double e, double f) {
		x = d;
		y = e;
		rad = f;
		itemID = itemCounter++;
		col = 'r';
	}

	/**
	 * Draw the item in the canvas
	 * 
	 * @param mc canvas to draw on
	 */
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getRad() {
		return rad;
	}

	protected void setXY(double x2, double y2) {
		x = x2;
		y = y2;
	}

	public int getID() {
		return itemID;
	}

	public abstract void drawItem(MyCanvas mc);

	protected String getStrType() {
		return "Item";
	}

	public abstract void checkItem(RobotArena r);

	public abstract void adjustItem();

	public String toString() {
		return getStrType() + " at " + Math.round(x) + ", " + Math.round(y);
	}

	public boolean hitting(double ox, double oy, double or) {
		return (Math.hypot(x - ox, y - oy) < (rad + or) * (rad + or)); // possible situation later on
	}

	public boolean hitting(ArenaItem iRobot) {
		return hitting(iRobot.getX(), iRobot.getY(), iRobot.getRad());

	}

	public double calcX(double s, double a) {
		return x + s * Math.cos(Math.toRadians(a));
	}

	public double calcY(double s, double a) {
		return y + s * Math.sin(Math.toRadians(a));
	}

	public abstract String fileString();

}
