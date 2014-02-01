package DeepBlue.Strategies;

import DeepBlue.*;
import battlecode.common.*;

/**
 * Created by fredkneeland on 1/28/14.
 */
public abstract class UnitStratPastrKiller extends UnitStrategy {
    public static RobotController rc;
    public static MapLocation target;
    public static final MapLocation wait1 = null;
    public static final MapLocation wait2 = null;
    public static final MapLocation wait3 = null;
    public static MapLocation waitLoc;
    static final int samePastr = 35675;
    static final int inPastr = 35780;
    static MapLocation oldTarget;

    public static void initialize(RobotController rcIn) throws GameActionException
    {
        rc = rcIn;
    }

    public static void upDate() throws GameActionException
    {
    	boolean doublePastr = false;
    	MapLocation[] enemyPastrs = rc.sensePastrLocations(rc.getTeam().opponent());
        MapLocation[] ourPastrs = rc.sensePastrLocations(rc.getTeam());
    	if(rc.readBroadcast(samePastr)==1){
    		doublePastr = true;
    		if(rc.readBroadcast(inPastr)!= 0){
    			int loc = rc.readBroadcast(inPastr);
    			waitLoc = DeepBlue.VectorFunctions.intToLoc(loc);
    			
    			for(int i = 0; i < 6; i++){
        			waitLoc = waitLoc.add(waitLoc.directionTo(rc.senseHQLocation()));
        		}
    		} else {
    			waitLoc = new MapLocation(rc.getMapWidth()/2, rc.getMapHeight()/2);
    		}
    	}
        if(doublePastr == true){
        	if (enemyPastrs.length > 0 && rc.getLocation().distanceSquaredTo(enemyPastrs[0])<500)
        	{
        		Soldiers.nav.setSneak(false);
        		MapLocation closest = enemyPastrs[enemyPastrs.length-1];
        		int smallestDist = rc.getLocation().distanceSquaredTo(closest);
        		
        		for (int i = enemyPastrs.length - 1; --i>=0;)
        		{
        			MapLocation current = enemyPastrs[i];
        			int currentDist = rc.getLocation().distanceSquaredTo(current);
        			if (currentDist < smallestDist)
        			{
        				smallestDist = currentDist;
        				closest = current;
        			}
        		}
        		target = closest;
        	} else {
        		target = waitLoc;
        	}
        } else {
        	if (enemyPastrs.length > 0)
        	{
        		Soldiers.nav.setSneak(false);
        		MapLocation closest = enemyPastrs[enemyPastrs.length-1];
        		int smallestDist = rc.getLocation().distanceSquaredTo(closest);
        		
        		for (int i = enemyPastrs.length - 1; --i>=0;)
        		{
        			MapLocation current = enemyPastrs[i];
        			int currentDist = rc.getLocation().distanceSquaredTo(current);
        			if (currentDist < smallestDist)
        			{
        				smallestDist = currentDist;
        				closest = current;
        			}
        		}
        		
        		target = closest;
        	}
        	else if (ourPastrs.length > 0 && ourPastrs[0].distanceSquaredTo(rc.senseHQLocation()) > 10)
        	{
        		MapLocation closest = ourPastrs[ourPastrs.length-1];
        		int smallestDist = rc.getLocation().distanceSquaredTo(closest);
        		
        		for (int i = ourPastrs.length - 1; --i>=0;)
        		{
        			MapLocation current = ourPastrs[i];
        			int currentDist = rc.getLocation().distanceSquaredTo(current);
        			if (currentDist < smallestDist && current.distanceSquaredTo(rc.senseHQLocation()) > 10)
        			{
        				smallestDist = currentDist;
        				closest = current;
        			}
        		}
        		target = closest;
        		
        		if (rc.getLocation().distanceSquaredTo(target) < 50)
        		{
        			Soldiers.nav.setSneak(true);
        		}
        		else
        		{
        			Soldiers.nav.setSneak(false);
        		}
        		
        		target = target.add(target.directionTo(rc.senseEnemyHQLocation()));
        		target = target.add(target.directionTo(rc.senseEnemyHQLocation()));
        		target = target.add(target.directionTo(rc.senseEnemyHQLocation()));
                target = target.add(target.directionTo(rc.senseEnemyHQLocation()));
        	}
        	else
        	{
        		target = new MapLocation(rc.getMapWidth()/2, rc.getMapHeight()/2);
                while(Soldiers.map.getTileType(target)==RoadMap.TileType.TTVoid || Soldiers.map.getTileType(target)==RoadMap.TileType.TTOffMap || target.equals(rc.senseHQLocation())){
                	target = target.add(target.directionTo(rc.senseHQLocation()));
                }
                if(Soldiers.map.getTileType(target)==RoadMap.TileType.TTVoid || Soldiers.map.getTileType(target)==RoadMap.TileType.TTOffMap || target.equals(rc.senseHQLocation())){
                	while(Soldiers.map.getTileType(target)==RoadMap.TileType.TTVoid || Soldiers.map.getTileType(target)==RoadMap.TileType.TTOffMap || target.equals(rc.senseHQLocation())){
                		target = target.add(target.directionTo(rc.senseHQLocation()).opposite());
                	}
                }
        	}
        }

        if (target == null)
        {
            target = new MapLocation(rc.getMapWidth()/2, rc.getMapHeight()/2);
            while(Soldiers.map.getTileType(target)==RoadMap.TileType.TTVoid || Soldiers.map.getTileType(target)==RoadMap.TileType.TTOffMap || target.equals(rc.senseHQLocation())){
            	target = target.add(target.directionTo(rc.senseHQLocation()));
            }
            if(Soldiers.map.getTileType(target)==RoadMap.TileType.TTVoid || Soldiers.map.getTileType(target)==RoadMap.TileType.TTOffMap || target.equals(rc.senseHQLocation())){
            	while(Soldiers.map.getTileType(target)==RoadMap.TileType.TTVoid || Soldiers.map.getTileType(target)==RoadMap.TileType.TTOffMap || target.equals(rc.senseHQLocation())){
            		target = target.add(target.directionTo(rc.senseHQLocation()).opposite());
            	}
            }
        }

        if (oldTarget == null || !oldTarget.equals(target))
        {
            oldTarget = target;
            Soldiers.nav.setDestination(target);
        }
    }
}