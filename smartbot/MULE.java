package smartbot;

import battlecode.common.*;

/**
 * Created by fredkneeland on 1/8/14.
 */
public class MULE {
    RobotController rc;
    int corner;
    public MULE(RobotController rc)
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
                switch(corner)
                {
                    case 1:
                        Utilities.MoveMapLocation(rc, new MapLocation(2, 2), true);
                        break;
                    case 2:
                        Utilities.MoveMapLocation(rc, new MapLocation(rc.getMapWidth() - 3, 2), true);
                        break;
                    case 3:
                        Utilities.MoveMapLocation(rc, new MapLocation(2, rc.getMapHeight() - 3), true);
                        break;
                    default:
                        Utilities.MoveMapLocation(rc, new MapLocation(rc.getMapWidth() - 3, rc.getMapHeight() - 3), true);
                        break;
                }

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
