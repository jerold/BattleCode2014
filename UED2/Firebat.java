package UED2;

import battlecode.common.*;

/**
 * Created by fredkneeland on 1/13/14.
 */
public class Firebat {

    RobotController rc;
    public Firebat(RobotController rc)
    {
        this.rc = rc;
    }

    public void run()
    {
        while (true)
        {
            try
            {
                if (rc.isActive())
                {
                    Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());

                    if (rc.canMove(dir))
                    {
                        rc.move(dir);
                        rc.selfDestruct();
                    }
                }
            } catch(Exception e){}
        }

    }


}
