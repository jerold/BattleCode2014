package firstjoshua;

import battlecode.common.*;

public class PASTR
{
	RobotController rc;
	int corner;
	public PASTR(RobotController rc)
	{
		this.rc = rc;
		corner = findBestCorner();
	}
	
	public void run()
	{
		while(true)
		{
			if(rc.getType() == RobotType.SOLDIER)
			{
				switch(corner)
				{
					case 1:
						Utilities.MoveMapLocation(rc, new MapLocation(2, 2), true);
						break;
					case 2:
						Utilities.MoveMapLocation(rc, new MapLocation(rc.getMapWidth() - 3, 2), true);
						break;
					case 3:
						Utilities.MoveMapLocation(rc, new MapLocation(2, rc.getMapHeight() - 3), true);
						break;
					default:
						Utilities.MoveMapLocation(rc, new MapLocation(rc.getMapWidth() - 3, rc.getMapHeight() - 3), true);
						break;
				}
				
				if(rc.isActive())
				{
					try
					{
						rc.construct(RobotType.PASTR);
					}
					catch (Exception e){}
				}
			}
		}
	}
	
	private int findBestCorner()
	{
		double[][] pasture = rc.senseCowGrowth();
		
		double max = 0;
		int corner = 0;
		double total = 0;
		
		//top left corner
		for(int k = 0; k < 10; k++)
		{
			for(int a = 0; a < 10; a++)
			{
				total += pasture[k][a];
			}
		}
		if(total > max)
		{
			max = total;
			corner = 1;
		}
		total = 0;
		
		//top right corner
		for(int k = rc.getMapWidth() - 11; k < rc.getMapWidth(); k++)
		{
			for(int a = 0; a < 10; a++)
			{
				total += pasture[k][a];
			}
		}
		if(total > max)
		{
			max = total;
			corner = 2;
		}
		total = 0;
		
		//bottom left corner
		for(int k = 0; k < 10; k++)
		{
			for(int a = rc.getMapHeight() - 11; a < rc.getMapHeight(); a++)
			{
				total += pasture[k][a];
			}
		}
		if(total > max)
		{
			max = total;
			corner = 3;
		}
		total = 0;
		
		//bottom right corner
		for(int k = rc.getMapWidth() - 11; k < rc.getMapWidth(); k++)
		{
			for(int a = rc.getMapHeight() - 11; a < rc.getMapHeight(); a++)
			{
				total += pasture[k][a];
			}
		}
		if(total > max)
		{
			max = total;
			corner = 4;
		}
		
		return corner;
	}
}
