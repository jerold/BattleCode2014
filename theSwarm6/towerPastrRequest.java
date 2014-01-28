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
	public static final int start = 60000;
	public static final int numSpots = 3;
	
	private MapLocation[] locs;
	private RobotController rc;
	
	public towerPastrRequest(RobotController rc)
	{
		try
		{
			this.rc = rc;
			
			if(rc.readBroadcast(start) == 0)
			{
				rc.broadcast(start, -1);
				MapLocation[] locs = TowerUtil.findBestSpots(rc, numSpots);
				this.locs = locs;
				for(int k = 0; k < numSpots; k++)
				{
					rc.broadcast(start + k * 3, TowerUtil.convertMapLocationToInt(locs[k]));
				}
			}
			else
			{
				while(rc.readBroadcast(start) == -1){rc.yield();}
				locs = new MapLocation[numSpots];
				for(int k = 0; k < numSpots; k++)
				{
					locs[k] = TowerUtil.convertIntToMapLocation(rc.readBroadcast(start + (k * 3)));
				}
			}
		}
		catch(Exception e){}
	}
	
	/*
	 * returns 3 ints:
	 * target location as int
	 * type associated with it
	 * 1 for pasture, 0 for tower
	 */
	public int[] checkForNeed()
	{
		for(int k = 0; k < numSpots; k++)
		{
			try
			{
				int tower = rc.readBroadcast(start + (k * 3) + 1);
				int pastr = rc.readBroadcast(start + (k * 3) + 2);
				
				if(tower == 1)
				{
					if(locs[k].x != 0 || locs[k].y != 0)
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
						int[] answer = {TowerUtil.convertMapLocationToInt(locs[k].add(choice)), tower, 0};
						return answer;
					}
					else
					{
						rc.broadcast(start + (k * 3) + 1, 0);
					}
				}
				if(pastr == 1 || pastr == 2 || pastr > 3)
				{
					if(locs[k].x != 0 || locs[k].y != 0)
					{
						rc.broadcast(start + (k * 3) + 2, 3);
						int[] answer = {TowerUtil.convertMapLocationToInt(locs[k]), pastr, 1};
						return answer;
					}
					else
					{
						rc.broadcast(start + (k * 3) + 2, 0);
					}
				}
			}
			catch(Exception e){}
		}
		
		int[] answer = {-1,-1,-1};
		return answer;
	}
	
	public void sendRequest(MapLocation target, boolean pastr)
	{
		try
		{
			for(int k = 0; k < numSpots; k++)
			{
				if(locs[k].distanceSquaredTo(target) <= 16)
				{
					if(!pastr && rc.readBroadcast(start + (k * 3) + 1) == 0)
					{
						rc.broadcast(start + (k * 3) + 1, 1);
					}
					else if(rc.readBroadcast(start + (k * 3) + 2) == 0)
					{
						rc.broadcast(start + (k * 3) + 2, 2);
					}
				}
			}
		}
		catch(Exception e){}
	}
	
	public void madeIt(boolean pastr)
	{
		MapLocation target = rc.getLocation();
		
		try
		{
			for(int k = 0; k < numSpots; k++)
			{
				if(locs[k].distanceSquaredTo(target) <= 16)
				{
					if(!pastr)
					{
						rc.broadcast(start + (k * 3) + 1, 0);
					}
					else
					{
						rc.broadcast(start + (k * 3) + 2, 0);
					}
					break;
				}
			}
		}
		catch(Exception e){}
	}
	
	public static void setInitial(RobotController rc)
	{
		for(int k = 0; k < numSpots; k++)
		{
			try
			{
				rc.broadcast(start + (k * 3) + 1, 1);
				rc.broadcast(start + (k * 3) + 2, 2);
			}
			catch(Exception e){}
		}
	}
}
