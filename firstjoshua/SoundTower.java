package firstjoshua;

import battlecode.common.*;

public class SoundTower
{
	RobotController rc;
	int width, height, corner;
	int[] radii;
	
	public SoundTower(RobotController rc)
	{
		this.rc = rc;
		corner = findBestCorner();
		width = rc.getMapWidth();
		height = rc.getMapHeight();
		int[] radii = {15, 13, 11, 9, 7};
		this.radii = radii;
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
				fireArcs();
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
	
	private void fireArcs()
	{
		for(int k = 0; k < radii.length; k++)
		{
			for(int a = 0; a <= radii[k]; a+= 4)
			{
				while(!rc.isActive()){}
				try
				{
					switch(corner)
					{
						case 1:
							rc.attackSquare(new MapLocation(a, radii[k]));
							break;
						case 2:
							rc.attackSquare(new MapLocation(width - a + 1, radii[k]));
							break;
						case 3:
							rc.attackSquare(new MapLocation(a, height - radii[k] + 1));
							break;
						default:
							rc.attackSquare(new MapLocation(width - a + 1, height - radii[k] + 1));
							break;
					}
				}
				catch(Exception e){}
				
				rc.yield();
				
				while(!rc.isActive()){}
				try
				{
					switch(corner)
					{
						case 1:
							rc.attackSquare(new MapLocation(radii[k], a));
							break;
						case 2:
							rc.attackSquare(new MapLocation(width - radii[k] + 1, a));
							break;
						case 3:
							rc.attackSquare(new MapLocation(radii[k], height - a + 1));
							break;
						default:
							rc.attackSquare(new MapLocation(width - radii[k] + 1, height - a + 1));
							break;
					}
				}
				catch(Exception e){}
				
				rc.yield();
			}
			
			while(!rc.isActive()){}
			try
			{
				switch(corner)
				{
					case 1:
						rc.attackSquare(new MapLocation(radii[k], radii[k]));
						break;
					case 2:
						rc.attackSquare(new MapLocation(width - radii[k] + 1, radii[k]));
						break;
					case 3:
						rc.attackSquare(new MapLocation(radii[k], height - radii[k] + 1));
						break;
					default:
						rc.attackSquare(new MapLocation(width - radii[k] + 1, height - radii[k] + 1));
						break;
				}
			}
			catch(Exception e){}
			
			rc.yield();
		}
	}
}
