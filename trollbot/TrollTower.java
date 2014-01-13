package trollbot;

import battlecode.common.*;

public class TrollTower
{
	RobotController rc;
	MapLocation target, pastr;
	
	public TrollTower(RobotController rc, MapLocation pastr)
	{
		this.rc = rc;
		this.pastr = pastr;
		target = Utilities.spotOfTrollTower(rc, pastr);
	}
	
	public void run()
	{
		while(true)
		{
			if(rc.getType() == RobotType.SOLDIER)
			{
				rc.setIndicatorString(0, "(" + target.x + ", " + target.y + ")");
				rc.setIndicatorString(1, "(" + rc.getMapWidth() + ", " + rc.getMapHeight() + ")");
				if(rc.isActive())
				{
					Utilities.MoveMapLocation(rc, target, false);
				}
				
				while(!rc.isActive()){}
				
				try
				{
					rc.setIndicatorString(2, "Constructing");
					rc.construct(RobotType.NOISETOWER);
				}
				catch(Exception e){}
			}
			rc.yield();
		}
	}
}
