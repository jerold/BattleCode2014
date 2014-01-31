package DeepBlue.Strategies;

import DeepBlue.*;
import battlecode.common.*;

/**
 * Created by fredkneeland on 1/28/14.
 */
public abstract class UnitStratPastrKiller extends UnitStrategy {
    public static RobotController rc;
    public static MapLocation target;
    public static MapLocation firstPastr = null;
    public static boolean found = false;
    public static long[] teamMem;

    public static void initialize(RobotController rcIn) throws GameActionException
    {
        rc = rcIn;
    }

    public static void upDate() throws GameActionException
    {
        MapLocation[] enemyPastrs = rc.sensePastrLocations(rc.getTeam().opponent());
        MapLocation[] ourPastrs = rc.sensePastrLocations(rc.getTeam());
        if(rc.readBroadcast(35675)==1){
        	
        	if (enemyPastrs.length > 0)
            {
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
                        if(found == false){
                        	firstPastr = closest;
                        	found = true;
                        }
                    }
                }
                target = closest;
            } else if(firstPastr != null){
            	Direction dir = firstPastr.directionTo(new MapLocation(rc.getMapWidth()/2, rc.getMapHeight()/2));
            	for(int i = 0; i < 6; i++){
        			firstPastr = firstPastr.add(dir);
        			System.out.println("loc: " + firstPastr.x + ", " + firstPastr.y);
        		}
            	target = firstPastr;
            } else {
            	target = new MapLocation(rc.getMapWidth()/2, rc.getMapHeight()/2);
            }
        } else {
        	if (enemyPastrs.length > 0)
        	{
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

        		target = target.add(target.directionTo(rc.senseEnemyHQLocation()));
        		target = target.add(target.directionTo(rc.senseEnemyHQLocation()));
        		target = target.add(target.directionTo(rc.senseEnemyHQLocation()));
        	}
        	else
        	{
        		target = new MapLocation(rc.getMapWidth()/2, rc.getMapHeight()/2);
        	}
        }
        
        Soldiers.nav.setDestination(target);
    }


}
