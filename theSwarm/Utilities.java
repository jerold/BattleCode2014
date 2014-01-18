package theSwarm;

import battlecode.common.*;

/**
 * Created by fredkneeland on 1/16/14.
 */
public class Utilities
{
    static Direction[] directions = Direction.values();

    public static void fireCircle(RobotController rc, int radius, MapLocation center)
    {
        for(int k = 0; k < directions.length; k++)
        {
            while(!rc.isActive()){rc.yield();}
            MapLocation toFire = center.add(directions[k], radius);
            try
            {
                if(toFire.x >= 0 && toFire.x < rc.getMapWidth() && toFire.y >= 0 && toFire.y < rc.getMapHeight() && rc.canAttackSquare(toFire))
                {
                    rc.attackSquare(toFire);
                    rc.yield();
                }
            }
            catch(Exception e){}
            while(!rc.isActive()){rc.yield();}
            toFire = center;
            for(int a = 0; a < radius / 2; a++)
            {
                toFire = toFire.add(directions[k]);
                toFire = toFire.add(directions[(k + 1) % directions.length]);
            }
            try
            {
                if(toFire.x >= 0 && toFire.x < rc.getMapWidth() && toFire.y >= 0 && toFire.y < rc.getMapHeight() && rc.canAttackSquare(toFire))
                {
                    rc.attackSquare(toFire);
                    rc.yield();
                }
            }
            catch(Exception e){}
        }
    }

    public static void pullInto(RobotController rc, int radius, MapLocation center)
    {
        for(int k = 0; k < directions.length; k++)
        {
            while(!rc.isActive()){rc.yield();}
            MapLocation toFire = center.add(directions[k], radius);
            try
            {
                while(toFire.distanceSquaredTo(center) > 3)
                {
                    if(toFire.x >= 0 && toFire.x < rc.getMapWidth() && toFire.y >= 0 && toFire.y < rc.getMapHeight() && rc.canAttackSquare(toFire))
                    {
                        try
                        {
                            rc.attackSquare(toFire);
                            rc.yield();
                        }
                        catch(Exception e){}
                    }
                    toFire = toFire.add(directions[k].opposite());
                }
            }
            catch(Exception e){}
        }
    }
}
