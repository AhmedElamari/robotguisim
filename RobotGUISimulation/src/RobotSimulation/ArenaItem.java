package RobotSimulation;

/**
 * The <code>ArenaItem</code> class is an abstract base class for all items
 * within the simulation arena.
 *
 * <p>
 * Each <code>ArenaItem</code> maintains a position (<code>x, y</code>), a
 * radius (<code>rad</code>), a colour (<code>col</code>), and a unique item ID.
 * Subclasses must implement how the item is drawn, checked for interactions,
 * and adjusted over time.
 * </p>
 *
 * <p>
 * Examples of subclasses include <code>Robot</code>, <code>Obstacle</code>, and
 * <code>Light</code>, each of which extends and customizes this abstract class
 * for its specific behavior.
 * </p>
 *
 * @author Ahmed Elamari
 * @version 1.0
 */
public abstract class ArenaItem {

	/** The X coordinate of this item's position. */
	protected double x;
	/** The Y coordinate of this item's position. */
	protected double y;
	/** The radius of this item, used for drawing and collision detection. */
	protected double rad;
	/** A character representing the colour of this item (e.g., 'r' for red). */
	protected char col;

	/**
	 * A static counter used to assign unique IDs to each <code>ArenaItem</code>
	 * created.
	 */
	static int itemCounter = 0;

	/** The unique ID of this <code>ArenaItem</code>. */
	protected int itemID;

	/**
	 * Constructs a new <code>ArenaItem</code> at the given coordinates with the
	 * specified radius.
	 *
	 * @param d the initial X coordinate
	 * @param e the initial Y coordinate
	 * @param f the initial radius of the item
	 */
	public ArenaItem(double d, double e, double f) {
		x = d;
		y = e;
		rad = f;
		itemID = itemCounter++;
		// Default colour set to 'r' (red). Subclasses can override as needed.
		col = 'r';
	}

	/**
	 * Returns the current X coordinate of this <code>ArenaItem</code>.
	 *
	 * @return the current X coordinate
	 */
	public double getX() {
		return x;
	}

	/**
	 * Returns the current Y coordinate of this <code>ArenaItem</code>.
	 *
	 * @return the current Y coordinate
	 */
	public double getY() {
		return y;
	}

	/**
	 * Returns the current radius of this <code>ArenaItem</code>.
	 *
	 * @return the current radius
	 */
	public double getRad() {
		return rad;
	}

	/**
	 * Sets the position of this <code>ArenaItem</code> to the given coordinates.
	 *
	 * @param x2 the new X coordinate
	 * @param y2 the new Y coordinate
	 */
	protected void setXY(double x2, double y2) {
		x = x2;
		y = y2;
	}

	/**
	 * Returns the unique ID of this <code>ArenaItem</code>.
	 *
	 * @return the item's unique ID
	 */
	public int getID() {
		return itemID;
	}

	/**
	 * Draws this <code>ArenaItem</code> on the provided <code>MyCanvas</code>.
	 * Subclasses must implement the specific drawing logic.
	 *
	 * @param mc the <code>MyCanvas</code> used for rendering this item
	 */
	public abstract void drawItem(MyCanvas mc);

	/**
	 * Returns a string representation of this item's type. Subclasses can override
	 * to return a more descriptive type string.
	 *
	 * @return a short string describing the item type
	 */
	protected String getStrType() {
		return "Item";
	}

	/**
	 * Checks this <code>ArenaItem</code> against any relevant arena conditions,
	 * such as collisions or interactions. Subclasses implement their own logic.
	 *
	 * @param r the <code>RobotArena</code> that manages and contains this item
	 */
	public abstract void checkItem(RobotArena r);

	/**
	 * Adjusts this <code>ArenaItem</code> over time, typically called once per
	 * simulation step (e.g., to move or change behavior). Subclasses implement
	 * their own logic.
	 */
	public abstract void adjustItem();

	/**
	 * Returns a string describing this item's type and approximate position.
	 *
	 * @return a <code>String</code> with the item type and position
	 */
	@Override
	public String toString() {
		// Round the coordinates for a brief summary
		return getStrType() + " at " + Math.round(x) + ", " + Math.round(y);
	}

	/**
	 * Determines whether this item is colliding with a circle specified by its
	 * center (<code>ox, oy</code>) and radius (<code>or</code>).
	 *
	 * @param ox the X coordinate of the other circle
	 * @param oy the Y coordinate of the other circle
	 * @param or the radius of the other circle
	 * @return <code>true</code> if the circles overlap, <code>false</code>
	 *         otherwise
	 */
	public boolean hitting(double ox, double oy, double or) {
		// Compare the distance between centers to the sum of the radii
		// Note: The multiplication in (rad + or) * (rad + or) might be a placeholder
		// for
		// an alternative approach. Typically, you'd compare distance < (rad + or).
		return Math.hypot(x - ox, y - oy) < (rad + or) * (rad + or);
	}

	/**
	 * Determines whether this item is colliding with another
	 * <code>ArenaItem</code>.
	 *
	 * @param iRobot another <code>ArenaItem</code> (often a Robot)
	 * @return <code>true</code> if the two items overlap, <code>false</code>
	 *         otherwise
	 */
	public boolean hitting(ArenaItem iRobot) {
		return hitting(iRobot.getX(), iRobot.getY(), iRobot.getRad());
	}

	/**
	 * Calculates an X coordinate offset by distance <code>s</code> at angle
	 * <code>a</code> (in degrees) relative to this item's current position
	 * (<code>x, y</code>).
	 *
	 * @param s the distance offset from this item's center
	 * @param a the angle in degrees from this item's center
	 * @return the computed X coordinate
	 */
	public double calcX(double s, double a) {
		return x + s * Math.cos(Math.toRadians(a));
	}

	/**
	 * Calculates a Y coordinate offset by distance <code>s</code> at angle
	 * <code>a</code> (in degrees) relative to this item's current position
	 * (<code>x, y</code>).
	 *
	 * @param s the distance offset from this item's center
	 * @param a the angle in degrees from this item's center
	 * @return the computed Y coordinate
	 */
	public double calcY(double s, double a) {
		return y + s * Math.sin(Math.toRadians(a));
	}

	/**
	 * Returns a formatted string representing this item's state, typically used for
	 * file output. Subclasses must implement their specific format.
	 *
	 * @return a <code>String</code> containing the main properties of this item
	 */
	public abstract String fileString();
}
