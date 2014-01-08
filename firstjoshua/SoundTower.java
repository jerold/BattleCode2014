package firstjoshua;

import battlecode.common.*;

public class SoundTower
{
	RobotController rc;
	int corner, iteration;
	int width, height;
	MapLocation[] one, two, three, four;
	
	public SoundTower(RobotController rc)
	{
		this.rc = rc;
		corner = findBestCorner();
		width = rc.getMapWidth();
		height = rc.getMapHeight();
		MapLocation[] one = {new MapLocation(0, 13), new MapLocation(4, 13), new MapLocation(10, 13),
							 new MapLocation(13, 10), new MapLocation(13, 4), new MapLocation(13, 0),
							 new MapLocation(0, 10), new MapLocation(5, 10), new MapLocation(10, 10), 
							 new MapLocation(10, 5), new MapLocation(10, 0), new MapLocation(2, 8),
							 new MapLocation(8, 8), new MapLocation(8, 2)};
		MapLocation[] two = {new MapLocation(width - 1, 13), new MapLocation(width - 5, 13), new MapLocation(width - 11, 13),
				 			 new MapLocation(width - 14, 10), new MapLocation(width - 14, 4), new MapLocation(width - 14, 0),
				 			 new MapLocation(width - 1, 10), new MapLocation(width - 6, 10), new MapLocation(width - 11, 10), 
				 			 new MapLocation(width - 11, 5), new MapLocation(width - 11, 0), new MapLocation(width - 3, 8),
				 			 new MapLocation(width - 9, 8), new MapLocation(width - 9, 2)};
		MapLocation[] three = {new MapLocation(0, height - 14), new MapLocation(4, height - 14), new MapLocation(10, height - 14),
				 			   new MapLocation(13, height - 11), new MapLocation(13, height - 5), new MapLocation(13, height - 1),
				 			   new MapLocation(0, height - 11), new MapLocation(5, height - 11), new MapLocation(10, height - 11), 
				 			   new MapLocation(10, height - 6), new MapLocation(10, height - 1), new MapLocation(2, height - 9),
				 			   new MapLocation(8, height - 9), new MapLocation(8, height - 3)};
		MapLocation[] four = {new MapLocation(width - 1, height - 14), new MapLocation(width - 5, height - 14), new MapLocation(width - 11, height - 14),
							  new MapLocation(width - 14, height - 11), new MapLocation(width - 14, height - 5), new MapLocation(width - 14, height - 1),
							  new MapLocation(width - 1, height - 11), new MapLocation(width - 6, height - 11), new MapLocation(width - 11, height - 11), 
							  new MapLocation(width - 11, height - 6), new MapLocation(width - 11, height - 1), new MapLocation(width - 3, height - 9),
							  new MapLocation(width - 9, height - 9), new MapLocation(width - 9, height - 3)};
		this.one = one;
		this.two = two;
		this.three = three;
		this.four = four;
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
							Utilities.MoveMapLocation(rc, new MapLocation(5, 5), true);
							break;
						case 2:
							Utilities.MoveMapLocation(rc, new MapLocation(rc.getMapWidth() - 6, 5), true);
							break;
						case 3:
							Utilities.MoveMapLocation(rc, new MapLocation(5, rc.getMapHeight() - 6), true);
							break;
						default:
							Utilities.MoveMapLocation(rc, new MapLocation(rc.getMapWidth() - 6, rc.getMapHeight() - 6), true);
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
					if(rc.isActive())
					{
						switch(corner)
						{
							case 1:
								rc.attackSquare(one[iteration]);
								iteration++;
								iteration %= one.length;
								break;
							case 2:
								rc.attackSquare(two[iteration]);
								iteration++;
								iteration %= two.length;
								break;
							case 3:
								rc.attackSquare(three[iteration]);
								iteration++;
								iteration %= three.length;
								break;
							default:
								rc.attackSquare(four[iteration]);
								iteration++;
								iteration %= four.length;
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
		
		return corner;
	}
}
