package theSwarm6;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.TerrainTile;

public class Hatchery {
	
	RobotController rc;
	MapLocation target;
    boolean goneForPastr = false;
    int roundNum = 0;
    boolean build = false;
    boolean build2 = false;
    int lastCreate = 0;
    boolean doublePastr;
	
	public Hatchery(RobotController rc)
	{
		this.rc = rc;
		
		HQFunctions.InitialLocationBroadcasts(rc);

		HQFunctions.findInitialRally(rc);
		if(TowerUtil.isGoodCorner(rc, TowerUtil.findBestCorner(rc)))
		{
			//rc.setIndicatorString(0, "Good Corner: " + TowerUtil.findBestCorner(rc));
		}
		else
		{
			//rc.setIndicatorString(0, "Bad Corner: " + TowerUtil.findBestCorner(rc));
		}
		
		doublePastr = checkDoublePastr(rc, TowerUtil.bestSpot3(rc), TowerUtil.getOppositeSpot(rc, TowerUtil.bestSpot3(rc)));
		
		if(doublePastr)
		{
			rc.setIndicatorString(2, "Double Pasture");
		}
		else
		{
			rc.setIndicatorString(2, "Single Pasture");
		}
	}

	public void run()
	{
		while (true)
		{
            Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());

			if (rc.isActive())
			{
                Movement.fire(rc, enemies, null);
				HQFunctions.SpawnSoldiers(rc);
			}

			if (Clock.getRoundNum() % 5 == 0 && Clock.getRoundNum() > 100)
			{
				//HQFunctions.moveTargetLocationRandomly(rc);
                /*
                if (goneForPastr && (rc.sensePastrLocations(rc.getTeam()).length > 0 || roundNum > (Clock.getRoundNum() - 250)))
                {
                    HQFunctions.setTargetLocation(rc, goneForPastr);
                }
                else
                {
                    goneForPastr = HQFunctions.setTargetLocation(rc, goneForPastr);
                    roundNum = Clock.getRoundNum();
                }*/
                HQFunctions.setTargetLocation(rc, true);
                //HQFunctions.findInitialRally(rc);

			}
			
			
			if(Clock.getRoundNum() >= lastCreate && rc.sensePastrLocations(rc.getTeam()).length == 0)
			{
				build = true;
				if(doublePastr)
				{
					build2 = true;
				}
				lastCreate = Clock.getRoundNum() + 300;
			}
			
			try
			{
				if(build && rc.readBroadcast(4) == 0 && rc.readBroadcast(5) == 0)
				{
					rc.broadcast(4, 1);
					rc.broadcast(5, 50);
					build = false;
				}
				else if(build2 && rc.readBroadcast(4) == 0 && rc.readBroadcast(5) == 0)
				{
					rc.broadcast(4, -1);
					rc.broadcast(5, -3);
					build2 = false;
				}
			}
			catch(Exception e) {}
			
			
			rc.yield();
		}
	}
	
	public static boolean checkDoublePastr(RobotController rc, MapLocation m1, MapLocation m2){
    	try{
    		int mapSize = Utilities.getMapSize(rc);
    		MapLocation enemy = rc.senseEnemyHQLocation();
    		MapLocation pastr1 = m1;
    		MapLocation pastr2 = m2;
    		int distToEnemy1 = m2.distanceSquaredTo(enemy);
    		int voidsBetween = 0;
    		MapLocation temp = pastr1;
    		while(temp.x != pastr2.x || temp.y != pastr2.y)
    		{
    			if(rc.senseTerrainTile(temp) == TerrainTile.VOID)
    			{
    				voidsBetween++;
    			}
    			temp = temp.add(temp.directionTo(pastr2));
    		}
    		if(mapSize >= 100){
    			if(TowerUtil.getSpotScore(rc, pastr1) > 50){
    				if((pastr1.distanceSquaredTo(pastr2) > mapSize/2. || (voidsBetween > 1 && pastr1.distanceSquaredTo(pastr2) > 100)) && distToEnemy1 > 100){
    					return true;
    				} else {
    					return false;
    				}
    			} else {
    				return false;
    			}
    		} else {
    			return false;
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    }
}
