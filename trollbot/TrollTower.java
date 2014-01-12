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
		target = pastr;
		int width = rc.getMapWidth();
		int height = rc.getMapHeight();
		int dist = 350;
		
		if(pastr.x < width / 2)
		{
			while(target.distanceSquaredTo(pastr) < dist && target.x > 0)
			{
				target = target.add(Direction.WEST);
			}
		}
		else
		{
			while(target.distanceSquaredTo(pastr) < dist && target.x < rc.getMapWidth() - 1)
			{
				target = target.add(Direction.EAST);
			}
		}
		if(pastr.y < height / 2)
		{
			while(target.distanceSquaredTo(pastr) < dist && target.y < height - 1)
			{
				target = target.add(Direction.SOUTH);
			}
		}
		else
		{
			while(target.distanceSquaredTo(pastr) < dist && target.y > 0)
			{
				target = target.add(Direction.NORTH);
			}
		}
		
		while(rc.senseTerrainTile(target) == TerrainTile.VOID && target.x < width / 2)
		{
			target = target.add(Direction.EAST);
		}
		while(rc.senseTerrainTile(target) == TerrainTile.VOID && target.x > width / 2)
		{
			target = target.add(Direction.WEST);
		}
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
			else if(rc.getType() == RobotType.NOISETOWER)
			{
				try
				{
					if(rc.isActive())
					{
						rc.attackSquare(pastr);
					}
				}
				catch(Exception e){}
			}
			rc.yield();
		}
	}
}
