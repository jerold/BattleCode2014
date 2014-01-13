package smartbot;

import battlecode.common.*;

import java.util.Random;

public class SmartTower
{
    RobotController rc;
    int corner;
    MapLocation target;
    
    public SmartTower(RobotController rc)
    {
        this.rc = rc;
        corner = Utilities.findBestCorner(rc);
    }

    public void run()
    {
        while(true)
        {
            if(rc.getType() == RobotType.SOLDIER)
            {
                    try
                    {
                        target = Utilities.spotOfSensorTower(rc);
                        Utilities.MoveMapLocation(rc, target, true);

                        if(rc.isActive())
                        {
                            rc.construct(RobotType.NOISETOWER);
                        }
                    }
                    catch(Exception e){}

            }
            rc.yield();
        }
    }
}
