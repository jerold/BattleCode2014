package attacker;

import battlecode.common.*;
import java.util.Random;

public class Scout
{
	int[][] travelled;
	RobotController rc;
	
	public void run(RobotController rc)
	{
		this.rc = rc;
		travelled = new int[rc.getMapWidth()][rc.getMapHeight()];
		
		resetTravelled();
		addTravelled(rc.getLocation());
		
		while(true)
		{
			MapLocation next = findNextLocation();
			Utilities.MoveMapLocation(rc, next, true);
			addTravelled(next);
		}
	}
	
	private void resetTravelled()
	{
		for(int k = 0; k < rc.getMapWidth(); k++)
		{
			for(int a = 0; a < rc.getMapHeight(); a++)
			{
				travelled[k][a] = 0;
			}
		}
	}
	
	private void addTravelled(MapLocation loc)
	{
		int x = loc.x;
		int y = loc.y;
		int width = 1;
		int height = -4;
		for(int k = 0; k < 9; k++)
		{
			for(int a = 0; a < width; a++)
			{
				int xTemp = x + (a - width / 2);
				int yTemp = y + height;
				if(xTemp > 0 && xTemp < rc.getMapWidth() && yTemp > 0 && yTemp < rc.getMapHeight())
				{
					travelled[xTemp][yTemp] = 1;
				}
			}
			
			height++;
            if(height > 0)
            {
                width -= 2;
            }
            else
            {
                width += 2;
            }
		}
	}

	private MapLocation findNextLocation()
	{
		Random rand = new Random();
		
		MapLocation loc = new MapLocation(rand.nextInt(rc.getMapWidth()), rand.nextInt(rc.getMapHeight()));
		int k = 0;
		
		while(rc.senseTerrainTile(loc) == TerrainTile.VOID || travelled[loc.x][loc.y] == 1)
		{
			loc = new MapLocation(rand.nextInt(rc.getMapWidth()), rand.nextInt(rc.getMapHeight()));
			k++;
			if(k > rc.getMapHeight() * rc.getMapWidth())
			{
				resetTravelled();
			}
		}
		
		return loc;
	}
}
