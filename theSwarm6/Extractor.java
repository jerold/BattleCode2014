package theSwarm6;

import battlecode.common.*;

public class Extractor
{
    RobotController rc;
    MapLocation towerSpot;
    towerPastrRequest request;
    int start;

    public Extractor(RobotController rc, int type, MapLocation target)
    {
        this.rc = rc;
        towerSpot = target;
        start = Clock.getRoundNum();
        request = new towerPastrRequest(rc);
        
        rc.setIndicatorString(0, "Extractor");
    }

    public void run()
    {
        while (true)
        {
            try
            {
                if (rc.isActive())
                {
                    if (rc.getLocation().x == towerSpot.x && rc.getLocation().y == towerSpot.y)
                    {
                        rc.construct(RobotType.NOISETOWER);
                    }
                    else
                    {
                        Movement.MoveMapLocation(rc, towerSpot, false, false);
                        if(Clock.getRoundNum() - start > 100)
                        {
                        	request.abandonPath(towerSpot);
                        	new Larva(rc).run();
                        }
                    }
                }
            } catch (Exception e) {}
        }
    }
}
