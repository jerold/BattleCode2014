package distract;

import battlecode.common.*;

public class HQCircle
{
	RobotController rc;
	MapLocation target;
	public HQCircle(RobotController rc)
	{
		this.rc = rc;
		target = rc.senseEnemyHQLocation();
	}
	
	public void run()
	{
		Utilities.MoveMapLocation(rc, rc.senseEnemyHQLocation().add(Direction.SOUTH, 3), true);
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
