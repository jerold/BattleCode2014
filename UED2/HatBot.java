package UED2;

import battlecode.common.*;

public class HatBot
{
	RobotController rc;
	
	public HatBot(RobotController rc)
	{
		this.rc = rc;
	}
	
	public void run()
	{
		while(!rc.isActive()){rc.yield();}
		try
		{
			rc.wearHat();
		}
		catch (Exception e){}
		Utilities.MoveMapLocation(rc, new MapLocation(rc.getMapWidth() / 2, rc.getMapHeight() / 2), false);
	}
}
