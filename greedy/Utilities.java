package greedy;

/**
 * Created by fredkneeland on 1/7/14.
 */
import java.util.Random;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class Utilities
{
    static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
    static Random rand;

    // In this function the HQ spawns a soldier ideally toward the enemy base but in any direction otherwise
    public static void SpawnSoldiers(RobotController rc)
    {
        try
        {
            if (rc.isActive() && rc.getType() == RobotType.HQ)
            {
                Direction toEnemy = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
                if (rc.senseObjectAtLocation(rc.getLocation().add(toEnemy)) == null) {}
                else
                {
                    for (int i = 0; i < 7; i++)
                    {
                        toEnemy = toEnemy.rotateLeft();

                        if (rc.senseObjectAtLocation(rc.getLocation().add(toEnemy)) == null)
                        {
                            i = 47;
                        }
                        else if (i == 6)
                        {
                            toEnemy = Direction.NONE;
                        }
                    }
                }

                if (toEnemy != Direction.NONE)
                {
                    if (rc.isActive())
                    {
                        if (rc.getType() == RobotType.HQ)
                        {
                            rc.spawn(toEnemy);
                        }
                    }
                }
            }
        } catch (Exception e)
        {
            System.out.println("Utility Exception");
        }
    }

    public static MapLocation[] BestPastureSpots(RobotController rc)
    {
        MapLocation[] empty = new MapLocation[1];
        try
        {
            //Check if a robot is spawnable and spawn one if it is
            double growthRates[][] = rc.senseCowGrowth();
            int countofBestSpots = 0;
            double best = 0.0001;
            for (int i = 0; i < rc.getMapWidth(); i++)
            {
                for (int k = 0; k < rc.getMapHeight(); k++)
                {
                    if (growthRates[i][k] > best)
                    {
                        best = growthRates[i][k];
                        countofBestSpots = 1;
                    }
                    else if (growthRates[i][k] == best)
                    {
                        countofBestSpots++;
                    }

                    SpawnSoldiers(rc);
                }
            }

            MapLocation[] bestSpots = new MapLocation[countofBestSpots];
            int index = 0;
            for (int i = 0; i < rc.getMapWidth(); i++)
            {
                for (int k = 0; k < rc.getMapHeight(); k++)
                {
                    if (growthRates[i][k] == best)
                    {
                        bestSpots[index] = new MapLocation(i,k);
                        index++;
                    }

                    SpawnSoldiers(rc);
                }
            }

            for (int i = 1; i < countofBestSpots; i++)
            {
                for (int j = i; j < countofBestSpots; j++)
                {
                    int dist1 = rc.getLocation().distanceSquaredTo(bestSpots[j]);
                    int dist2 = rc.getLocation().distanceSquaredTo(bestSpots[j]);

                    if (dist1 < dist2)
                    {
                        MapLocation temp = bestSpots[j];
                        bestSpots[j] = bestSpots[j-1];
                        bestSpots[j-1] = temp;
                    }

                    SpawnSoldiers(rc);
                }
            }
            return bestSpots;
        } catch (Exception e)
        {
            System.out.println("Utility Exception");
        }
        return empty;
    }

    public static void MoveDirection(RobotController rc, Direction dir, boolean sneak)
    {
        Direction newDir = Direction.NONE;
        int counter = 1;
        try
        {
            // here we do some checks to make sure that we don't throw any exceptions
            if (rc.isActive())
            {

                if (dir != Direction.NONE && dir != Direction.OMNI)
                {
                    if (!rc.canMove(dir))
                    {
                        // now we loop through the other 7 directions to find one that works
                        for (int i = 0; i < 7; i++)
                        {
                            newDir = dir;
                            // first look right
                            if (i % 2 == 0)
                            {
                                // now we rotate 45 right a certain numb of times
                                for (int j = 0; j < counter; j++)
                                {
                                    newDir = newDir.rotateRight();
                                }
                            }
                            // the look left
                            else
                            {
                                // now we rotate 45 left a certain numb of times
                                for (int j = 0; j < counter; j++)
                                {
                                    newDir = newDir.rotateLeft();
                                }
                                // now after we have looked both ways we update counter
                                counter++;
                            }
                            // at end of for loop we check to see if we can move or if we need to keep looking
                            if (rc.canMove(newDir))
                            {
                                i = 48;
                            }
                            // if we have gone through all our options and can't move then we will wait
                            else if (i == 5 && !rc.canMove(newDir))
                            {
                                newDir = Direction.NONE;
                            }
                        }
                    }
                    // we are going to move in the direction of newDir and as we can move in direction dir we assign newDir to it
                    else
                    {
                        newDir = dir;
                    }

                    if (newDir != Direction.NONE)
                    {

                        // now we decide if we are going to sneak or run
                        if (sneak)
                        {
                            // another check to make sure we don't throw any exceptions
                            if (rc.isActive() && rc.canMove(newDir))
                            {
                                rc.sneak(newDir);
                            }
                        }

                        else
                        {
                            // another check to make sure we don't throw any exceptions
                            if (rc.isActive() && rc.canMove(newDir))
                            {
                                rc.move(newDir);
                            }
                        }

                    }

                }

            }
        } catch (Exception e)
        {
            // tell the console we through an exception in utility object for debug purposes
            System.out.println("Utility Exception");
            System.out.println(newDir);
        }
    }

    public static void MoveMapLocation(RobotController rc, MapLocation target, boolean sneak)
    {
        MapLocation[] pastLocations = new MapLocation[10];
        int side = 45;
        Direction dir;
        Direction newDir;
        rand = new Random();
        // we initialize pastLocations to hold our current location 5 times
        for (int i = 0; i < pastLocations.length; i++)
        {
            pastLocations[i] = rc.getLocation();
        }

        // this method will run until we get to our target location
        while (!rc.getLocation().equals(target))
        {
            // we put the try block inside of the while loop so an exception won't terminate the method
            try
            {
                dir = rc.getLocation().directionTo(target);
                newDir = Direction.NONE;

                // simple shoot at an enemy if we see one will need to be improved later
                Robot[] nearbyEnemies = rc.senseNearbyGameObjects(Robot.class,10,rc.getTeam().opponent());
                if (nearbyEnemies.length > 0)
                {
                    fire(rc);
                }
                // if we can move towards target and we haven't been on the square recently then lets move
                else if (rc.canMove(dir) && !MapLocationInArray(rc, rc.getLocation().add(dir), pastLocations))
                {
                    newDir = dir;
                    //System.out.println("if Statement");
                    side = 45;
                }
                else
                {
                    if (side == 45)
                    {
                        side = rand.nextInt(Clock.getRoundNum()) % 2;
                    }

                    newDir = dir;

                    // now we loop through the other 7 directions to find one that works
                    for (int i = 0; i < 7; i++)
                    {

                        if (side == 0)
                        {
                            newDir = newDir.rotateRight();
                        }
                        else
                        {
                            newDir = newDir.rotateLeft();
                        }

                        if (rc.canMove(newDir) && !MapLocationInArray(rc, rc.getLocation().add(newDir), pastLocations))
                        {
                            i = 48;
                        }
                        else if (i == 6)
                        {
                            newDir = Direction.NONE;
                        }

                    }
                }

                // if we found a direction to move then we go to it
                if (newDir != Direction.NONE)
                {

                    // now we decide if we are going to sneak or run
                    if (sneak)
                    {
                        //System.out.println(newDir);
                        //System.out.println(rc.canMove(newDir));

                        // another check to make sure we don't throw any exceptions
                        if (rc.isActive() && rc.canMove(newDir))
                        {
                            //System.out.println(newDir);
                            rc.sneak(newDir);
                        }


                    }
                    else
                    {
                        // another check to make sure we don't throw any exceptions
                        if (rc.isActive() && rc.canMove(newDir))
                        {
                            rc.move(newDir);
                        }
                    }
                }

                // now we  shift everything up one in pastLocations
                if (rc.getLocation() != pastLocations[(pastLocations.length-1)])
                {
                    for (int j = 0; j < (pastLocations.length-1); j++)
                    {
                        pastLocations[j] = pastLocations[j+1];
                        System.out.println(pastLocations[j]);
                    }
                    // stick current local into array
                    pastLocations[(pastLocations.length-1)] = rc.getLocation();
                }

                rc.yield();
            }
            catch (Exception e)
            {
                // tell the console we through an exception in utility object for debug purposes
                System.out.println("Utility Exception");
                System.out.println(e.toString());
                rc.yield();
            }
        }
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
    public static void fire(RobotController rc)
    {
        int radius;

        try
        {
            if(rc.getType() == RobotType.HQ)
            {
                radius = 15;
                Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, radius, rc.getTeam().opponent());
                Direction[] dirs = Direction.values();
                Robot target = null;
                int maxValue = 0;

                for(int k = 0; k < enemies.length; k++)
                {
                    MapLocation loc = rc.senseRobotInfo(enemies[k]).location;
                    int value = 2;
                    for(int a = 0; a < 8; a++)
                    {
                        try
                        {
                            if(rc.senseObjectAtLocation(loc.add(dirs[a])).getTeam() == rc.getTeam().opponent())
                            {
                                value++;
                            }
                            else if(rc.senseObjectAtLocation(loc.add(dirs[a])).getTeam() == rc.getTeam())
                            {
                                value--;
                            }
                        }
                        catch(Exception e){}
                    }

                    rc.setIndicatorString(0, "" + value);

                    if(value > maxValue)
                    {
                        maxValue = value;
                        target = enemies[k];
                    }
                }

                if(target != null)
                {
                    rc.attackSquare(rc.senseRobotInfo(target).location);
                }

            }
            else
            {
                radius = 10;
                Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, radius, rc.getTeam().opponent());
                Robot target = null;

                for(int k = 0; k < enemies.length; k++)
                {
                    if(target == null)
                    {
                        target = enemies[k];
                    }
                    else if(rc.senseRobotInfo(enemies[k]).health < rc.senseRobotInfo(target).health)
                    {
                        target = enemies[k];
                    }
                }

                if(target != null)
                {
                    rc.attackSquare(rc.senseRobotInfo(target).location);
                }
            }
        }
        catch(Exception e){}
    }
}
