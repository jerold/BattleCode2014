package theSwarm4;

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
            catch(Exception e){
                e.printStackTrace();
            }
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
            catch(Exception e){
                e.printStackTrace();
            }
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
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static boolean MapLocationOutOfRangeOfEnemies(RobotController rc, MapLocation[] SeenEnemies, MapLocation location)
    {
        try
        {
            if (!rc.canMove(rc.getLocation().directionTo(location)))
            {
                return false;
            }
            // we loop through all enemies and if any of them are close enough to shoot this spot then we don't move
            for (int i = SeenEnemies.length; --i >= 0; )
            {
                MapLocation enemySpot = SeenEnemies[i];
                if (enemySpot != null)
                {
                    if (enemySpot.distanceSquaredTo(location) < 11)
                    {
                        return false;
                    }
                }
            }
            return true;
        } catch(Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }

    public static int AlliesBehindUs(RobotController rc, Robot[] allies, MapLocation target)
    {
        int numbOfAlliesBehind = 0;
        try
        {
            int distanceToTarget = rc.getLocation().distanceSquaredTo(target);

            for (int i = 0; i < allies.length; i++)
            {
                if (distanceToTarget < rc.senseLocationOf(allies[i]).distanceSquaredTo(target))
                {
                    numbOfAlliesBehind++;
                }
            }
        } catch(Exception e)
        {
            e.printStackTrace();
        }
        return numbOfAlliesBehind;
    }

    public static int AlliesAhead(RobotController rc, Robot[] allies, MapLocation target)
    {
        int numbOfAlliesAhead = 0;
        try
        {
            int distanceToTarget = rc.getLocation().distanceSquaredTo(target);

            for (int i = 0; i < allies.length; i++)
            {
                if (distanceToTarget > rc.senseLocationOf(allies[i]).distanceSquaredTo(target))
                {
                    numbOfAlliesAhead++;
                }
            }
        } catch(Exception e)
        {
            e.printStackTrace();
        }
        return numbOfAlliesAhead;
    }

    public static boolean MapLocationInRangeOfEnemyHQ(RobotController rc, MapLocation target)
    {
        if (target.distanceSquaredTo(rc.senseEnemyHQLocation()) < 30)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static boolean AlliesEngaged(RobotController rc, MapLocation[] enemies, MapLocation[] allies)
    {
        boolean alliesEngaged = false;
        try
        {
            if (enemies != null && allies != null)
            {
                for (int i = enemies.length; --i >= 0; )
                {
                    MapLocation enemySpot = enemies[i];
                    if (enemySpot != null)
                    {
                        for (int j = allies.length; --j >= 0; )
                        {

                            if (enemySpot.distanceSquaredTo(allies[j]) <= 10)
                            {
                                alliesEngaged = true;
                                j = -1;
                                i = -1;
                            }
                        }
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        return alliesEngaged;
    }

    // this method returns true if a MapLocation is inside of an array false otherwise
    public static boolean MapLocationInArray(RobotController rc, MapLocation target, MapLocation[] array)
    {

        for (int i = 0; i < array.length; i++)
        {
            if (array[i].equals(target))
            {
                return true;
            }
        }

        return false;
    }
    
    public static int convertMapLocationToInt(MapLocation loc)
    {
        int x = loc.x;
        int y = loc.y;
        int total = (x*1000) + y;
        return total;
    }

    public static MapLocation convertIntToMapLocation(int value)
    {
        int x = value / 1000;
        int y = value % 1000;
        MapLocation loc = new MapLocation(x, y);
        return loc;
    }
}
