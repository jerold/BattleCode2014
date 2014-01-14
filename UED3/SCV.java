package UED3;

import battlecode.common.*;
import java.util.Random;

/**
 * Created by fredkneeland on 1/8/14.
 */
public class SCV
{
    RobotController rc;
    Random rand;
    int corner;
    MapLocation goToSpot;
    Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
    boolean arrived = false;

    public SCV (RobotController rc)
    {
        this.rc = rc;
        rand = new Random();
        corner = rand.nextInt(Clock.getRoundNum()) % 4;

        if (corner == 0)
        {
            goToSpot = new MapLocation(rc.getMapWidth()/10, rc.getMapHeight() -10);
        }
        else if (corner == 1)
        {
            goToSpot = new MapLocation(rc.getMapWidth()/10, rc.getMapHeight()/10);
        }
        else if (corner == 2)
        {
            goToSpot = new MapLocation(rc.getMapWidth()-10, rc.getMapHeight()-10);
        }
        else
        {
            goToSpot = new MapLocation(rc.getMapWidth()-10, rc.getMapHeight()/10);
        }

        while (rc.senseTerrainTile(goToSpot).equals(TerrainTile.VOID) || rc.senseTerrainTile(goToSpot).equals(TerrainTile.OFF_MAP))
        {
            Direction dir = directions[rand.nextInt(8)];
            goToSpot = goToSpot.add(dir);
        }
    }

    public void run()
    {
        while (true)
        {
            try
            {
                Thor thor = new Thor(rc, true);
                thor.run();
                /*
                if (!arrived)
                {
                    Utilities.MoveMapLocation(rc, goToSpot, false);
                    if (rc.getLocation().equals(goToSpot))
                    {
                        arrived = true;
                    }
                }
                else
                {
                    GameObject[] nearByBots = rc.senseNearbyGameObjects(Robot.class, 16, rc.getTeam());
                    while (rc.senseCowsAtLocation(rc.getLocation()) < 100 || nearByBots.length > 0)
                    {
                        Direction dir = directions[rand.nextInt(8)];
                        Utilities.MoveDirection(rc, dir, true);
                        nearByBots = rc.senseNearbyGameObjects(Robot.class, 16, rc.getTeam());
                        rc.yield();
                    }

                    while (!rc.isActive())
                    {
                        rc.yield();
                    }
                    if (rc.isActive())
                    {
                        rc.construct(RobotType.PASTR);
                    }
                }
                */
            } catch (Exception e)
            {
                e.printStackTrace();
                System.out.println("SCV Exception");
            }
            rc.yield();
        }
    }
}
