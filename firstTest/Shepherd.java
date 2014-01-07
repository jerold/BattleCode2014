package firstTest;

import java.util.Random;

import battlecode.common.*;

/**
 * Created by fredkneeland on 1/7/14.
 */
public class Shepherd
{
    boolean right;
    MapLocation target;
    Direction dir;
    boolean isPasture = false;
    int numbOfMoves = 0;
    Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
    Random rand;
    GameObject leader = null;
    int shepardCount = 0;
    int robotCount = -1;
    MapLocation[] leaderSpots = new MapLocation[4];
    boolean orbit = false;
    MapLocation originalTarget;
    MapLocation newTarget;
    int moveAmount = 1;

    // currently two "squads" of shepards are sent out one to the right and one to the left as directed by HQ
    public Shepherd(boolean right, RobotController rc)
    {
        this.right = right;
        dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
        // now we will set our rally point
        if (right)
        {
            dir = dir.rotateRight();
        }
        else
        {
            dir = dir.rotateLeft();
        }

        target = rc.getLocation();

        // now we run through a loop where we move target to a location 10 squares away in direction dir
        for (int i = 0; i < 10; i++)
        {
            target = target.add(dir);
        }

        originalTarget = target;

        for (int i = 0; i < leaderSpots.length; i++)
        {
            leaderSpots[i] = target;
        }
    }

    public void run(RobotController rc)
    {
        while (true)
        {
            try
            {
                // we wait until we get a certain number of shepards
                if (shepardCount < 3)
                {
                    shepardCount = rc.readBroadcast(2);
                }

                if (shepardCount == 3 && robotCount == -1)
                {
                    robotCount = rc.readBroadcast(1);
                }
                // if we are the fist shepherd then we will be the ones to set up the pasture
                if (shepardCount == 1)
                {
                    isPasture = true;
                }
                else
                {
                    if (!isPasture)
                    {
                        if (rc.canSenseSquare(target) && leader == null)
                        {
                            leader = rc.senseObjectAtLocation(target);
                        }
                    }
                }

                // we always check to see if we can shoot an enemy before we move
                Robot[] nearbyEnemies = rc.senseNearbyGameObjects(Robot.class,10,rc.getTeam().opponent());
                if (nearbyEnemies.length > 0)
                {
                    Shooter shooter = new Shooter(rc);
                    shooter.fire();
                }
                else if (robotCount > 0 && (robotCount+1) <= rc.readBroadcast(1))
                {
                    if (isPasture)
                    {
                        // in here we will try to find a good place for a pasture
                        if (rc.senseCowsAtLocation(rc.getLocation()) > 150  && numbOfMoves > 5 && (rc.senseTerrainTile(rc.getLocation()) != TerrainTile.ROAD))
                        {
                            if (rc.isActive())
                            {
                                rc.construct(RobotType.PASTR);
                            }
                        }
                        else
                        {
                            // now we sneak as we move so as to not scare the cows
                            if (numbOfMoves % 2 == 0)
                            {
                                if (rc.isActive())
                                {
                                    Utilities.MoveDirection(rc, dir, true);
                                    numbOfMoves++;
                                }
                            }
                            else
                            {
                                if (rc.isActive())
                                {
                                    //Direction dir2 = directions[rand.nextInt(8)];
                                    //Utilities.MoveDirection(rc, dir2, true);
                                    Utilities.MoveDirection(rc, dir, true);
                                    numbOfMoves++;
                                }
                            }
                        }
                    }
                    else
                    {
                        if (!orbit)
                        {
                            if ((leaderSpots[0].equals(leaderSpots[1])) && leaderSpots[1].equals(leaderSpots[2]) && leaderSpots[2].equals(leaderSpots[3]) && !(leaderSpots[3].equals(originalTarget)))
                            {
                                orbit = true;
                            }
                            else if (leader == null)
                            {
                                Utilities.MoveDirection(rc, rc.getLocation().directionTo(target), false);
                            }
                            else
                            {
                                target = rc.senseLocationOf(leader);
                                Utilities.MoveDirection(rc, rc.getLocation().directionTo(target), true);
                                if (Clock.getRoundNum() % 4 == 0)
                                {
                                    for (int i = 1; i < leaderSpots.length; i++)
                                    {
                                        leaderSpots[i-1] = leaderSpots[i];
                                    }
                                    leaderSpots[(leaderSpots.length-1)] = target;
                                }
                            }
                        }
                        else
                        {
                            if (newTarget == null)
                            {
                                Direction direction = leaderSpots[0].directionTo(rc.senseHQLocation());
                                newTarget = leaderSpots[0];

                                for (int i = 0; i < 6; i++)
                                {
                                    newTarget.add(direction);
                                }
                                int k = 0;
                                while (k < 4)
                                {
                                    try
                                    {
                                        if (rc.isActive())
                                        {
                                            Utilities.MoveDirection(rc, rc.getLocation().directionTo(rc.senseHQLocation()), true);
                                            rc.yield();
                                            k++;
                                        }
                                    } catch(Exception e)
                                    {
                                        // print some stuff so we can see the error produced
                                        e.printStackTrace();
                                        rc.yield();
                                    }

                                }
                            }
                            else
                            {

                                Direction direction = rc.getLocation().directionTo(leaderSpots[0]);
                                if (moveAmount % 10 == 0 && moveAmount > 15)
                                {
                                    direction = direction.rotateRight();
                                }
                                else
                                {
                                    direction = direction.rotateRight().rotateRight();

                                }
                                if (rc.isActive())
                                {
                                    moveAmount++;
                                }
                                Utilities.MoveDirection(rc, direction, false);
                            }
                        }
                    }
                }
                else
                {
                    // in here we will move to the rally point
                    Utilities.MoveDirection(rc, rc.getLocation().directionTo(target), false);
                    if (rc.getLocation().equals(target))
                    {
                        isPasture = true;
                    }
                }
                rc.yield();
            }
            catch(Exception e)
            {
                // print some stuff so we can see the error produced
                e.printStackTrace();
                rc.yield();
            }
        }
    }
}
