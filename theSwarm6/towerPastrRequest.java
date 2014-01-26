package theSwarm6;

import battlecode.common.*;

/*
 * firstLoc = start
 * firstRequestTower = start + 1
 * firstRequestPastr = start + 2
 * ...
 * 
 * locs hold the location of the spot in question
 * requests have numbers for whether they are pending, or needed
 * for towers, 0 means do nothing, 1 means a tower is needed, and 2 means a tower is coming
 * for pastures, 0 means do nothing, 1 means the pasture should set right up when it arrives, 
 * 	   2 means it should wait for a friendly tower to set up 3 means there is one coming, 
 *     and anything else is how many turns to wait before constructing after a tower is there
 */
public class towerPastrRequest
{
	public final int start = 60000;
	public final int numSpots = 3;
	
	private MapLocation[] locs;
	private RobotController rc;
	
	public towerPastrRequest(RobotController rc)
	{
		try
		{
			this.rc = rc;
			
			if(rc.readBroadcast(start) == 0)
			{
				MapLocation[] locs = TowerUtil.findBestSpots(rc, numSpots);
				this.locs = locs;
				for(int k = 0; k < numSpots; k++)
				{
					rc.broadcast(start + k * 3, TowerUtil.convertMapLocationToInt(locs[k]));
				}
			}
			else
			{
				locs = new MapLocation[numSpots];
				for(int k = 0; k < numSpots; k++)
				{
					locs[k] = TowerUtil.convertIntToMapLocation(rc.readBroadcast(start + (k * 3)));
				}
			}
		}
		catch(Exception e){}
	}
	
	public MapLocation checkForNeed()
	{
		for(int k = 0; k < numSpots; k++)
		{
			try
			{
				int tower = rc.readBroadcast(start + (k * 3) + 1);
				int pastr = rc.readBroadcast(start + (k * 3) + 2);
				
				if(tower == 1)
				{
					rc.broadcast(start + (k * 3) + 1, 2);
					Direction[] dirs = Direction.values();
					Direction choice = Direction.NONE;
					for(Direction dir : dirs)
					{
						if(rc.senseTerrainTile(locs[k].add(dir)) != TerrainTile.VOID)
						{
							choice = dir;
							break;
						}
					}
					return locs[k].add(choice);
				}
				if(pastr == 1 || pastr == 2 || pastr > 3)
				{
					rc.broadcast(start + (k * 3) + 2, 3);
					return locs[k];
				}
			}
			catch(Exception e){}
		}
		
		return null;
	}
	
	public void sendRequest()
	{
		try
		{
			for(int k = 0; k < numSpots; k++)
			{
				if(locs[k].distanceSquaredTo(rc.getLocation()) <= 16)
				{
					if(rc.getType() == RobotType.PASTR)
					{
						rc.broadcast(start + (k * 3) + 1, 1);
					}
					else
					{
						rc.broadcast(start + (k * 3) + 2, 2);
					}
				}
			}
		}
		catch(Exception e){}
	}
	
	public boolean isPending()
	{
		try
		{
			for(int k = 0; k < numSpots; k++)
			{
				if(locs[k].distanceSquaredTo(rc.getLocation()) <= 16)
				{
					if(rc.getType() == RobotType.PASTR)
					{
						if(rc.readBroadcast(start + (k * 3) + 1) == 2)
						{
							return true;
						}
						else
						{
							return false;
						}
					}
					else
					{
						if(rc.readBroadcast(start + (k * 3) + 2) == 3)
						{
							return true;
						}
						else
						{
							return false;
						}
					}
				}
			}
		}
		catch(Exception e){}
		
		return false;
	}
	
	public void abandonPath(MapLocation target)
	{
		try
		{
			for(int k = 0; k < numSpots; k++)
			{
				if(locs[k].distanceSquaredTo(target) <= 16)
				{
					if(rc.getType() == RobotType.PASTR)
					{
						rc.broadcast(start + (k * 3) + 2, 2);
					}
					else
					{
						rc.broadcast(start + (k * 3) + 1, 1);
					}
				}
			}
		}
		catch(Exception e){}
	}
}
