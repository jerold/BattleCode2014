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
    	MapLocation target;
    	
        while(true)
        {
            if(rc.getType() == RobotType.SOLDIER)
            {
            	try
                {
                    target = Utilities.spotOfPastr(rc);
                    Utilities.MoveMapLocation(rc, target, true);

                    if(rc.isActive())
                    {
                        rc.construct(RobotType.PASTR);
                    }
                }
                catch(Exception e){}
            }
        }
    }
}
