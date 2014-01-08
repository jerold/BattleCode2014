package greedy;


import java.util.Random;

import battlecode.common.*;

/**
 * Created by fredkneeland on 1/7/14.
 */


public class Marine
{
    RobotController rc;
    boolean left;
    Random rand;
    int pastrIndex = 0;
    MapLocation target;
    MapLocation[] pastrSpots;
    Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
    Robot[] nearbyEnemies;

    public Marine(RobotController rc)
    {
        this.rc = rc;
    }

    public void run()
    {
        rand = new Random();
        rc.setIndicatorString(0, "Marine");
        while(true) {
            if (rc.getType() == RobotType.SOLDIER)
            {
                try
                {
                    if (rc.isActive())
                    {
                        pastrSpots = rc.sensePastrLocations(rc.getTeam());
                        target = pastrSpots[pastrIndex];

                        Direction direction = target.directionTo(rc.senseHQLocation());

                        while (rc.senseTerrainTile(target.add(direction)) == TerrainTile.VOID || rc.senseTerrainTile(target.add(direction)) == TerrainTile.OFF_MAP)
                        {
                            direction = directions[rand.nextInt(8)];
                        }

                        target = target.add(direction);

                        Utilities.MoveMapLocation(rc, target, false);
                        rc.setIndicatorString(1, "Made It");

                        // if we see any enemy in range we attack it or if we see an enemy comming we move towards it
                        nearbyEnemies = rc.senseNearbyGameObjects(Robot.class,10,rc.getTeam().opponent());
                        if (nearbyEnemies.length > 0)
                        {
                            Utilities.fire(rc);
                        }
                        nearbyEnemies = rc.senseNearbyGameObjects(Robot.class,100,rc.getTeam().opponent());
                        while (nearbyEnemies.length > 1)
                        {
                            nearbyEnemies = rc.senseNearbyGameObjects(Robot.class,10,rc.getTeam().opponent());
                            if (nearbyEnemies.length > 1)
                            {
                                Utilities.fire(rc);
                            }
                            else
                            {
                                nearbyEnemies = rc.senseNearbyGameObjects(Robot.class,100,rc.getTeam().opponent());
                                Utilities.MoveDirection(rc, rc.getLocation().directionTo(rc.senseLocationOf(nearbyEnemies[0])), false);
                            }
                            nearbyEnemies = rc.senseNearbyGameObjects(Robot.class,100,rc.getTeam().opponent());
                        }

                        pastrIndex++;
                        pastrIndex %= pastrSpots.length;



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
