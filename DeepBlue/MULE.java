package DeepBlue;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

import java.util.Random;

/**
 * Created by fredkneeland on 1/8/14.
 */
public class MULE
{
    RobotController rc;
    int corner;
    MapLocation target;
    Random rand = new Random();
    Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
    Direction dir;

    public MULE(RobotController rc)
    {
        this.rc = rc;
        /*
        corner = Utilities.findBestCorner(rc);

        switch(corner)
        {
            case 1:
                target = new MapLocation(2, 2);
                break;
            case 2:
                target = new MapLocation(rc.getMapWidth() - 3, 2);
                break;
            case 3:
                target = new MapLocation(2, rc.getMapHeight() - 3);
                break;
            default:
                target = new MapLocation(rc.getMapWidth() - 3, rc.getMapHeight() - 3);
                break;
        }

        while (rc.senseTerrainTile(target).equals(TerrainTile.VOID))
        {
            dir = directions[rand.nextInt(8)];
            target = target.add(dir);
        }
        */

        target = Utilities.spotOfPastr(rc);

        rc.setIndicatorString(0, "MULE");

    }

    public void run()
    {
        while(true)
        {
            if(rc.getType() == RobotType.SOLDIER)
            {
                //Utilities.AvoidEnemiesMoveMapLocation(rc, target, true);
                Utilities.MoveMapLocation(rc, target, true);

                if(rc.isActive())
                {
                    try
                    {
                        rc.construct(RobotType.PASTR);
                    }
                    catch (Exception e){}
                }
            }
        }
    }
}
