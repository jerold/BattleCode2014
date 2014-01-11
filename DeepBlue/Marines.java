package DeepBlue;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;

/**
 * Created by fredkneeland on 1/9/14.
 */
public class Marines
{
    RobotController rc;
    MapLocation target;
    boolean gotToTarget = false;

    public Marines(RobotController rc)
    {
        this.rc = rc;
        int corner = Utilities.findBestCorner(rc);
        switch(corner)
        {
            case 1:
                target = new MapLocation(5, 7);
                break;
            case 2:
                target = new MapLocation(rc.getMapWidth() - 6, 7);
                break;
            case 3:
                target = new MapLocation(5, rc.getMapHeight() - 8);
                break;
            default:
                target = new MapLocation(rc.getMapWidth() - 6, rc.getMapHeight() - 8);
                break;
        }

        Direction dir = target.directionTo(rc.senseEnemyHQLocation());
        target.add(dir);
    }

    public void run()
    {
        while (true)
        {
            try
            {
                if (rc.isActive())
                {
                    Robot[] enemies = rc.senseNearbyGameObjects(Robot.class,10,rc.getTeam().opponent());
                    Robot[] enemies2 = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());
                    if (Utilities.fightMode(rc))
                    {

                    }
                    else if (rc.getLocation().equals(target))
                    {
                        gotToTarget = true;
                    }
                    else if (!gotToTarget)
                    {

                        Utilities.MoveMapLocation(rc, target, false);
                    }
                }
            } catch(Exception e){}
            rc.yield();
        }
    }
}
