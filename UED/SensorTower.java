package UED;

import battlecode.common.*;

import java.util.Random;

/**
 * Created by fredkneeland on 1/9/14.
 */
public class SensorTower
{
    RobotController rc;
    int width, height, corner;
    int[] radii;
    MapLocation target;
    Random rand = new Random();
    Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};

    public SensorTower(RobotController rc)
    {
        this.rc = rc;
        corner = Utilities.findBestCorner(rc);
        width = rc.getMapWidth();
        height = rc.getMapHeight();
        int[] radii = {15, 13, 11, 9, 7};
        this.radii = radii;
    }

    public void run()
    {
        while(true)
        {
            if(rc.getType() == RobotType.SOLDIER)
            {

                    try
                    {
                        /*
                        switch(corner)
                        {
                            case 1:
                                target = new MapLocation(5, 5);
                                break;
                            case 2:
                                target = new MapLocation(rc.getMapWidth() - 6, 5);
                                break;
                            case 3:
                                target = new MapLocation(5, rc.getMapHeight() - 6);
                                break;
                            default:
                                target = new MapLocation(rc.getMapWidth() - 6, rc.getMapHeight() - 6);
                                break;
                        }

                        Direction dir = directions[rand.nextInt(8)];
                        // make sure we don't try to build on a void space
                        while (rc.senseTerrainTile(target).equals(TerrainTile.VOID))
                        {
                            target = target.add(dir);
                            dir = directions[rand.nextInt(8)];
                        }
                        */
                        target = Utilities.spotOfSensorTower(rc);
                        Utilities.MoveMapLocation(rc, target, true);

                        if(rc.isActive())
                        {
                            rc.construct(RobotType.NOISETOWER);
                        }
                    }
                    catch(Exception e){}

            }
            else
            {
                fireArcs();
            }

            rc.yield();
        }
    }

    private void fireArcs()
    {
        for(int k = 0; k < radii.length; k++)
        {
            for(int a = 0; a <= radii[k]; a+= 4)
            {
                while(!rc.isActive()){}
                try
                {
                    switch(corner)
                    {
                        case 1:
                            rc.attackSquare(new MapLocation(a, radii[k]));
                            break;
                        case 2:
                            rc.attackSquare(new MapLocation(width - a + 1, radii[k]));
                            break;
                        case 3:
                            rc.attackSquare(new MapLocation(a, height - radii[k] + 1));
                            break;
                        default:
                            rc.attackSquare(new MapLocation(width - a + 1, height - radii[k] + 1));
                            break;
                    }
                }
                catch(Exception e){}

                rc.yield();

                while(!rc.isActive()){}
                try
                {
                    switch(corner)
                    {
                        case 1:
                            rc.attackSquare(new MapLocation(radii[k], a));
                            break;
                        case 2:
                            rc.attackSquare(new MapLocation(width - radii[k] + 1, a));
                            break;
                        case 3:
                            rc.attackSquare(new MapLocation(radii[k], height - a + 1));
                            break;
                        default:
                            rc.attackSquare(new MapLocation(width - radii[k] + 1, height - a + 1));
                            break;
                    }
                }
                catch(Exception e){}

                rc.yield();
            }

            while(!rc.isActive()){}
            try
            {
                switch(corner)
                {
                    case 1:
                        rc.attackSquare(new MapLocation(radii[k], radii[k]));
                        break;
                    case 2:
                        rc.attackSquare(new MapLocation(width - radii[k] + 1, radii[k]));
                        break;
                    case 3:
                        rc.attackSquare(new MapLocation(radii[k], height - radii[k] + 1));
                        break;
                    default:
                        rc.attackSquare(new MapLocation(width - radii[k] + 1, height - radii[k] + 1));
                        break;
                }
            }
            catch(Exception e){}

            rc.yield();
        }
    }
}
