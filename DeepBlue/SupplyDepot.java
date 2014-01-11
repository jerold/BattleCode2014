package DeepBlue;

import battlecode.common.RobotController;
import battlecode.common.RobotType;


/**
 * Created by fredkneeland on 1/8/14.
 */
public class SupplyDepot {
    RobotController rc;
    boolean moved;

    public SupplyDepot(RobotController rc)
    {
        this.rc = rc;
        moved = false;
    }

    public void run()
    {
        while(true)
        {
            try
            {
                if(!moved)
                {
                    if(rc.isActive())
                    {
                        Utilities.MoveDirection(rc, rc.getLocation().directionTo(rc.senseEnemyHQLocation()).opposite(), false);
                        moved = true;
                    }
                }
                else
                {
                    if(rc.isActive())
                    {
                        rc.construct(RobotType.PASTR);
                    }
                }
            }
            catch(Exception e){}
            rc.yield();
        }
    }
}
