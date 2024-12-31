package RobotSimulation;

public class Obstacle extends ArenaItem {


public Obstacle(double ix, double iy, double ir) {
	super(ix, iy, ir);
	col = 'o';
}

	//add code so user can command add an obstacle
public void addObstacle(double x, double y, double rad, char col) {
        Obstacle o = new Obstacle(x, y, rad);
            }
	
	
	@Override
	public void drawItem(MyCanvas mc) {
		// TODO Auto-generated method stub
		mc.showCircle(x, y, rad, col);
	}

	
	@Override
	public void checkItem(RobotArena r) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void adjustItem() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setXY(double x2, double y2) {
		// TODO Auto-generated method stub
		
	}

}
