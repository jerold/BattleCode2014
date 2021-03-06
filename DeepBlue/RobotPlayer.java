package DeepBlue;

import battlecode.common.RobotController;
import battlecode.common.RobotType;

/**
 * Created by Jerold Albertson on 1/7/14.
 *
 */
public class RobotPlayer
{

    public static void run(RobotController rc)
    {
        //int a = 3/0;
        while (true) {
            try {
                if (rc.getType() == RobotType.HQ)
                {
                    rc.wearHat();
                    Headquarter.run(rc);
                }
                else if (rc.getType() == RobotType.SOLDIER) Soldiers.run(rc);
                else Structures.run(rc);
            } catch(Exception e) {
                e.printStackTrace();
                rc.breakpoint();
            }
            rc.yield();
        }
    }
}
