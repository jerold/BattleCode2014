package firstjoshua;

import battlecode.common.*;

public class SoundTower2
{
	RobotController rc;
	int corner, iteration;
	int width, height;
	MapLocation[] one, two, three, four;
	
	public SoundTower2(RobotController rc)
	{
		this.rc = rc;
		corner = findBestCorner();
		width = rc.getMapWidth();
		height = rc.getMapHeight();
		MapLocation[] one = {new MapLocation(0, 0), new MapLocation(2, 0), new MapLocation(0, 2),
							 new MapLocation(0, 13), new MapLocation(6, 13), new MapLocation(13, 13),
							 new MapLocation(13, 6), new MapLocation(13, 0), new MapLocation(7, 9),
							 new MapLocation(9, 7)};
		this.one = one;
		//this.two = two;
		//this.three = three;
		//this.four = four;
	}
	
	public void run()
	{
		while(true)
		{
			if(rc.getType() == RobotType.SOLDIER)
			{
				try
				{
					switch(corner)
					{
						case 1:
							Utilities.MoveMapLocation(rc, new MapLocation(4, 4), true);
							break;
						case 2:
							Utilities.MoveMapLocation(rc, new MapLocation(rc.getMapWidth() - 5, 4), true);
							break;
						case 3:
							Utilities.MoveMapLocation(rc, new MapLocation(4, rc.getMapHeight() - 5), true);
							break;
						default:
							Utilities.MoveMapLocation(rc, new MapLocation(rc.getMapWidth() - 5, rc.getMapHeight() - 5), true);
							break;
					}
					
					if(rc.isActive())
					{
						rc.construct(RobotType.NOISETOWER);
					}
				}
				catch(Exception e){}
			}
			else
			{
				try
				{
					rc.setIndicatorString(0, "Inactive");
					if(rc.isActive())
					{
						rc.setIndicatorString(0, "Active");
						switch(corner)
						{
							case 1:
								rc.attackSquare(one[iteration]);
								iteration++;
								iteration %= 10;
								break;
							case 2:
								rc.attackSquare(two[iteration]);
								iteration++;
								iteration %= 8;
								break;
							case 3:
								rc.attackSquare(three[iteration]);
								iteration++;
								iteration %= 8;
								break;
							default:
								rc.attackSquare(four[iteration]);
								iteration++;
								iteration %= 8;
								break;
						}
					}
				}
				catch(Exception e){}
			}
			
			rc.yield();
		}
	}
	
	//finds best corner to collect milk where the return is an int as follows:
	//1  2
	//3  4
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
		
		return 1;
	}
}
