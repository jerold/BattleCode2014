package distract;

import battlecode.common.*;

public class TowerCircle
{
	RobotController rc;
	MapLocation target;
	public TowerCircle(RobotController rc)
	{
		this.rc = rc;
		int corner = Utilities.findBestCorner(rc);
		switch(corner)
		{
			case 1:
				target = new MapLocation(5, 7);
				break;
			case 2:
				target = new MapLocation(rc.getMapWidth() - 6, 7);
				break;
			case 3:
				target = new MapLocation(5, rc.getMapHeight() - 8);
				break;
			default:
				target = new MapLocation(rc.getMapWidth() - 6, rc.getMapHeight() - 8);
				break;
		}
	}
	
	public void run()
	{
		Utilities.MoveMapLocation(rc, target.add(Direction.SOUTH, 2), true);
		while(true)
		{
			try
			{
				Robot[] enemies = rc.senseNearbyGameObjects(Robot.class,10,rc.getTeam().opponent());
                if (enemies.length > 0)
                {
                    Utilities.fire(rc);
                }
                else
                {
					Direction dir = rc.getLocation().directionTo(target);
					dir = dir.rotateLeft().rotateLeft();
					Utilities.MoveDirection(rc, dir, false);
                }
				rc.yield();
			}
			catch(Exception e){}
		}
	}
}
