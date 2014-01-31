package DeepBlue.Strategies;

import DeepBlue.*;
import battlecode.common.*;
import firstjoshua.Soldier;

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

        if (enemyPastrs.length > 0)
        {
            Soldiers.nav.setSneak(false);
            MapLocation closest = enemyPastrs[enemyPastrs.length-1];
            int smallestDist = rc.getLocation().distanceSquaredTo(closest);

            for (int i = enemyPastrs.length - 1; --i>=0;)
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
        }
        
        Soldiers.nav.setDestination(target);
    }


}
