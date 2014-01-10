package ultraGreedy;


import java.util.Random;

import battlecode.common.Direction;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class PASTR
{
    Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
    Random rand;
    RobotController rc;
    boolean left;
    MapLocation target;
    Direction dir;
    boolean reachedTarget = false;
    int movedAmount = 0;

    /**
     * PASTRs will alternate between going out left and right 10 spaces at a 90 angle from enemy HQ
     * then they will search for a location with relatively high cows that doesn't overlap with
     * other pastrs
     */
    public PASTR(RobotController rc, boolean left)
    {
        this.rc = rc;
        this.left = left;
        dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
        target = rc.getLocation();
        if (left)
        {
            dir = dir.rotateLeft();//.rotateLeft();
        }
        else
        {
            dir = dir.rotateRight();//.rotateRight();
        }

        // we move our target location 10 spaces in the direction we chose
        for (int i = 0; i < 10; i++)
        {
            target = target.add(dir);
        }
    }

    public void run()
    {
        rand = new Random();
        while(true) {
            if (rc.getType() == RobotType.SOLDIER)
            {
                try
                {
                    if (rc.isActive())
                    {
                        // first we need to get to our target
                        // if our target is a space we can't get to we will go towards it 15 times before looking for a pastr location
                        if (!reachedTarget)
                        {
                            /*
                            if (rc.senseTerrainTile(target).equals(TerrainTile.VOID) || rc.senseTerrainTile(target).equals(TerrainTile.OFF_MAP))
                            {*/
                                int k = 0;
                                while (k < 25)
                                {
                                    if (rc.isActive())
                                    {
                                        k++;
                                    }
                                    Direction direction = rc.getLocation().directionTo(target);
                                    Utilities.MoveDirection(rc, direction, false);

                                    rc.yield();
                                }
                            /*}
                            // otherwise we should be able to get to our target location
                            else
                            {
                                Utilities.MoveMapLocation(rc, target, false);
                            }*/
                            reachedTarget = true;
                        }

                        if (reachedTarget)
                        {
                            //rc.selfDestruct();
                        }

                        // at this point we should either be at our target or close to it

                        GameObject[] nearByBots = rc.senseNearbyGameObjects(Robot.class,16);
                        while (movedAmount < 5 || rc.senseCowsAtLocation(rc.getLocation()) < 100 || (nearByBots.length > 0))
                        {
                            //rc.selfDestruct();
                            rc.setIndicatorString(0, ""+movedAmount);
                            //if ()
                            nearByBots = rc.senseNearbyGameObjects(Robot.class, 16);
                            if (movedAmount % 3 == 0)
                            {
                                dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
                                if (left)
                                {

                                    dir = dir.rotateLeft().rotateLeft();
                                }
                                else
                                {
                                    dir = dir.rotateRight().rotateRight();
                                }
                            }
                            else if (movedAmount % 3 == 1)
                            {
                                dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
                                if (left)
                                {
                                    dir = dir.rotateLeft().rotateLeft().rotateLeft();
                                }
                                else
                                {
                                    dir = dir.rotateRight().rotateRight().rotateRight();
                                }
                            }
                            else
                            {
                                dir = directions[rand.nextInt(8)];
                            }

                            if (rc.isActive())
                            {
                                movedAmount++;
                                Utilities.MoveDirection(rc, dir, true);
                            }

                        }
                        while (true)
                        {
                            //rc.selfDestruct();
                            if (rc.isActive())
                            {
                                rc.setIndicatorString(0, "Constructing");
                                for (int i = 0; i <100; i++)
                                {
                                    if (rc.isActive())
                                    {
                                        try
                                        {
                                            rc.construct(RobotType.PASTR);
                                        } catch (Exception e)
                                        {
                                            e.printStackTrace();
                                            System.out.println("SOLDIER Exception");
                                        }

                                    }
                                    rc.yield();
                                }
                            }
                        }
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                    System.out.println("SOLDIER Exception");
                }
            }
        }
    }
}
